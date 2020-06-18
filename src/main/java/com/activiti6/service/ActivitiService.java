package com.activiti6.service;


import com.activiti6.Enums.BpmsActivityTypeEnum;
import com.activiti6.utils.DelAllFile;
import com.activiti6.utils.PathUtil;
import com.activiti6.utils.UtilMisc;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.tomcat.jni.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ActivitiService {
        @Autowired
        public RepositoryService repositoryService;
     @Autowired
        public HistoryService historyService;
     @Autowired
     public SpringProcessEngineConfiguration processEngine;
    /**生成当前任务节点流程图片PNG
     * @param PROC_INST_ID_ //流程实例ID
     * @param FILENAME 		//图片名称
     * @throws IOException
     */
    public void createXmlAndPngAtNowTask(String PROC_INST_ID_,String fileName) throws IOException{
        String path=PathUtil.getClasspath()+"processes/";
        DelAllFile.delFolder(path); 	//生成先清空之前生成的文件
        InputStream in = getResourceDiagramInputStream(PROC_INST_ID_);
        File dir = new File(path);
        if (!dir.exists()) {// 判断目录是否存在
            dir.mkdir();
        }
        FileOutputStream downloadFile = new FileOutputStream(path+fileName);
          int index;
        byte[] bytes = new byte[1024];
        while ((index = in.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        in.close();
    }
    /**获取当前任务流程图片的输入流
     * @param PROC_INST_ID_	//流程实例ID
     * @return
     */
    private InputStream getResourceDiagramInputStream(String PROC_INST_ID_){
        try {
            HistoricProcessInstance hip = historyService.createHistoricProcessInstanceQuery().processInstanceId(PROC_INST_ID_).singleResult(); 			//获取历史流程实例
            List<HistoricActivityInstance> hai = historyService.createHistoricActivityInstanceQuery().processInstanceId(PROC_INST_ID_)
                    .orderByHistoricActivityInstanceId().asc().list();	//获取流程中已经执行的节点，按照执行先后顺序排序
            List<String> executedActivityIdList = new ArrayList<String>();						// 构造已执行的节点ID集合
            for (HistoricActivityInstance activityInstance : hai) {
                executedActivityIdList.add(activityInstance.getActivityId());
            }
            BpmnModel bpmnModel = repositoryService.getBpmnModel(hip.getProcessDefinitionId()); // 获取bpmnModel
            List<String> flowIds = this.getExecutedFlows(bpmnModel, hai);						// 获取流程已发生流转的线ID集合
            ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);	//使用默认配置获得流程图表生成器，并生成追踪图片字符流
            return imageStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private List<String> getExecutedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> flowIdList = new ArrayList<String>();					//流转线ID集合
        List<FlowNode> historicFlowNodeList = new LinkedList<FlowNode>();	//全部活动实例
        List<HistoricActivityInstance> finishedActivityInstanceList = new LinkedList<HistoricActivityInstance>();	//已完成的历史活动节点
        for(HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            historicFlowNodeList.add((FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true));
            if(historicActivityInstance.getEndTime() != null) {
                finishedActivityInstanceList.add(historicActivityInstance);
            }
        }
        /**遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的*/
        FlowNode currentFlowNode = null;
        for(HistoricActivityInstance currentActivityInstance : finishedActivityInstanceList) {
            /**获得当前活动对应的节点信息及outgoingFlows信息*/
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();
            /**
             * 遍历outgoingFlows并找到已流转的
             * 满足如下条件任务已流转：
             * 1.当前节点是并行网关或包含网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最近的流转节点视为有效流转
             */
            FlowNode targetFlowNode = null;
            if(BpmsActivityTypeEnum.PARALLEL_GATEWAY.getType().equals(currentActivityInstance.getActivityType())
                    || BpmsActivityTypeEnum.INCLUSIVE_GATEWAY.getType().equals(currentActivityInstance.getActivityType())) {
                for(SequenceFlow sequenceFlow : sequenceFlowList) { //遍历历史活动节点，找到匹配Flow目标节点的
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if(historicFlowNodeList.contains(targetFlowNode)) {
                        flowIdList.add(sequenceFlow.getId());
                    }
                }
            }else{
                List<Map<String, String>> tempMapList = new LinkedList<Map<String,String>>();
                for(SequenceFlow sequenceFlow : sequenceFlowList) {	 //遍历历史活动节点，找到匹配Flow目标节点的
                    for(HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if(historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            tempMapList.add(UtilMisc.toMap("flowId", sequenceFlow.getId(), "activityStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime())));
                        }
                    }
                }
                String flowId = null;
                for (Map<String, String> map : tempMapList) {
                    flowId = map.get("flowId");
                    flowIdList.add(flowId);
                }

            }
        }
        return flowIdList;
    }

}

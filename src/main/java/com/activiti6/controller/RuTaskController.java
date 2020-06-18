package com.activiti6.controller;

import com.activiti6.Parms.ApiResult;
import com.activiti6.Parms.CompletParm;
import com.activiti6.dto.Runtask.HistoryTaskComment;
import com.activiti6.dto.Runtask.TaskList;
import com.activiti6.service.ActivitiService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.bcel.generic.RETURN;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RuTaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeServiceImpl runtimeService;
    @Autowired
    private HistoryService historyService;



    @GetMapping("/ru/{userid}")
    public ApiResult<Object> GetTaskByUserid(@PathVariable String userid,Integer page,Integer limit) {
        List<TaskList> TaskLists = new ArrayList<>();
        if(StringUtils.isEmpty(userid)) return ApiResult.fail("参数错误");
        try {
           List<Task> tasks = taskService.createTaskQuery()
                   .taskAssignee(userid)
                   .orderByTaskCreateTime().desc()
                   .listPage(page,limit);
            TaskLists= TaskConvert(tasks);
        } catch (Exception ex) {
            return ApiResult.fail(ex.getMessage());
        }
        return ApiResult.SUCCESS(TaskLists);
    }

    @GetMapping("/ru/{userid}/{deployId}")
    public ApiResult<Object> GetTaskByUserid(@PathVariable String userid,@PathVariable String deployId,Integer page,Integer limit) {
        //根据流程获取该流程的所有流程实例
        List<TaskList> TaskLists = new ArrayList<>();
        if(StringUtils.isEmpty(userid)) return ApiResult.fail("参数错误");
        try {
            List<Task> tasks = taskService.createTaskQuery()
                    .taskAssignee(userid)
                    .deploymentId(deployId)
                    .orderByTaskCreateTime().desc()
                    .listPage(page,limit);
            TaskLists= TaskConvert(tasks);
        } catch (Exception ex) {
            return ApiResult.fail(ex.getMessage());
        }
        return ApiResult.SUCCESS(TaskLists);
    }



    @PostMapping(value = "/ru/complete",produces = "application/json;charset=UTF-8")
    public ApiResult<Object> completeByUserid(@RequestBody CompletParm parm) {

       try{
           Task task=taskService.createTaskQuery().taskId(parm.getTaskId()).singleResult();
           //利用任务对象，获取流程实例id
           String processInstancesId=task.getProcessInstanceId();
            taskService.addComment(task.getId(),processInstancesId,parm.getMessage());
             taskService.complete(parm.getTaskId(),parm.getData());
       }catch (Exception ex){
           return ApiResult.fail(ex.getMessage());
       }
       return ApiResult.SUCCESS();
    }

    //将任务委派
    @PostMapping(value = "/ru/delegate")
    public ApiResult<Object> delegate(String taskId,String userid) {
        try{
            taskService.setAssignee(taskId,userid);
        }catch (Exception ex){
            return ApiResult.fail(ex.getMessage());
        }
        return ApiResult.SUCCESS();
    }
    public List<TaskList> TaskConvert( List<Task> tasks ){
        List<TaskList> TaskLists = new ArrayList<>();
        for (Task item :tasks
        ) {

            TaskList ta= new TaskList();
            ta.setAssignee(item.getAssignee());
            ta.setCreateTime(item.getCreateTime());
            ta.setId(item.getId());
            ta.setParentTaskId(item.getParentTaskId());
            ta.setProcessInstanceId(item.getProcessInstanceId());
            ta.setName(item.getName());
            ta.setBusinessKey(GetBusinessKey(item.getId()));
            //获取历史流程信息
            List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//历史任务表查询
                    .processInstanceId(ta.getProcessInstanceId())//使用流程实例ID查询

                    .list();
            htiList=htiList.stream().filter(u->u.getEndTime()!=null).collect(Collectors.toList());

            if(htiList!=null && htiList.size()>0){
                List<HistoryTaskComment> HISTORY=new ArrayList<>();
                for(HistoricTaskInstance hti:htiList){
                    String htaskId = hti.getId();
                    List<Comment> List = taskService.getTaskComments(htaskId);//对用历史完成后的任务ID

                    for (Comment u:List
                         ) {
                        HistoryTaskComment n= HistoryTaskComment.builder()
                                .FullMessage(u.getFullMessage())
                                .TaskId(u.getTaskId())
                                .Id(u.getId())
                                .ProcessInstanceId(u.getProcessInstanceId())
                                .Type(u.getType())
                                .UserId(u.getUserId())
                                .build();
                        HISTORY.add(n);
                    }

                }
                ta.setComment(HISTORY);
            }
            TaskLists.add(ta);
        }
        return TaskLists;
    }
    public  String GetBusinessKey(String taskId){
        Task task= (Task) taskService.createTaskQuery().taskId(taskId).singleResult();
        //2  通过任务对象获取流程实例
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String key=   pi.getBusinessKey();

       String BusinessKey= key.substring(key.lastIndexOf(":")+1
               ,key.length());

        return BusinessKey;
    }




}

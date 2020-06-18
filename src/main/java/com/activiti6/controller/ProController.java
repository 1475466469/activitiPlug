package com.activiti6.controller;

import com.activiti6.Parms.ApiResult;
import com.activiti6.Parms.StartProcessParm;
import com.jayway.jsonpath.JsonPath;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class ProController {



    @Autowired
    private RuntimeServiceImpl runtimeService;

    @Autowired
    private SpringProcessEngineConfiguration processEngineConfiguration;

    //开启实例流程
    @PostMapping(value = "pro/start",produces = "application/json;charset=UTF-8")
    public ApiResult<Object> StartProcess(@RequestBody StartProcessParm parm) {
        ProcessInstance processInstance=null;
        try {
            String businessKey = parm.getKey() + ":" + parm.getBusinessId();
             processInstance = runtimeService.startProcessInstanceByKey(parm.getKey(), businessKey, parm.getData());
        }catch (Exception ex){
            ApiResult.fail(ex.getMessage());
        }

      return   ApiResult.SUCCESS(processInstance.getProcessInstanceId());
    }

    //删除流程实例
    @PostMapping(value = "pro/del")

    public ApiResult<Object> DelProcess(String pid) {
        try {
            runtimeService.deleteProcessInstance(pid,"11");
        }catch (Exception ex){
            return   ApiResult.fail(ex.getMessage());
        }
        return   ApiResult.SUCCESS();
    }



















}

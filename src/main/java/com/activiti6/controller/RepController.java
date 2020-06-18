package com.activiti6.controller;


import com.activiti6.Parms.ApiResult;
import com.activiti6.service.ActivitiService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.junit.platform.commons.function.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RepController {
    @Autowired
    public ActivitiService activitiService;

    @GetMapping("/re/img/{id}")
    public ApiResult<Object> GetPROCImages(@PathVariable String id, HttpServletRequest request) throws IOException {
        String filePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/processes/"+id+".png";
        try {
            activitiService.createXmlAndPngAtNowTask(id,id+".png");
        }catch (Exception ex){
            ApiResult.fail(ex.getMessage());
        }
        return ApiResult.SUCCESS(filePath);
    }

}

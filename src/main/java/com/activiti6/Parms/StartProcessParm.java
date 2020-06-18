package com.activiti6.Parms;

import lombok.Data;

import java.util.Map;

@Data
public class StartProcessParm {

    //流程Key
    private String Key;

    //业务表主键

    private String businessId;




    //附带流程变量
    private Map<String,Object>  data;

}

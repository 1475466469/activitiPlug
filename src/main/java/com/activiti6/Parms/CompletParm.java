package com.activiti6.Parms;

import lombok.Data;

import java.util.Map;

@Data
public class CompletParm {
    private String taskId;
    private Map<String,Object> data;
    private String Message;

}

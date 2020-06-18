package com.activiti6.dto.Runtask;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TaskList implements Serializable {
    private String id;
    private String assignee;
    private String name;
    private String parentTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    private String processInstanceId;
    private String BusinessKey;

    //历史节点的处理结果
    private List<HistoryTaskComment> comment;


}

package com.activiti6.dto.Runtask;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HistoryTaskComment {
    private String Id;
    private  String UserId;
    private String TaskId;
    private String ProcessInstanceId;
    private String Type;
    private String FullMessage;

}

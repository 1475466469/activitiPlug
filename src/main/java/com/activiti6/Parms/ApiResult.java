package com.activiti6.Parms;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.net.httpserver.Authenticator;
import lombok.Builder;
import lombok.Data;
import org.apache.bcel.generic.RET;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public  class  ApiResult<T> implements Serializable {



    private   Integer code;
    private   String msg;
    private   T data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;

    public static ApiResult SUCCESS(Object data){
        return  result(200,"success",data);

    }
    public static ApiResult SUCCESS(){
        return  result(200,"success");

    }
    public static ApiResult fail(String msg){
        return  result(500,msg);

    }
    public static ApiResult fail(){
        return  result(500,"error");

    }




    public static ApiResult result(Integer apiCode,String msg,Object data){
        String message = msg;
        if (StringUtils.isNotBlank(msg)){
            message = msg;
        }
        return ApiResult.builder()
                .code(apiCode)
                .msg(message)
                .data(data)
                .time(new Date())
                .build();
    }
    public static ApiResult result(Integer apiCode,String msg){
        String message = msg;
        if (StringUtils.isNotBlank(msg)){
            message = msg;
        }
        return ApiResult.builder()
                .code(apiCode)
                .msg(message)
                .time(new Date())
                .build();
    }



}

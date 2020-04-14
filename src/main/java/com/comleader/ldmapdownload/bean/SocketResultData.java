package com.comleader.ldmapdownload.bean;

import com.comleader.ldmapdownload.util.OperationTypeEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName ResultData
 * @Description: Socket交互类
 * @Author zhanghang
 * @Date 2020/4/10
 * @Version V1.0
 **/
public class SocketResultData  implements Serializable {
    private int type; // 数据类型
    private String message; // 信息
    private String status; // 状态
    private Map<String,Object> body; // body信息

    public SocketResultData(OperationTypeEnum operationTypeEnum) {
        this.type = operationTypeEnum.getType();
        this.status = operationTypeEnum.getStatus();
    }

    public SocketResultData(OperationTypeEnum operationTypeEnum,Map<String,Object> body) {
        this.type = operationTypeEnum.getType();
        this.status = operationTypeEnum.getStatus();
        this.body = body;
    }

    public SocketResultData(OperationTypeEnum operationTypeEnum,String message,Map<String,Object> body) {
        this.type = operationTypeEnum.getType();
        this.status = operationTypeEnum.getStatus();
        this.message = message;
        this.body = body;
    }

    public SocketResultData(int type, String message, String status, Map<String, Object> body) {
        this.type = type;
        this.message = message;
        this.status = status;
        this.body = body;
    }

    public SocketResultData() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String staus) {
        this.status = staus;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
}

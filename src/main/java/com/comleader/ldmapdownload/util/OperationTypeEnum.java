package com.comleader.ldmapdownload.util;

/**
 * @ClassName OperationType
 * @Description: 操作类型枚举
 * @Author zhanghang
 * @Date 2020/4/10
 * @Version V1.0
 **/
public enum OperationTypeEnum {
    DOWNLOAD_READY(0, "success"), // 下载内容信息确认
    DOWNLOAD_MAP(1, "success"), // 开始下载
    SYS_SUCCESS(2, "success"), // 系统交互的消息(成功)
    SYS_ERROR(2, "error"), // 系统交互的消息(失败)
    DOWNLOAD_SCHEDULE(3, "success"), // 下载的进度
    DOWNLOAD_FINISHED(4, "success"), // 通知下载完成
    DOWNLOAD_STOP(5, "success"), // 停止下载的命令
    DOWNLOAD_BASEPATH(6, "success"); // 停止下载的命令
    Integer type;
    String status;

    OperationTypeEnum(Integer type, String status) {
        this.type = type;
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }}

package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint.pojo;

/**
 * @author francis
 * @version 2021-06-09
 */
public class ThreadPoolUpdateDTO {
    private Boolean success;
    private String errorMsg;

    public ThreadPoolUpdateDTO(String errorMsg) {
        this.success = Boolean.FALSE;
        this.errorMsg = errorMsg;
    }

    public ThreadPoolUpdateDTO() {
        this.success = Boolean.TRUE;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
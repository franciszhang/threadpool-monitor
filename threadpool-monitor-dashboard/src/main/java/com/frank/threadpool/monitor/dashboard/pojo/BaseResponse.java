package com.frank.threadpool.monitor.dashboard.pojo;

import lombok.Data;

/**
 * @author francis
 * @version 2021-07-19
 */
@Data
public class BaseResponse<T> {
    private Boolean success;
    private int code;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> isSuccess(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        baseResponse.setMsg("success");
        baseResponse.setData(data);
        return baseResponse;
    }

    public static <T> BaseResponse<T> isSuccess() {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        baseResponse.setMsg("success");
        return baseResponse;
    }

    public static <T> BaseResponse<T> isFail(int code, String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(false);
        baseResponse.setCode(code);
        baseResponse.setMsg(msg);
        return baseResponse;
    }

    public static <T> BaseResponse<T> isFail(String msg) {
        return isFail(-1, msg);
    }
}

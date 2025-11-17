package com.example.quotation.common;

/**
 * 统一API响应格式
 * 所有API接口统一使用此类作为响应结构
 * 
 * @param <T> 响应数据的类型
 * @author System
 */
public class ApiResponse<T> {
    /** 响应码：200表示成功，其他表示失败 */
    private int code;
    /** 响应消息 */
    private String msg;
    /** 响应数据 */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 创建错误响应
     * 
     * @param msg 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int c) {
        this.code = c;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String m) {
        this.msg = m;
    }

    public T getData() {
        return data;
    }

    public void setData(T d) {
        this.data = d;
    }
}

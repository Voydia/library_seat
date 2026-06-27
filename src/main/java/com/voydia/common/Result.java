package com.voydia.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;
/**
 * 构造方法：创建并初始化一个Result对象
 * @param code 响应状态码，通常用于表示请求处理状态
 * @param msg 响应消息，对处理结果的简要说明
 * @param data 响应数据，请求处理返回的具体数据内容
 */
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }
    public static <T> Result<T> error(int code,String msg){
        return new Result<>(400,msg,null);
    }
}

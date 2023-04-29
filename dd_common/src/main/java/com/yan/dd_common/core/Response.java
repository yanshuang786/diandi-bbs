package com.yan.dd_common.core;

import lombok.ToString;

import java.io.Serializable;

/**
 * @author yanshuang
 * @date 2023/3/31 16:50
 */
@ToString
public class Response<T> implements Serializable {
    private ResultCode resultCode;
    private T data;

    private int code;

    private String msg;

    private static final long serialVersionUID = 9527L;

    private Response() {
    }



    public Response(ResultCode resultCode) {
        this.resultCode = resultCode;
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    public Response msg(String msg){
        this.msg = msg;
        return this;
    }


    public Response<T> data(T data){
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }


    public String getMsg() {
        return msg;
    }
}

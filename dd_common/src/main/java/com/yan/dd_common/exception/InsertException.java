package com.yan.dd_common.exception;

import com.yan.dd_common.global.BaseMessageConf;
import com.yan.dd_common.global.ErrorCode;

import java.io.Serializable;

/**
 * @author yanshuang
 * @date 2023/4/30 22:18
 */
public class InsertException extends RuntimeException implements Serializable {

    /**
     * 异常状态码
     */
    private String code;

    public InsertException() {
        super(BaseMessageConf.INSERT_DEFAULT_ERROR);
        this.code = ErrorCode.INSERT_DEFAULT_ERROR;
    }

    public InsertException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.INSERT_DEFAULT_ERROR;
    }

    public InsertException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public InsertException(String message) {
        super(message);
        this.code = ErrorCode.INSERT_DEFAULT_ERROR;
    }

    public InsertException(String code, String message) {
        super(message);
        this.code = code;
    }

    public InsertException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}


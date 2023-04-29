package com.yan.dd_common.exception.exceptionType;

import com.yan.dd_common.global.BaseMessageConf;
import com.yan.dd_common.global.ErrorCode;

import java.io.Serializable;

/**
 * @author yanshuang
 * @date 2023/4/28 17:20
 */
public class QueryException extends RuntimeException implements Serializable {

    /**
     * 异常状态码
     */
    private String code;

    public QueryException() {
        super(BaseMessageConf.QUERY_DEFAULT_ERROR);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public QueryException(String message) {
        super(message);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public QueryException(String code, String message) {
        super(message);
        this.code = code;
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}


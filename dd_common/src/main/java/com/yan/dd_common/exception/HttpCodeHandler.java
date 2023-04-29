package com.yan.dd_common.exception;

/**
 * 返回状态码 接口
 *
 * @author yanshuang
 * @date 2023/4/25 13:45
 */
public interface HttpCodeHandler {

    long getCode();

    String getMessage();

}


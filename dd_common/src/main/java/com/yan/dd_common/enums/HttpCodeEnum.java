package com.yan.dd_common.enums;


import com.yan.dd_common.exception.HttpCodeHandler;

/**
 * 返回状态码
 *
 * @author yanshuang
 * @date 2021/12/3 9:44 下午
 */
public enum HttpCodeEnum implements HttpCodeHandler {
    // 数据操作错误定义
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PRAM_NOT_MATCH(400, "参数不正确"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "未登录或token过期，请登录！"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "没有找到相关数据");

    private long code;
    private String message;

    private HttpCodeEnum(long code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

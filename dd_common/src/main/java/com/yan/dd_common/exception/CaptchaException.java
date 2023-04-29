package com.yan.dd_common.exception;

/**
 * 验证码异常处理
 *
 * @author yanshuang
 * @date 2023/4/25 14:49
 */
public class CaptchaException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg)
    {
        super(msg);
    }
}

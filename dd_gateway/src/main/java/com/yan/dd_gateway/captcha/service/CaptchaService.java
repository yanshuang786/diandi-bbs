package com.yan.dd_gateway.captcha.service;

import com.yan.dd_common.core.R;
import com.yan.dd_common.exception.CaptchaException;

/**
 * @author yanshuang
 * @date 2023/4/25 13:57
 */
public interface CaptchaService {

    /**
     * 生成验证码
     * @return 验证码
     */
    R createCaptcha();

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;

}

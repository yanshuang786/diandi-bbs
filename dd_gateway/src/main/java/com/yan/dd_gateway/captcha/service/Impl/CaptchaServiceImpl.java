package com.yan.dd_gateway.captcha.service.Impl;

import com.google.code.kaptcha.Producer;
import com.yan.dd_common.constant.Constants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.exception.CaptchaException;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.Base64;
import com.yan.dd_common.utils.StringUtils;
import com.yan.dd_common.utils.uuid.IdUtils;
import com.yan.dd_gateway.captcha.service.CaptchaService;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author yanshuang
 * @date 2023/4/25 13:57
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisUtil redisUtil;

    public CaptchaServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    /**
     * 生成验证码
     * @return 验证码
     */
    @Override
    public R createCaptcha() {

        R r = R.success();
        // 查询是否开启验证码
        boolean captchaOnOff = true;
        r.put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff) {
            return r;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String code = null;

        // 生成验证码

        String capStr = code = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(capStr);

        redisUtil.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return R.error(e.getMessage());
        }

        r.put("uuid", uuid);
        r.put("img", Base64.encode(os.toByteArray()));
        return r;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) throws CaptchaException
    {
        if (StringUtils.isEmpty(code))
        {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid))
        {
            throw new CaptchaException("验证码已失效");
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisUtil.getCacheObject(verifyKey);
        redisUtil.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException("验证码错误");
        }
    }
}

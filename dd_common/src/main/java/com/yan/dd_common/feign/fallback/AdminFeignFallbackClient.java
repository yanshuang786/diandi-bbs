package com.yan.dd_common.feign.fallback;

import com.yan.dd_common.core.R;
import com.yan.dd_common.feign.AdminFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * 用户调用回调
 *
 * @author yanshuang
 * @date 2023/4/27 12:28
 */
@Component
@Slf4j
public class AdminFeignFallbackClient implements AdminFeignClient {

    @Override
    public R getAdminAuthInfo(String username) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return R.error("系统功能降级");
    }
}


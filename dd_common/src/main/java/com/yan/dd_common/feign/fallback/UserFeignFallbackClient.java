package com.yan.dd_common.feign.fallback;

import com.yan.dd_common.core.R;
import com.yan.dd_common.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanshuang
 * @date 2023/4/28 22:42
 */
@Component
@Slf4j
public class UserFeignFallbackClient implements UserFeignClient {

    @Override
    public R getUserAuthInfo(String username) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return R.error("系统功能降级");
    }
}


package com.yan.dd_common.feign;

import com.yan.dd_common.core.R;
import com.yan.dd_common.feign.fallback.UserFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yanshuang
 * @date 2023/4/28 22:41
 */
@FeignClient(value = "dd-web", fallback = UserFeignFallbackClient.class)
public interface UserFeignClient {

    /**
     * 根据用户名获得用户信息
     * @param username 用户名
     * @return 用户详情
     */
    @GetMapping("/system/user/authInfo")
    R getUserAuthInfo(@RequestParam("username") String username);
}


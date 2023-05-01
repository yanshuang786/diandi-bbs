package com.yan.dd_common.feign;

import com.yan.dd_common.core.R;
import com.yan.dd_common.feign.fallback.AdminFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yanshuang
 * @date 2023/4/27 12:21
 */
@FeignClient(value = "dd-admin", fallback = AdminFeignFallbackClient.class)
public interface AdminFeignClient {

    /**
     * 根据用户名获得用户信息
     * @param username 用户名
     * @return 用户详情
     */
    @GetMapping("/system/admin/authInfo")
    R getAdminAuthInfo(@RequestParam("username") String username);


    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/systemConfig/getSystemConfig", method = RequestMethod.GET)
    public String getSystemConfig();
}

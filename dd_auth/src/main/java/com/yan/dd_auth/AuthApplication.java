package com.yan.dd_auth;

import com.yan.dd_common.feign.AdminFeignClient;
import com.yan.dd_common.feign.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 认证服务：用户登陆服务
 *
 * @author yanshuang
 * @date 2023/3/29 16:18
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = { UserFeignClient.class, AdminFeignClient.class })
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}

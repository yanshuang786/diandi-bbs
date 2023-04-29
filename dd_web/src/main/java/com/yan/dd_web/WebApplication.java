package com.yan.dd_web;

import com.yan.dd_common.feign.AdminFeignClient;
import com.yan.dd_common.feign.UserFeignClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yanshuang
 * @date 2023/4/12 12:49
 */
@SpringBootApplication(scanBasePackages = {"com.yan.dd_common.redis","com.yan.dd_web.*"})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yan.bbs.mapper",
        "com.yan.bbs.service",
        "com.yan.bbs.service.Impl","com.yan.dd_common.redis","com.yan.dd_web.controller"})
@MapperScan(value = "com.yan.bbs.mapper")
@EnableFeignClients(basePackageClasses = {UserFeignClient.class, AdminFeignClient.class})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}

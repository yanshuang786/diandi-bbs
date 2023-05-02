package com.yan.dd_picture;

import com.yan.dd_common.feign.AdminFeignClient;
import com.yan.dd_common.feign.WebFeignClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author yanshuang
 * @date 2023/4/30 11:57
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableFeignClients(basePackageClasses = {WebFeignClient.class, AdminFeignClient.class})
@MapperScan("com.yan.dd_picture.mapper")
public class PictureApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(PictureApplication.class, args);
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}

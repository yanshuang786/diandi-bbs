package com.yan.sms;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yanshuang
 * @date 2023/5/1 16:47
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.yan.sms.config"})
@EnableDiscoveryClient
@EnableRabbit
@EnableFeignClients("com.yan.dd_common.feign.*")
@ComponentScan(basePackages = {
        "com.yan.dd_common.utils",
        "com.yan.dd_common.redis",
        "com.yan.dd_common.config",
        "com.yan.sms",
        "com.yan.dd_common.feign.fallback",
        "com.yan.bbs.mapper",
        "com.yan.bbs.service"
})
@MapperScan(value = "com.yan.bbs.mapper")
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }
}

package admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yanshuang
 * @date 2023/3/24 16:01
 */
@SpringBootApplication(scanBasePackages = {"com.yan.dd_common.utils","com.yan.dd_common.redis","admin.*","com.yan.bbs.mapper","com.yan.bbs.service","com.yan.bbs.service.Impl"})
@EnableDiscoveryClient
@EnableFeignClients("com.yan.dd_common.feign")
@ComponentScan(basePackages = {"com.yan.bbs.mapper",
        "com.yan.bbs.service",
        "com.yan.bbs.service.Impl",
        "com.yan.dd_common.redis",
        "com.yan.dd_common.config",
        "admin.controller.*"
})
@MapperScan(value = "com.yan.bbs.mapper")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}

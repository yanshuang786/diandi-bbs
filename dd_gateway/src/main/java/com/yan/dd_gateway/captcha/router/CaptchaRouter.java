package com.yan.dd_gateway.captcha.router;

import com.yan.dd_gateway.captcha.handler.CaptchaHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 验证码路由
 *
 * @author yanshuang
 * @date 2023/4/25 13:54
 */
@Configuration
public class CaptchaRouter {
    @Bean
    public RouterFunction<ServerResponse> captchaRouterFunction(CaptchaHandler captchaHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/dd-admin/captcha")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), captchaHandler::handle);
    }

}

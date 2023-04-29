package com.yan.dd_gateway.captcha.handler;

import com.yan.dd_common.core.R;
import com.yan.dd_gateway.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 验证码处理器
 *
 * @author yanshuang
 * @date 2023/4/25 13:55
 */
@Component
@RequiredArgsConstructor
public class CaptchaHandler implements HandlerFunction<ServerResponse> {

    @Autowired
    private CaptchaService captchaService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        R r;
        r = captchaService.createCaptcha();
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(r));
    }

}

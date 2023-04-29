package com.yan.dd_auth.controller;

import com.yan.dd_auth.model.dto.Oauth2TokenDto;
import com.yan.dd_auth.model.vo.UserVO;
import com.yan.dd_common.core.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;

/**
 * @author yanshuang
 * @date 2023/4/8 17:56
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class AuthController {

    private final TokenEndpoint tokenEndpoint;

    public AuthController(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public R postAccessToken(Principal principal, @RequestBody UserVO userVO) throws HttpRequestMethodNotSupportedException {

        // TODO 微信公众号扫码登陆时
        HashMap<String, String> parameters = new HashMap<>(8);
        parameters.put("grant_type","password");
        parameters.put("username",userVO.getUsername());
        parameters.put("password", userVO.getPassword());
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();

        assert oAuth2AccessToken != null;
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        return R.success(oauth2TokenDto);
    }

}

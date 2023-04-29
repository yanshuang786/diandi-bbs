package com.yan.dd_auth.config;

import com.yan.dd_auth.userdetails.admin.AdminUserDetails;
import com.yan.dd_auth.userdetails.web.WebUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/4/9 15:01
 */
@Configuration
@Slf4j
public class JwtTokenConfig {

    /**
     * jwt内容增强器
     *
     * 这里我们把用户的id添加到jwt当中
     *
     * @return tokenEnhancer
     */
    @Bean
    public TokenEnhancer tokenEnhancer(){
        return (accessToken,authentication)->{
            Object principal = authentication.getUserAuthentication().getPrincipal();
            Map<String, Object> info = new HashMap<>(8);
            Object userDetailsObject = authentication.getPrincipal();
            if(principal instanceof WebUserDetails) {
                log.info("{}", "this is UserDetailsService");
                WebUserDetails webUserDetails = (WebUserDetails) userDetailsObject;
                //把用户ID设置到JWT中
                info.put("user", webUserDetails.getUser());
            } else if(principal instanceof AdminUserDetails) {
                log.info("{}", "this is AdminDetailsService");
                AdminUserDetails securityUser = (AdminUserDetails) userDetailsObject;
                //把管理员ID设置到JWT中
                info.put("admin", securityUser.getAdmin());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
            return accessToken;
        };
    }


    @Bean
    public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter){
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /**
     * 配置该类用于TokenStore内部token和jwt的相互转换
     * //jwt转换器使用RSA非对称加密
     *
     * @return  加密规则
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        //设置对称签名
        // 配置秘钥（非对称签名）
        accessTokenConverter.setKeyPair(keyPair());
        return accessTokenConverter;
    }




    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥) RSA
     *
     * keytool -genkeypair -alias oauth2 -keyalg RSA -keypass oauth2 -keystore oauth2.jks -storepass oauth2
     * 生成rsa证书，-storepass oauth2 ，-storepass后面的oauth2是密码
     *
     * -alias:密钥的别名
     * -keyalg:使用的hash算法
     * -keypass:密钥的访问密码
     * -keystore:密钥库文件名，oauth2.jks保存了生成的证书
     * -storepass:密钥库的访问密码
     */
    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }


}


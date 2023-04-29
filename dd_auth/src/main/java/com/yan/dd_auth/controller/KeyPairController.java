package com.yan.dd_auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/3/30 21:10
 */
@RestController
@Slf4j
public class KeyPairController {

    @Autowired
    private KeyPair keyPair;

    /**
     * 获取RSA公钥地址
     *
     * @return Map<String, Object>
     */
    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key1 = new RSAKey.Builder(publicKey).build();
        PrivateKey privateKey = keyPair.getPrivate();
        log.info("publicKey:{},privateKey:{}",publicKey,privateKey);

        return new JWKSet(key1).toJSONObject();
    }
}


package com.yan.dd_auth.config;

import com.yan.dd_auth.extension.MoreAuthenticatedUserDetailsServiceImpl;
import com.yan.dd_auth.userdetails.admin.AdminDetailsServiceImpl;
import com.yan.dd_auth.userdetails.web.UserDetailsServiceImpl;
import com.yan.dd_auth.utils.ClientEnums;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Security配置类
 *
 * @author yanshuang
 * @date 2023/3/29 16:46
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    private final AdminDetailsServiceImpl adminDetailsService;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AdminDetailsServiceImpl adminDetailsService) {
        this.userDetailsService = userDetailsService;
        this.adminDetailsService = adminDetailsService;
    }

    /**
     * 授权码模式在浏览器地址栏发起请求来获取 code
     * .anyRequest().authenticated() 必须对该请求进行认证拦截，发现用户没有登陆的时候会弹出登陆框, 从而让用户输入用户名和密码进行登陆, 若是对该请求进行放行, 则登陆页无法弹出, 并抛出 InsufficientAuthenticationException
     * .httpBasic() 因为用户未登陆访问了受保护的资源, 所以还要开启 httpBasic 进行简单认证, 否则会抛出 AccessDeniedException 异常,
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/rsa/publicKey").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
        ;
    }

    /**
     * 注入一个认证管理器, 自身不实现身份验证, 而是逐一向认证提供者进行认证, 直到某一个认证提供者能够成功验证当前用户的身份
     *
     * AuthenticationManager(认证管理器接口) 的默认实现类 ProviderManager, 管理多个 AuthenticationProvider(认证提供者)
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * 自定义AuthenticationProvider
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider= new DaoAuthenticationProvider();
        Map<String, UserDetailsService> userDetailsMap = new HashMap<>();
        userDetailsMap.put(ClientEnums.ADMIN_CLIENT.getName(), adminDetailsService);
        userDetailsMap.put(ClientEnums.USER_CLIENT.getName(), userDetailsService);
        provider.setUserDetailsService(new MoreAuthenticatedUserDetailsServiceImpl(userDetailsMap));
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
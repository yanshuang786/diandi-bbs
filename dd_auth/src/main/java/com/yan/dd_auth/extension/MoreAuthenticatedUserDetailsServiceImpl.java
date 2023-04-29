package com.yan.dd_auth.extension;

import com.yan.dd_auth.utils.RequestUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * UserDetailsService的选择
 *
 * @author yanshuang
 * @date 2023/4/29 12:51
 */
@NoArgsConstructor
@Slf4j
public class MoreAuthenticatedUserDetailsServiceImpl implements UserDetailsService {

    /**
     * 客户端ID和用户服务 UserDetailService 的映射
     */
    private Map<String, UserDetailsService> userDetailsServiceMap;

    public MoreAuthenticatedUserDetailsServiceImpl(Map<String, UserDetailsService> userDetailsServiceMap) {
        Assert.notNull(userDetailsServiceMap, "userDetailsService cannot be null.");
        this.userDetailsServiceMap = userDetailsServiceMap;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = RequestUtils.getClientId();
        log.info("ClientId: {}", clientId);
        Assert.notNull(clientId, "client cannot be null.");
        UserDetailsService userDetailsService = userDetailsServiceMap.get(clientId);
        return userDetailsService.loadUserByUsername(username);
    }
}

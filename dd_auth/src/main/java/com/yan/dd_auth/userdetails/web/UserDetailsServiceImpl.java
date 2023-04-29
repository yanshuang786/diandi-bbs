package com.yan.dd_auth.userdetails.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.User;
import com.yan.dd_common.enums.DStatus;
import com.yan.dd_common.exception.ServiceException;
import com.yan.dd_common.feign.UserFeignClient;
import com.yan.dd_common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author yanshuang
 * @date 2023/3/30 19:30
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFeignClient userFeignClient;

    public UserDetailsServiceImpl(@Qualifier("com.yan.dd_common.feign.UserFeignClient") UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        R adminAuthInfo = userFeignClient.getUserAuthInfo(username);
        // 解析用户数据
        String data = JSONArray.toJSONString(adminAuthInfo.get("data"));
        User user = JSONObject.parseObject(data, User.class);

        log.info(String.valueOf(adminAuthInfo));
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        else if (DStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        else if (DStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        return new WebUserDetails(user.getUserId(), user);
    }


}
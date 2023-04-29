package com.yan.dd_auth.userdetails.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.enums.DStatus;
import com.yan.dd_common.exception.ServiceException;
import com.yan.dd_common.feign.AdminFeignClient;
import com.yan.dd_common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author yanshuang
 * @date 2023/4/28 22:39
 */
@Service
@Slf4j
public class AdminDetailsServiceImpl implements UserDetailsService {

    private final AdminFeignClient adminFeignClient;

    public AdminDetailsServiceImpl(@Qualifier("com.yan.dd_common.feign.AdminFeignClient") AdminFeignClient adminFeignClient) {
        this.adminFeignClient = adminFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        R userAuthInfo = adminFeignClient.getAdminAuthInfo(username);
        String data = JSONArray.toJSONString(userAuthInfo.get("data"));
        Admin admin = JSONObject.parseObject(data, Admin.class);
        log.info("管理员信息：{}", userAuthInfo);
        if (StringUtils.isNull(admin)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        else if (DStatus.DISABLE.getCode().equals(admin.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }

        return new AdminUserDetails(admin.getAdminId(), admin);
    }
}

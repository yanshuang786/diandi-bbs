package com.yan.dd_common.utils;

import com.alibaba.fastjson.JSONObject;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/4/27 14:13
 */
public class SecurityUtils {

    public static Long getLoginAdminId() {
        return getLoginAdmin().getAdminId();
    }

    public static String getUsername() {
        return getLoginAdmin().getAdminName();
    }

    public static Admin getLoginAdmin() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String user = request.getHeader("user");
            return JSONObject.parseObject(String.valueOf(JSONObject.parseObject(user, Map.class).get("admin")), Admin.class);
        }
        return null;
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }


    public static User getLoginUser() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String user = request.getHeader("user");
            return JSONObject.parseObject(String.valueOf(JSONObject.parseObject(user, Map.class).get("user")), User.class);
        }
        return null;
    }


}

package com.yan.dd_auth.utils;

import lombok.Getter;

/**
 * @author yanshuang
 * @date 2023/4/29 13:06
 */
public enum ClientEnums {
    /**
     *
     */
    USER_CLIENT("web-password", "后台客户端"),
    ADMIN_CLIENT("admin-password", "web端客户端");

    @Getter
    public String name;

    @Getter
    public String desc;

    ClientEnums(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}

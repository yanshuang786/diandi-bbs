package com.yan.bbs.service;

import com.yan.dd_common.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yanshuang
 * @date 2023/4/25 16:56
 */
@Service
public class SysPermissionService {

    private final RoleService roleService;

    private final MenuService menuService;

    public SysPermissionService(MenuService menuService, RoleService roleService) {
        this.menuService = menuService;
        this.roleService = roleService;
    }

    /**
     * 获取角色数据权限
     *
     * @param admin 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(Admin admin) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
        if (admin.isAdmin()) {
            roles.add("admin");
        }
        else {
            // 根据用户的角色id
            roles.addAll(roleService.selectRolePermissionByAdminId(admin.getRoleId()));
        }
        return roles;
    }


    /**
     * 获取菜单数据权限
     *
     * @param admin 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(Admin admin) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (admin.isAdmin()) {
            perms.add("*:*:*");
        }
        else {
            perms.addAll(menuService.selectMenuPermsByAdmin(admin));
        }
        return perms;
    }

}

package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.SysRoleMenu;
import com.yan.bbs.mapper.RoleMenuMapper;
import com.yan.bbs.service.RoleMenuService;
import com.yan.dd_common.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 15:46
 */
@Service
public class RoleMenuServiceImpl extends SuperServiceImpl<RoleMenuMapper, SysRoleMenu> implements RoleMenuService {

    @Autowired
    private RoleMenuMapper dao;

    @Autowired
    RoleMenuService roleMenuService;

    @Override
    public void deleteRoleMenu(Long[] roleIds) {
        for(Long roleId : roleIds){
            dao.deleteById(roleId);
        }
    }

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    @Override
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList) {
        roleMenuService.saveBatch(roleMenuList);
        return 0;
    }


    /**
     * 批量新增角色菜单信息
     *
     * @param role 角色对象
     */
    @Override
    public boolean insertRoleMenu(SysRole role) {
        boolean rows = false;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuService.saveBatch(list);
        }
        return rows;
    }

    @Override
    public int deleteRoleMenuByRoleId(Long roleId) {
        return dao.deleteById(roleId);
    }
}


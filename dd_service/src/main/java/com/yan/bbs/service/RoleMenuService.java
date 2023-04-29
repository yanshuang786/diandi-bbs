package com.yan.bbs.service;

import com.yan.bbs.entity.SysRoleMenu;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.entity.SysRole;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 15:46
 */
public interface RoleMenuService extends SuperService<SysRoleMenu> {
    /**
     * 根据角色ids删除
     * @param roleIds
     */
    public void deleteRoleMenu(Long[] roleIds);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);

    /**
     * 批量增加
     * @param role
     * @return
     */
    public boolean insertRoleMenu(SysRole role);

    /**
     * 根据id删除
     * @param roleId
     * @return
     */
    public int deleteRoleMenuByRoleId(Long roleId);
}

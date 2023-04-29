package com.yan.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * @author yanshuang
 * @date 2023/4/25 16:57
 */
public interface RoleService extends SuperService<SysRole> {
    /**
     * 查询所有的角色
     * @param role
     * @return
     */
    IPage<SysRole> selectRoleList(SysRole role);

    /**
     * 检查用户是否有权限
     * @param roleId
     */
    void checkRoleDataScope(Long roleId);

    SysRole selectRoleById(Long roleId);

    /**
     * 校验角色名是否合法
     * @param role
     * @return
     */
    String checkRoleNameUnique(SysRole role);

    /**
     * 校验用户名是否合法
     * @param role
     * @return
     */
    String checkRoleKeyUnique(SysRole role);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean insertRole(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 根据角色id 进行删除
     * @param roleIds
     * @return
     */
    boolean deleteRoleByIds(Long[] roleIds);


    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 根据管理员ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByAdminId(Long userId);

    /**
     * 获取
     * @param userId
     * @return
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByAdminId(Long userId);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean editRole(SysRole role);


    /**
     * 更新角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean saveOrUpdateRole(SysRole role);
}

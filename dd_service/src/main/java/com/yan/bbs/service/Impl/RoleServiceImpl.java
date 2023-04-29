package com.yan.bbs.service.Impl;

import com.yan.bbs.mapper.RoleMapper;
import com.yan.bbs.service.AdminService;
import com.yan.bbs.service.RoleMenuService;
import com.yan.bbs.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.page.PageDomain;
import com.yan.dd_common.core.page.TableSupport;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.entity.SysRole;
import com.yan.dd_common.enums.DStatus;
import com.yan.dd_common.exception.ServiceException;
import com.yan.dd_common.utils.SecurityUtils;
import com.yan.dd_common.utils.SpringUtils;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/25 16:58
 */
@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, SysRole> implements RoleService {

    @Autowired
    private RoleMapper dao;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 根据用户ID查询权限
     *
     * @param roleId 角色id
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByAdminId(Long roleId) {
        // 查询角色
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getDelFlag, DStatus.ENABLE.getCode());
        queryWrapper.eq(SysRole::getRoleId,roleId);
        List<SysRole> perms = dao.selectList(queryWrapper);

        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll(){
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getStatus,"1");
        return dao.selectList(queryWrapper);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        Admin admin = adminService.selectAdminById(userId);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleId,admin.getRoleId());
        queryWrapper.eq(SysRole::getDelFlag,"0");
        return dao.selectList(queryWrapper);
    }
    /**
     * 模糊查询
     * @param role
     * @return
     */
    @Override
    public IPage<SysRole> selectRoleList(SysRole role) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<SysRole> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getDelFlag, DStatus.ENABLE.getCode());
        if(!StringUtils.isEmpty(role.getRoleName())) {
            queryWrapper.like(SysRole::getRoleName,role.getRoleName());
        }
        if(!StringUtils.isEmpty(role.getRoleKey())) {
            queryWrapper.like(SysRole::getRoleKey,role.getRoleKey());
        }
        if(!StringUtils.isEmpty(role.getStatus())) {
            queryWrapper.eq(SysRole::getStatus, role.getStatus());
        }
        if(!StringUtils.isEmpty(role.getParams())) {
            String beginTime = (String) role.getParams().get("beginTime");
            String endTime = (String) role.getParams().get("endTime");
            queryWrapper.ge(SysRole::getCreateTime,beginTime).le(SysRole::getCreateTime,endTime);
        }
        IPage<SysRole> sysRoles = dao.selectPage(page,queryWrapper);
        return sysRoles;
    }



    /**
     * 根据id查询角色信息
     * @param roleId
     * @return
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        if(!StringUtils.isNull(roleId)) {
            queryWrapper.eq(SysRole::getRoleId,roleId);
        }
        SysRole sysRole = dao.selectOne(queryWrapper);
        return sysRole;
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertRole(SysRole role) {

        // 新增角色信息 ，mybatis-plus 没办法解决返回值的问题
        saveOrUpdateRole(role);

        return roleMenuService.insertRoleMenu(role);
    }


    /**
     * 根据id进行删除
     * @param roleIds 角色id
     * @return
     */
    @Override
    @Transactional
    public boolean deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds )  {
            // 检查是否是超级管理员
            SysRole sysRole = new SysRole();
            sysRole.setRoleId(roleId);
            checkRoleAllowed(sysRole);
            SysRole role = selectRoleById(roleId);
            // 查询当前角色是否有用户关联
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联

        // 逻辑删除

        for(Long roleId : roleIds) {
            SysRole sysRole = new SysRole();
            sysRole.setRoleId(roleId);
            sysRole.setDelFlag("2");
            dao.updateById(sysRole);
        }
        return true;
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("role_id",role.getRoleId());
        return dao.update(role,sysRoleQueryWrapper);
    }
    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminId,userId);
        List<Long> list = new ArrayList<>();
        Admin admin = adminService.selectAdminById(userId);
        list.add(admin.getRoleId());
        return list;
    }

    @Override
    public List<Long> selectRoleListByAdminId(Long userId) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminId,userId);
        List<Long> list = new ArrayList<>();
        Admin admin = adminService.selectAdminById(userId);
        list.add(admin.getRoleId());
        return list;
    }


    /**
     * 修改角色信息
     * @param role 角色信息
     * @return
     */
    @Override
    public boolean editRole(SysRole role) {
        // 修改角色信息
        saveOrUpdateRole(role);
        // 删除角色与菜单关联
        roleMenuService.deleteRoleMenuByRoleId(role.getRoleId());
        return roleMenuService.insertRoleMenu(role);
    }


    /**
     * 保存或者修改角色信息
     * @param role 角色信息
     * @return
     */
    @Override
    public boolean saveOrUpdateRole(SysRole role) {
        // 修改角色信息
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<>();
        if(StringUtils.isNotNull(role.getRoleName())){
            updateWrapper.set(SysRole::getRoleName,role.getRoleName());
        }
        if(StringUtils.isNotNull(role.getRoleKey())){
            updateWrapper.set(SysRole::getRoleKey,role.getRoleKey());
        }
        if(StringUtils.isNotNull(role.getRoleSort())){
            updateWrapper.set(SysRole::getRoleSort,role.getRoleSort());
        }
        if(StringUtils.isNotNull(role.getDataScope())){
            updateWrapper.set(SysRole::getDataScope,role.getDataScope());
        }
        if(StringUtils.isNotNull(role.isMenuCheckStrictly())){
            updateWrapper.set(SysRole::isMenuCheckStrictly, role.isMenuCheckStrictly());
        }
        if(StringUtils.isNotNull(role.getStatus())){
            updateWrapper.set(SysRole::getStatus,role.getStatus());
        }
        if(StringUtils.isNotNull(role.getRemark())){
            updateWrapper.set(SysRole::getRemark,role.getRemark());
        }
        if(StringUtils.isNotNull(role.getUpdateBy())){
            updateWrapper.set(SysRole::getUpdateBy,role.getUpdateBy());
        }

        SysRole sysRole = new SysRole();
        if(StringUtils.isNotNull(role.getRoleName())){
            sysRole.setRoleName(role.getRoleName());
        }
        if(StringUtils.isNotNull(role.getRoleKey())){
            sysRole.setRoleKey(role.getRoleKey());
        }
        if(StringUtils.isNotNull(role.getRoleSort())){
            sysRole.setRoleSort(role.getRoleSort());
        }
        if(StringUtils.isNotNull(role.getDataScope())){
            sysRole.setDataScope(role.getDataScope());
        }
        if(StringUtils.isNotNull(role.isMenuCheckStrictly())){
            sysRole.setMenuCheckStrictly(role.isMenuCheckStrictly());
        }
        if(StringUtils.isNotNull(role.getStatus())){
            sysRole.setStatus(role.getStatus());
        }
        if(StringUtils.isNotNull(role.getRemark())){
            sysRole.setRemark(role.getRemark());
        }
        if(StringUtils.isNotNull(role.getUpdateBy())){
            sysRole.setUpdateBy(role.getUpdateBy());
        }
        sysRole.setUpdateTime(new Date());
        updateWrapper.set(SysRole::getUpdateTime, new Date());
        updateWrapper.eq(SysRole::getRoleId,role.getRoleId());
        return saveOrUpdate(role);
//        return roleService.update(updateWrapper);
    }

    /* -------------------------------------------数据校验---------------------------------------------------------------- */


    /**
     * 校验用户名是否合法
     * @param role
     * @return
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        if(!StringUtils.isNull(role.getRoleName())) {
            queryWrapper.eq(SysRole::getRoleName,role.getRoleName());
        }
        SysRole sysRole = dao.selectOne(queryWrapper);
        if(StringUtils.isNotNull(sysRole) && sysRole.getRoleId().longValue() != role.getRoleId()){
            return UserConstants.ROLE_NAME_NOT_UNIQUE;
        }
        return UserConstants.ROLE_NAME_UNIQUE;
    }

    /**
     * 校验权限是否合法
     * @param role
     * @return
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        if(!StringUtils.isNull(role.getRoleKey())) {
            queryWrapper.eq(SysRole::getRoleKey,role.getRoleKey());
        }
        SysRole sysRole = dao.selectOne(queryWrapper);
        if(StringUtils.isNotNull(sysRole) && sysRole.getRoleId().longValue() != sysRole.getRoleId()){
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }



    /**
     *
     * TODO
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId)
    {
        if (!Admin.isAdmin(SecurityUtils.getLoginAdminId()))
        {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysRoleLambdaQueryWrapper.eq(SysRole::getRoleId, roleId);
            List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role).getRecords();
            if (StringUtils.isEmpty(roles))
            {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }


    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role)
    {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return 0;
//        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

}

package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.SysUserRole;
import com.yan.bbs.mapper.AdminMapper;
import com.yan.bbs.mapper.SysUserRoleMapper;
import com.yan.bbs.service.AdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yan.dd_common.annotation.DataScope;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.enums.DStatus;
import com.yan.dd_common.exception.ServiceException;
import com.yan.dd_common.utils.SecurityUtils;
import com.yan.dd_common.utils.SpringUtils;
import com.yan.dd_common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 12:32
 */
@Service
public class AdminServiceImpl extends SuperServiceImpl<AdminMapper, Admin> implements AdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private AdminService adminService;

    /**
     *
     * @param admin
     * @return
     */
    @Override
    public List<Admin> selectAdminList(Admin admin) {

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotNull(admin.getAdminName())){
            queryWrapper.like(Admin::getAdminName,admin.getAdminName());
        }
        if(StringUtils.isNotNull(admin.getPhoneNumber())){
            queryWrapper.like(Admin::getPhoneNumber,admin.getPhoneNumber());
        }
        if(StringUtils.isNotNull(admin.getStatus())){
            queryWrapper.eq(Admin::getStatus,admin.getStatus());
        }
        if(admin.getParams().size() > 0){
            String a = (String) admin.getParams().get("beginTime");
            String b = (String) admin.getParams().get("endTime");
            queryWrapper.ge(Admin::getCreateTime,a);
            queryWrapper.le(Admin::getCreateTime,b);
        }
        return adminMapper.selectList(queryWrapper);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param admin 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<Admin> selectAllocatedList(Admin admin) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(admin.getAdminName())) {
            queryWrapper.like(Admin::getAdminName,admin.getAdminName());
        }
        if(!StringUtils.isEmpty(admin.getPhoneNumber())){
            queryWrapper.like(Admin::getPhoneNumber,admin.getPhoneNumber());
        }
        queryWrapper.eq(Admin::getDelFlag, DStatus.UNDEL.getCode());
        queryWrapper.eq(Admin::getRoleId,admin.getRoleId());
        return adminMapper.selectList(queryWrapper);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param admin 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<Admin> selectUnallocatedList(Admin admin) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(admin.getAdminName())) {
            queryWrapper.like(Admin::getAdminName,admin.getAdminName());
        }
        if(!StringUtils.isEmpty(admin.getPhoneNumber())){
            queryWrapper.like(Admin::getPhoneNumber,admin.getPhoneNumber());
        }
        queryWrapper.eq(Admin::getDelFlag, DStatus.UNDEL.getCode());
        queryWrapper.ne(Admin::getRoleId,admin.getRoleId());
        return adminMapper.selectList(queryWrapper);
    }

    /**
     * 通过用户名查询用户
     *
     * @param adminName 用户名
     * @return 用户对象信息
     */
    @Override
    public Admin selectUserByAdminName(String adminName) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminName,adminName);
        return adminMapper.selectOne(queryWrapper);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param adminId 用户ID
     * @return 用户对象信息
     */
    @Override
    public Admin selectAdminById(Long adminId) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminId,adminId);
        return adminMapper.selectOne(queryWrapper);
//        return adminMapper.selectUserById(adminId);
    }


    /**
     * 校验用户名称是否唯一
     *
     * @param adminName 用户名称
     * @return 结果
     */
    @Override
    public String checkAdminNameUnique(String adminName)
    {
//        int count = adminMapper.checkUserNameUnique(adminName);
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminName,adminName);
        int count = adminMapper.selectCount(queryWrapper);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param admin 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(Admin admin)
    {
        Long userId = StringUtils.isNull(admin.getAdminId()) ? -1L : admin.getAdminId();

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getPhoneNumber,admin.getPhoneNumber());
        Admin info = adminMapper.selectOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getAdminId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param admin 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(Admin admin)
    {
        Long userId = StringUtils.isNull(admin.getAdminId()) ? -1L : admin.getAdminId();
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getEmail,admin.getEmail());
        Admin info = adminMapper.selectOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getAdminId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }


    /**
     * 校验用户是否有数据权限
     *
     * @param adminId 用户id
     */
    @Override
    public void checkAdminDataScope(Long adminId) {
        if (!Admin.isAdmin(SecurityUtils.getLoginAdminId())) {
            Admin admin = new Admin();
            admin.setAdminId(adminId);
            List<Admin> users = SpringUtils.getAopProxy(this).selectAdminList(admin);
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param admin 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAdmin(Admin admin)
    {
        // 新增用户信息
        // 角色没有新增
        admin.setCreateTime(new Date());
        return adminMapper.insert(admin);
    }


    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertAdminAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param admin 用户信息
     * @return 结果
     */
    @Override
    public boolean updateAdminStatus(Admin admin) {
        admin.setUpdateTime(new Date());
        return adminService.updateById(admin);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
//    @Override
//    public int updateUserProfile(Admin user)
//    {
//        return adminMapper.updateUser(user);
//    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
//    @Override
//    public boolean updateUserAvatar(String userName, String avatar)
//    {
//        return adminMapper.updateUserAvatar(userName, avatar) > 0;
//    }

    /**
     * 重置用户密码
     *
     * @param admin 用户信息
     * @return 结果
     */
    @Override
    public boolean resetPwd(Admin admin)
    {
        return adminService.updateById(admin);
    }


    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(Admin user)
    {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getAdminId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0)
            {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(Admin user)
    {

    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotNull(roleIds))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0)
            {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
//    @Override
//    @Transactional
//    public int deleteUserById(Long userId)
//    {
//        // 删除用户与角色关联
//        userRoleMapper.deleteUserRoleByUserId(userId);
//        // 删除用户与岗位表
//        userPostMapper.deleteUserPostByUserId(userId);
//        return adminMapper.deleteUserById(userId);
//    }

    /**
     * 批量删除用户信息
     *
     * @param adminIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteAdminByIds(Long[] adminIds)
    {
        for (Long userId : adminIds) {
            checkAdminAllowed(new Admin(userId));
        }
        // 逻辑删除用户
        return adminService.removeByIds(Arrays.asList(adminIds));
    }


    /**
     * 校验管理员是否允许操作
     *
     * @param admin 管理员信息
     */
    @Override
    public void checkAdminAllowed(Admin admin) {
        if (StringUtils.isNotNull(admin.getAdminId()) && admin.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 更新管理员信息
     * @param admin
     * @return
     */
    @Override
    public int updateAdmin(Admin admin) {
        // 设置更新时间
        admin.setUpdateTime(new Date());
        // 设置角色id，
        // TODO 目前只能设置单角色
        admin.setRoleId(admin.getRoleIds()[0]);
        return adminMapper.updateById(admin);
    }

}


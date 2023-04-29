package com.yan.bbs.service;

import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.entity.Admin;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 12:32
 */
public interface AdminService extends SuperService<Admin> {
    /**
     * 根据条件分页查询管理员列表
     *
     * @param admin 管理员信息
     * @return 管理员信息集合信息
     * 1
     */
    public List<Admin> selectAdminList(Admin admin);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param admin 用户信息
     * @return 用户信息集合信息
     */
    public List<Admin> selectAllocatedList(Admin admin);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<Admin> selectUnallocatedList(Admin user);

    /**
     * 通过用户名查询用户 ，权限认证
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public Admin selectUserByAdminName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     * 1
     */
    public Admin selectAdminById(Long userId);


    /**
     * 校验用户名称是否唯一
     *
     * @param adminName 用户名称
     * @return 结果
     * 1
     */
    public String checkAdminNameUnique(String adminName);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     * 1
     */
    public String checkPhoneUnique(Admin user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     * 1
     */
    public String checkEmailUnique(Admin user);

    /**
     * 校验用户是否有数据权限
     *
     * @param adminId 用户id
     *                1
     */
    public void checkAdminDataScope(Long adminId);

    /**
     * 新增管理员信息
     *
     * @param user 用户信息
     * @return 结果
     * 1
     */
    public int insertAdmin(Admin user);



    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     *                1
     */
    public void insertAdminAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     * 1
     */
    public boolean updateAdminStatus(Admin user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
//    public int updateUserProfile(Admin user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
//    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     * 1
     */
    public boolean resetPwd(Admin user);



    /**
     * 批量删除管理员信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     * 1
     */
    public boolean deleteAdminByIds(Long[] userIds);


    /**
     *
     * @param admin
     * 1
     */
    public void checkAdminAllowed(Admin admin);

    /**
     * 更新管理员信息
     * @param admin
     * @return
     * 1
     */
    public int updateAdmin(Admin admin);
}

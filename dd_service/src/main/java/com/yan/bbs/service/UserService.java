package com.yan.bbs.service;

import com.yan.dd_common.entity.User;
import com.yan.bbs.service.Impl.SuperService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 15:42
 */
public interface UserService extends SuperService<User> {


    /**
     * 查询所有用户，条件查询
     * @param admin
     * @return
     */
    List<User> selectUserList(User admin);

    /**
     * 根据用户名查查询用户，登陆功能
     * @param username
     * @return
     */
    User selectUserByUserName(String username);

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertAdmin(User user);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    User selectUserById(@Param("userId") Long userId);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    String checkEmailUnique(User user);

    String checkUserNameUnique(String userName);

    String checkPhoneUnique(User user);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user);

    /**
     * 根据用户id删除用户
     * @param userIds
     * @return
     */
    boolean deleteUserByIds(Long[] userIds);

    /**
     * 重制密码
     * @param user
     * @return
     */
    int resetPwd(User user);

    /**
     * 更新用户状态
     * @param user
     * @return
     */
    int updateUserStatus(User user);

    /**
     * 更新用户评论状态
     * @param user
     * @return
     */
    int updateInterchangeCommentStatus(User user);

    /**
     * 给用户授权角色
     * @param userId
     * @param roleIds
     */
    boolean insertUserAuth(Long userId, Long[] roleIds);

    public List<User> getUserListByIds(List<String> ids);


    /**
     * 获取用户信息
     * @return
     */
    User getInfo();


    /**
     * 根据用户ID查询用户
     * @param userId
     * @return
     */
    User getUserById(Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户详细信息
     */
    User getUserByName(String username);
}

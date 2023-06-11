package com.yan.bbs.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yan.dd_common.entity.User;
import com.yan.bbs.mapper.UserMapper;
import com.yan.bbs.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.SecurityUtils;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 15:42
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     * @param user
     * @return
     */
    @Override
    public List<User> selectUserList(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotNull(user.getUserName())){
            queryWrapper.like(User::getUserName,user.getUserName());
        }
        if(StringUtils.isNotNull(user.getStatus())){
            queryWrapper.eq(User::getStatus,user.getStatus());
        }
        if(StringUtils.isNotNull(user.getMobile())){
            queryWrapper.like(User::getMobile,user.getMobile());
        }
        if(StringUtils.isNotNull(user.getParams())){
            String a = (String) user.getParams().get("beginTime");
            String b = (String) user.getParams().get("endTime");
            queryWrapper.ge(User::getCreateTime,a);
            queryWrapper.le(User::getCreateTime,b);
        }
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public User selectUserByUserName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", username);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 添加用户信息
     * @param admin 用户信息
     * @return
     */
    @Override
    public int insertAdmin(User admin) {
        return  this.baseMapper.insert(admin);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public User selectUserById(Long userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId,userId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(User user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        // 邮箱唯一
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,user.getEmail());
        User info = userService.getBaseMapper().selectOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        int count = userService.getBaseMapper().selectCount(queryWrapper);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 检查电话号是否重复
     * @param user 用户详情
     * @return 是否重复
     */
    @Override
    public String checkPhoneUnique(User user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMobile,user.getMobile());
        List<User> users = this.baseMapper.selectList(queryWrapper);
        if(users.size() > 1) {
            return UserConstants.NOT_UNIQUE;
        } else if(users.size() == 0) {
            return UserConstants.UNIQUE;
        }
        User info = users.get(0);
        // 不是同一个人
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int updateUser(User user) {
        return  this.baseMapper.updateById(user);
    }

    @Override
    public boolean deleteUserByIds(Long[] userIds) {

        // 逻辑删除用户
        return userService.removeByIds(Arrays.asList(userIds));
    }

    /**
     * 重制密码
     * @param user
     * @return
     */
    @Override
    public int resetPwd(User user) {
        return this.baseMapper.updateById(user);
    }

    /**
     * 更新用户状态
     * @param user
     * @return
     */
    @Override
    public int updateUserStatus(User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>();
        updateWrapper.eq(User::getUserId, user.getUserId());
        updateWrapper.set(User::getStatus, user.getStatus());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Override
    public int updateInterchangeCommentStatus(User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>();
        updateWrapper.eq(User::getUserId, user.getUserId());
        updateWrapper.set(User::getCommentStatus, user.getCommentStatus());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Override
    public boolean insertUserAuth(Long userId, Long[] roleIds) {
        User user = new User();
        user.setUserId(userId);
        user.setRoleId(roleIds[0]);
        return userService.updateById(user);
    }

    @Override
    public List<User> getUserListByIds(List<String> ids) {
        List<User> userList = new ArrayList<>();
        if (ids == null || ids.size() == 0) {
            return userList;
        }
        Collection<User> userCollection = userService.listByIds(ids);
        userCollection.forEach(item -> {
            userList.add(item);
        });
        return userList;
    }

    /**
     * 获取用户
     * @return 用户信息
     */
    @Override
    public User getInfo() {
//        Long loginAdminId = SecurityUtils.getLoginAdminId();
        // 获取用户信息
        return userService.getById(3);
    }

    /**
     * 获取用户
     * @param userId 用户的ID
     * @return 用户信息
     */
    @Override
    public User getUserById(Long userId) {
        return getById(userId);
    }

    @Override
    public User getUserByName(String username) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = this.baseMapper.selectOne(queryWrapper);
        return user;
    }

}


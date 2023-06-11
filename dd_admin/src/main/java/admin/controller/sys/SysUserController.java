package admin.controller.sys;

import com.yan.dd_common.entity.User;
import com.yan.bbs.service.AdminService;
import com.yan.bbs.service.RoleService;
import com.yan.bbs.service.UserService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.core.page.TableDataInfo;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.entity.SysRole;
import com.yan.dd_common.utils.MD5Utils;
import com.yan.dd_common.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author yanshuang
 * @date 2023/3/28 12:28
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @GetMapping("/authInfo")
    public R getAuthInfo(@RequestParam("username") String username) {
        return R.success(userService.selectUserByUserName(username));
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(User user) {
        startPage();
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }


    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = { "/", "/{userId}" })
    public R getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        // 检查往前管理员是否有权限
        adminService.checkAdminDataScope(userId);
        R ajax = R.success();
        // 查询所有角色
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", Admin.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        if (StringUtils.isNotNull(userId)) {
            ajax.put(R.DATA_TAG, userService.selectUserById(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PostMapping
    public R add(@Validated @RequestBody User user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return R.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }

        // 后端采用MD5加密
        user.setPassWord(MD5Utils.string2MD5(user.getPassWord()));

        return toAjax(userService.insertAdmin(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    public R edit(@Validated @RequestBody User user) {
        if (StringUtils.isNotEmpty(user.getMobile())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @GetMapping("/remove/{userIds}")
    public R remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody User user) {
        user.setPassWord(MD5Utils.string2MD5(user.getPassWord()));
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody User user) {
        return toAjax(userService.updateUserStatus(user));
    }


    @PutMapping("/changeCStatus")
    public R changeCommentStatus(@RequestBody User user) {
        return toAjax(userService.updateInterchangeCommentStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @GetMapping("/authRole/{userId}")
    public R authRole(@PathVariable("userId") Long userId)
    {
        R ajax = R.success();
        User user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("user", user);
        ajax.put("roles", Admin.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PutMapping("/authRole")
    public R insertAuthRole(Long userId, Long[] roleIds) {
        return R.success(userService.insertUserAuth(userId, roleIds));
    }
}

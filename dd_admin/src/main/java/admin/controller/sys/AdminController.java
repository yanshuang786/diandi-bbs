package admin.controller.sys;

import com.yan.bbs.service.AdminService;
import com.yan.bbs.service.RoleService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.core.page.TableDataInfo;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.entity.SysRole;
import com.yan.dd_common.utils.SecurityUtils;
import com.yan.dd_common.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanshuang
 * @date 2023/4/27 12:38
 */
@RestController
@RequestMapping("/system/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;


    @GetMapping("/authInfo")
    public R getAuthInfo(@RequestParam("username") String username) {
        return R.success(adminService.selectUserByAdminName(username));
    }


    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Admin admin) {
        startPage();
        List<Admin> list = adminService.selectAdminList(admin);
        return getDataTable(list);
    }

    /**
     * 根据用户编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public R getInfo(@PathVariable(value = "userId", required = false) Long adminId) {
        adminService.checkAdminDataScope(adminId);
        R ajax = R.success();
        // 查询所有角色
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", Admin.isAdmin(adminId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        if (StringUtils.isNotNull(adminId)) {
            ajax.put(R.DATA_TAG, adminService.selectAdminById(adminId));
            ajax.put("roleIds", roleService.selectRoleListByAdminId(adminId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
//    @PreAuthorize("@ss.hasPermi('system:user:add')")
//    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody Admin admin) {
        if (UserConstants.NOT_UNIQUE.equals(adminService.checkAdminNameUnique(admin.getAdminName()))) {
            return R.error("新增用户'" + admin.getAdminName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(admin.getEmail()) && UserConstants.NOT_UNIQUE.equals(adminService.checkEmailUnique(admin))) {
            return R.error("新增用户'" + admin.getAdminName() + "'失败，邮箱账号已存在");
        }

        admin.setPassword(SecurityUtils.encryptPassword(admin.getPassword()));

        return toAjax(adminService.insertAdmin(admin));
    }


    /**
     * 修改管理员
     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
//    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody Admin admin) {
        adminService.checkAdminAllowed(admin);
        if (StringUtils.isNotEmpty(admin.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(adminService.checkPhoneUnique(admin))) {
            return R.error("修改用户'" + admin.getPhoneNumber() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(admin.getEmail())
                && UserConstants.NOT_UNIQUE.equals(adminService.checkEmailUnique(admin))) {
            return R.error("修改用户'" + admin.getAdminName() + "'失败，邮箱账号已存在");
        }
        admin.setUpdateBy(getUsername());
        return toAjax(adminService.updateAdmin(admin));
    }

    /**
     * 状态修改
     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
//    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody Admin admin) {
        adminService.checkAdminAllowed(admin);
        admin.setUpdateBy(getUsername());
        admin.setUpdateTime(new Date());
        return toAjax(adminService.updateAdminStatus(admin));
    }

    /**
     * 删除管理员
     * @param userIds 管理员id
     * @return
     */
//    @PreAuthorize("@ss.hasPermi('system:user:remove')")
//    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @GetMapping("/remove/{userIds}")
    public R remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(adminService.deleteAdminByIds(userIds));
    }
    /**
     * 重置密码
     */
//    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
//    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody Admin admin) {
        adminService.checkAdminAllowed(admin);
        admin.setPassword(SecurityUtils.encryptPassword(admin.getPassword()));
        admin.setUpdateBy(getUsername());
        admin.setUpdateTime(new Date());
        return toAjax(adminService.resetPwd(admin));
    }

    /**
     * 根据用户编号获取授权角色
     */
//    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public R authRole(@PathVariable("userId") Long userId)
    {
        R ajax = R.success();
        Admin user = adminService.selectAdminById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", Admin.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     * 不完善
     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
//    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R insertAuthRole(Long userId, Long[] roleIds) {
        adminService.insertAdminAuth(userId, roleIds);
        return success();
    }
}

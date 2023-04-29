package admin.controller.sys;

import com.yan.bbs.service.AdminService;
import com.yan.bbs.service.RoleService;
import com.yan.bbs.service.SysPermissionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.core.page.TableDataInfo;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.entity.SysRole;
import com.yan.dd_common.utils.SecurityUtils;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 15:34
 */
@RestController
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private AdminService adminService;

    /**
     * 展示所有角色信息,和查询信息
     * @param role 角色搜索信息
     * @return 分页角色
     */
//    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo lists(SysRole role) {
        IPage<SysRole> list = roleService.selectRoleList(role);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(list.getRecords());
        tableDataInfo.setMsg("成功");
        tableDataInfo.setTotal(list.getTotal());
        return tableDataInfo;
    }

    /**
     * 根据角色编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public R getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return R.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
//    @PreAuthorize("@ss.hasPermi('system:role:add')")
//    @Log(title = "角色管理", businessType = LogType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysRole role) {
        // step1: 校验角色名是否合法
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // step2: 校验权限是否合法
        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return toAjax(roleService.insertRole(role));
    }


    /**
     *
     * 修改保存角色
     */
//    @PreAuthorize("@ss.hasPermi('system:role:edit')")
//    @Log(title = "角色管理", businessType = LogType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysRole role) {
        // 检查权限
        roleService.checkRoleAllowed(role);
        if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        if (roleService.editRole(role)) {
            // 更新缓存用户权限
            Admin loginUser = SecurityUtils.getLoginAdmin();
            if (StringUtils.isNotNull(loginUser) && !loginUser.isAdmin()) {
                // TODO
//                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getAdmin()));
//                loginUser.setAdmin(adminService.selectUserByAdminName(loginUser.getAdmin().getAdminName()));
            }
            return R.success();
        }
        return R.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 状态修改
     * @param role
     * @return
     */
//    @PreAuthorize("@ss.hasPermi('system:role:edit')")
//    @Log(title = "角色管理", businessType = LogType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysRole role) {
        // 只有管理员次才能修改
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
//    @PreAuthorize("@ss.hasPermi('system:role:remove')")
//    @Log(title = "角色管理", businessType = LogType.DELETE)
    @GetMapping("/remove/{roleIds}")
    public R remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }


    /**
     * 查询已分配用户角色列表
     */
//    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(Admin admin) {
        startPage();
        List<Admin> list = adminService.selectAllocatedList(admin);
        return getDataTable(list);
    }

    /**
     * 查询未分配用户角色列表
     */
//    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(Admin admin) {
        startPage();
        List<Admin> list = adminService.selectUnallocatedList(admin);
        return getDataTable(list);
    }

}

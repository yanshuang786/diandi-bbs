package admin.controller.sys;

import com.yan.bbs.entity.SysMenu;
import com.yan.bbs.service.MenuService;
import com.yan.bbs.service.SysPermissionService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Admin;
import com.yan.dd_common.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


/**
 * 后端登陆验证
 *
 * @author yanshuang
 * @date 2023/3/28 14:33
 */
@RestController
public class LoginController {

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private MenuService menuService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public R getInfo() {

        Admin admin = SecurityUtils.getLoginAdmin();

        // 角色集合
        Set<String> roles = permissionService.getRolePermission(admin);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(admin);
        R ajax = R.success();
        ajax.put("user", admin);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }


    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R getRouters() {
//        Long adminId = SecurityUtils.getAdminId();
        Long adminId = 1L;
        List<SysMenu> menus = menuService.selectMenuTreeByAdminId(adminId);
        return R.success(menuService.buildMenus(menus));
    }

    @ApiOperation("退出登陆")
    @PostMapping("/logout")
    public R logout() {
        //TODO JWT过期
        return R.success();
    }


}

package com.yan.dd_web.controller;

import com.yan.bbs.entity.vo.UserVO;
import com.yan.bbs.service.BlogService;
import com.yan.dd_common.entity.User;
import com.yan.bbs.service.UserService;
import com.yan.dd_common.core.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yanshuang
 * @date 2023/4/12 19:06
 */

@RestController
@RefreshScope
@RequestMapping("/")
@Slf4j
public class UserController {

    private final UserService userService;

    private final BlogService blogService;

    public UserController(UserService userService, BlogService blogService) {
        this.userService = userService;
        this.blogService = blogService;
    }

    @GetMapping("/system/user/authInfo")
    public R getAuthInfo(@RequestParam("username") String username) {
        return R.success(userService.selectUserByUserName(username));
    }

    @ApiOperation("获取信息")
    @GetMapping("/info")
    public R getInfo(){
        return R.success(userService.getInfo());
    }

    @ApiOperation("退出登陆")
    @PostMapping("/logout")
    public R logout() {
        //TODO JWT过期
        return R.success();
    }


    @ApiOperation(value = "获取个人博客")
    @GetMapping("/getBlogListByUser")
    public R getBlogListByUser(HttpServletRequest request,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize,
                               Long userId
    ) {
        log.info("获取用户博客");
        return R.success(blogService.getBlogListByUser(request,currentPage,pageSize,userId));
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getUserInfo")
    public R getUserInfo(Long id) {
        return R.success(userService.getUserById(id));
    }

    @ApiOperation(value = "修改用户信息")
    @PostMapping("/update")
    public R update(UserVO userVo) {
        User user = new User();
        BeanUtils.copyProperties(userVo,user);
        userService.updateById(user);
        return R.success();
    }

}

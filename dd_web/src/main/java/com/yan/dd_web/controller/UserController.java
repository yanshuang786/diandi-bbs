package com.yan.dd_web.controller;

import com.yan.dd_common.entity.User;
import com.yan.bbs.service.UserService;
import com.yan.dd_common.core.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author yanshuang
 * @date 2023/4/12 19:06
 */

@RestController
@RefreshScope
@RequestMapping("")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/system/user/authInfo")
    public R getAuthInfo(@RequestParam("username") String username) {
        return R.success(userService.selectUserByUserName(username));
    }

    @ApiOperation("获取信息")
    @GetMapping("/info")
    public R getInfo(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name","admin");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("roles","admin");

        User user = userService.getInfo();

        return R.success(user);
    }

    @ApiOperation("退出登陆")
    @PostMapping("/logout")
    public R logout() {
        //TODO JWT过期
        return R.success();
    }

}

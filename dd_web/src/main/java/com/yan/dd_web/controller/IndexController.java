package com.yan.dd_web.controller;

import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.WebNavbarService;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.ResultUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanshuang
 * @date 2023/4/12 13:00
 */
@RestController
@RequestMapping("/index")
@Slf4j
public class IndexController {

    private final BlogService blogService;

    private final WebNavbarService webNavbarService;

    public IndexController(BlogService blogService, WebNavbarService webNavbarService) {
        this.blogService = blogService;
        this.webNavbarService = webNavbarService;
    }

    @GetMapping("/getNewBlog")
    public R getNewBlog(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return R.success(blogService.getNewBlog(currentPage, pageSize));
    }

    @ApiOperation(value = "dd-search调用获取博客的接口[包含内容]", notes = "dd-search调用获取博客的接口")
    @GetMapping("/getBlogBySearch")
    public String getBlogBySearch(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage) {
        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogBySearch(currentPage, null));
    }

    @GetMapping("/getWebNavbar")
    public R getWebNavbar(@RequestParam String isShow) {
        log.info("获取网站导航栏");
        return R.success(SysConf.SUCCESS, webNavbarService.getWebAllList(isShow));
    }
}

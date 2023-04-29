package com.yan.dd_web.controller;

import com.yan.bbs.service.BlogService;
import com.yan.dd_common.core.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BlogService blogService;

    @GetMapping("/getNewBlog")
    public R getNewBlog(
                        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return R.success(blogService.getNewBlog(currentPage, pageSize));
    }

}

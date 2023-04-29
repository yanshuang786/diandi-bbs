package com.yan.dd_web.controller;

import com.yan.bbs.service.BlogSortService;
import com.yan.dd_common.core.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yanshuang
 * @date 2022/4/27 7:44 下午
 */
@RestController
@RequestMapping("/blog")
public class BlogSortController {


    private final BlogSortService blogSortService;

    public BlogSortController(BlogSortService blogSortService) {
        this.blogSortService = blogSortService;
    }


    @ApiOperation(value = "获取博客分类")
    @GetMapping("blogSort/getList")
    public R getBlogSortList() {
        return R.success(blogSortService.getList());
    }

}

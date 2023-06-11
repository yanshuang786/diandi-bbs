package com.yan.dd_web.controller;

import com.yan.bbs.entity.vo.BlogVO;
import com.yan.bbs.service.BlogService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.core.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 文章相关API
 *
 * @author yanshuang
 * @date 2022/4/27 8:27 下午
 */
@RestController
@Api(value = "文章相关API接口", tags = {"文章相关API接口"})
public class BlogController extends BaseController {


    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @ApiOperation(value = "通过id获取博客内容", notes = "通过id获取博客内容")
    @GetMapping("/content/getBlogById")
    public R getBlogById(@RequestParam(name = "id", required = false) Integer id) {
        return blogService.getBlogById(id);
    }

    @ApiOperation(value = "增加博客", notes = "增加博客")
    @PostMapping("/blog/add")
    public R add(@RequestBody BlogVO blogVO) {
        return blogService.addBlog(blogVO);
    }

    @ApiOperation(value = "更新博客")
    @RequestMapping("/blog/update")
    public R update(@RequestBody BlogVO blogVO) {
        return blogService.updateBlog(blogVO,getLoginUser().getUserId());
    }

    @ApiOperation(value = "删除博客")
    @RequestMapping("/blog/delete")
    public R update(@RequestParam(name = "id", required = false) Integer id) {
        return blogService.deleteById(id);
    }
}
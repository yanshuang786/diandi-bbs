package com.yan.dd_web.controller;

import com.yan.bbs.service.BlogLikeService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.core.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞相关API
 *
 * @author yanshuang
 * @date 2023/5/2 14:29
 */
@RestController
@Api(value = "博客点赞", tags = {"博客点赞"})
public class LikeController extends BaseController {

    private final BlogLikeService blogLikeService;

    public LikeController(BlogLikeService blogLikeService) {
        this.blogLikeService = blogLikeService;
    }


    @ApiOperation(value = "通过博客id点赞", notes = "通过博客id点赞")
    @PostMapping("/blog/like")
    public R likeBlog(Integer blogId) {
        return blogLikeService.likeBlog(blogId, getLoginUser().getUserId());
    }


    @ApiOperation(value = "通过博客id取消点赞", notes = "通过博客id取消点赞")
    @PostMapping("/blog/unlike")
    public R unLikeBlog(Integer blogId) {
        return blogLikeService.unLikeBlog(blogId, getLoginUser().getUserId());
    }
}

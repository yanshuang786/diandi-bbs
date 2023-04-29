package com.yan.bbs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yan.bbs.entity.UserLike;
import com.yan.dd_common.core.R;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 15:35
 */
public interface BlogLikeService extends IService<UserLike> {

    /**
     * 点赞博客
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    R likeBlog(Integer blogId, Long userId);

    /**
     * 取消点赞
     * @param blogId 博客ID
     */
    public R unLikeBlog(Integer blogId,Long userId);

    /**
     * 保存到数据库
     */
    public void transLikedFromRedis2Db();


    /**
     * 获取用户点赞的文章
     * @return
     */
    public List<UserLike> getUserLikeBlog(Long userId);
}

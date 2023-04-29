package com.yan.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.bbs.entity.Comment;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.model.Vo.CommentVO;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 21:40
 */
public interface CommentService extends SuperService<Comment> {

    /**
     * 获取评论数目
     *
     * @author xzx19950624@qq.com
     * @date 2018年10月22日下午3:43:38
     */
    Integer getCommentCount(int status);

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @return
     */
    IPage<Comment> getPageList(CommentVO commentVO);

    /**
     * 新增评论
     *
     * @param commentVO
     */
    String addComment(CommentVO commentVO);

    /**
     * 编辑评论
     *
     * @param commentVO
     */
    String editComment(CommentVO commentVO);

    /**
     * 删除评论
     *
     * @param commentVO
     */
    String deleteComment(CommentVO commentVO);

    /**
     * 批量删除评论
     *
     * @param commentVOList
     */
    String deleteBatchComment(List<CommentVO> commentVOList);

    /**
     *
     * @param blogUidList
     * @return
     */
    String batchDeleteCommentByBlogUid(List<String> blogUidList);


}


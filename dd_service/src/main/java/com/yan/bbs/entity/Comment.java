package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.entity.SuperBaseEntity;
import com.yan.dd_common.entity.User;
import lombok.Data;

import java.util.List;

/**
 *
 * 评论表
 *
 * @author yanshuang
 * @date 2023/4/28 21:41
 */
@Data
@TableName("d_comment")
public class Comment extends SuperBaseEntity<Comment> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户uid
     */
    private Integer userId;

    /**
     * 回复某条评论的uid
     */
    private Integer toId;

    /**
     * 该条评论下的，一级评论UID
     */
    private Integer firstCommentId;

    /**
     * 回复某个人的uid
     */
    private Integer toUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 博客uid
     */
    private Integer blogId;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    private String source;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 本条评论是哪个用户说的
     */
    @TableField(exist = false)
    private User user;

    /**
     * 发表评论的用户名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 被回复的用户名
     */
    @TableField(exist = false)
    private String toUserName;


    /**
     * 本条评论对哪个用户说的，如果没有则为一级评论
     */
    @TableField(exist = false)
    private User toUser;

    /**
     * 本条评论下的回复
     */
    @TableField(exist = false)
    private List<Comment> replyList;

    /**
     * 本条评论回复的那条评论
     */
    @TableField(exist = false)
    private Comment toComment;

    /**
     * 评论来源名称
     */
    @TableField(exist = false)
    private String sourceName;

    /**
     * 该评论来源的博客
     */
    @TableField(exist = false)
    private Blog blog;

}
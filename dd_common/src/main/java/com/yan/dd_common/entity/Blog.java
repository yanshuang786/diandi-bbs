package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/4/27 15:52
 */
@Data
@TableName("d_blog")
public class Blog {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一oid【自动递增】
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客简介
     * updateStrategy = FieldStrategy.IGNORED ：表示更新时候忽略非空判断
     */
    private String summary;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 标签uid
     */
    private String tagId;

    /**
     * 博客分类UID
     */
    private Integer blogSortId;

    /**
     * 博客点击数
     */
    private Integer clickCount;

    /**
     * 文章点赞数
     */
    private Integer likeCount;

    /**
     * 博客收藏数
     */
    private Integer collectCount;

    /**
     * 博客标题图
     */
    private String photoUrl;

    /**
     * 管理员UID
     */
    private Integer adminId;

    /**
     * 是否发布
     * 0：否，1：是
     */
    private String isPublish;

    /**
     * 是否原创
     */
    private String isOriginal;

    /**
     * 审核
     */
    private String isAudit;

    /**
     * 审核原因
     */
    private String reason;

    /**
     * 如果原创，作者为管理员名
     */
    private String author;

    /**
     * 文章出处
     * 0 后台添加，1 用户投稿
     */
    private String articlesPart;

    /**
     * 推荐级别，用于首页推荐
     * 0：正常
     * 1：一级推荐(轮播图)
     * 2：二级推荐(top)
     * 3：三级推荐 ()
     * 4：四级 推荐 (特别推荐)
     */
    private Integer level;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 投稿用户ID
     */
    private Long userId;


    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标签,一篇博客对应多个标签
     */
    @TableField(exist = false)
    private List<Tag> tagList;

    /**
     * 博客分类
     */
    @TableField(exist = false)
    private BlogSort blogSort;

    /**
     * 博客分类名
     */
    @TableField(exist = false)
    private String blogSortName;

    /**
     * 点赞数
     */
    @TableField(exist = false)
    private Integer praiseCount;

    /**
     * 版权申明
     */
    @TableField(exist = false)
    private String copyright;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String avatar;


    /**
     * 状态 0：失效  1：生效
     */
    private String status;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;

    /**
     * 更新时间
     */
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 请求参数 */
    @TableField(exist = false)
    private Map<String, Object> params;

    @TableField(exist = false)
    private Boolean isLike = false;
}

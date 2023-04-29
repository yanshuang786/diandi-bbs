package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yan.dd_common.enums.EStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 17:08
 */
@Data
@TableName("t_web_config")
public class WebConfig {

    private static final long serialVersionUID = 1L;


    /**
     * 网站Logo
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String logo;

    /**
     * 网站名称
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String name;

    /**
     * 标题
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String title;

    /**
     * 描述
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 关键字
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String keyword;

    /**
     * 作者
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String author;

    /**
     * 备案号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String recordNum;

    /**
     * 支付宝收款码FileId
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String aliPay;

    /**
     * 微信收款码FileId
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String weixinPay;

    /**
     * 友链申请模板
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String linkApplyTemplate;

    /**
     * 是否开启网页端评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 是否开启移动端评论(0:否， 1:是)
     */
    private String openMobileComment;

    /**
     * 是否开启赞赏(0:否， 1:是)
     */
    private String openAdmiration;

    /**
     * 是否开启移动端赞赏(0:否， 1:是)
     */
    private String openMobileAdmiration;

    /**
     * github地址
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String github;

    /**
     * gitee地址
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String gitee;

    /**
     * QQ号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qqNumber;

    /**
     * QQ群
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qqGroup;

    /**
     * 微信号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String weChat;

    /**
     * 邮箱
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String email;

    /**
     * 显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）
     */
    private String showList;

    /**
     * 登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）
     */
    private String loginTypeList;


    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标题图
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * Logo图片
     */
    @TableField(exist = false)
    private String logoPhoto;


    /**
     * 支付宝付款码
     */
    @TableField(exist = false)
    private String aliPayPhoto;

    /**
     * 微信付款码
     */
    @TableField(exist = false)
    private String weixinPayPhoto;


    /**
     * 唯一UID
     */
    @TableId(value = "uid", type = IdType.UUID)
    private String uid;

    /**
     * 状态 0：失效  1：生效
     */
    private int status;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yanshuang
 * @date 2023/4/27 15:40
 */
@Data
@ToString
@TableName("d_user")
public class User extends SuperBaseEntity<User> {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 性别(1:男2:女)
     */
    private String gender;

    /**
     * 个人头像(UID)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 出生年月日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 手机
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String mobile;

    /**
     * QQ号
     */
    private String qqNumber;

    /**
     * 微信号
     */
    private String weChat;

    /**
     * 职业
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String occupation;

    /**
     * 自我简介最多150字
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 验证码
     */
    private String validCode;

    /**
     * 资料来源
     */
    private String source;

    /**
     * 平台uuid
     */
    private String uuid;

    /**
     * 最后登录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 评论状态，0 禁言， 1 正常
     */
    private String commentStatus;

    /**
     * 开启邮件通知：  0：关闭， 1：开启
     */
    private Integer startEmailNotification;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 用户标签  0：普通，1：管理员，2：博主
     */
    private Integer userTag;

    // 以下字段不存入数据库

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String photoUrl;

    /** 删除标志（1代表存在 0代表删除） */
    @TableLogic(value = "1",delval = "0")
    private String delFlag;

}


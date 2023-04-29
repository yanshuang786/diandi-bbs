package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/3/30 15:32
 */
@TableName("d_admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long adminId;

    /** 用户账号 */
    private String adminName;

    /** 用户昵称 */
    private String nickName;

    private Long roleId;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phoneNumber;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String avatar;

    /** 密码 更新时如果该值为空就忽略 */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String password;

    /** 盐加密 */
    @TableField(exist = false)
    private String salt;

    /** 帐号状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic(value = "0",delval = "1")
    private String delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    @TableField
    private Date loginDate;

    /** 角色对象 */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 角色组 */
    @TableField(exist = false)
    private Long[] roleIds;



    /**
     * 自我简介最多150字
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    public Admin(Long adminId) {
        this.adminId = adminId;
    }

    public boolean isAdmin() {
        return isAdmin(this.adminId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }
}

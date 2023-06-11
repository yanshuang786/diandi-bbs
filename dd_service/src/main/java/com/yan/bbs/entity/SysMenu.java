package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单表
 *
 * @author yanshuang
 * @date 2023/4/25 16:58
 */
@Data
@TableName("d_menu")
public class SysMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /** 父菜单名称 */
    @TableField(exist = false)
    private String parentName;

    /** 父菜单ID */
    public Long parentId;

    /** 显示顺序 */
    private String orderNum;

    /** 路由地址 */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    private String path;

    /** 组件路径 */
    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否为外链（0是 1否） */
    private String isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    private String isCache;

    /** 类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 显示状态（0显示 1隐藏） */
    private String visible;

    /** 菜单状态（0显示 1隐藏） */
    private String status;

    /** 权限字符串 */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 子菜单 */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();
}


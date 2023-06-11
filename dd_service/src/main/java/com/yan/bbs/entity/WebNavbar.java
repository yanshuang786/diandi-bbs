package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yan.dd_common.entity.SuperBaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 导航栏管理
 *
 * @author yanshuang
 * @date 2023/4/28 17:04
 */
@Data
@TableName("d_web_navbar")
public class WebNavbar extends SuperBaseEntity<WebNavbar> implements Comparable<WebNavbar> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 导航栏名称
     */
    private String name;

    /**
     * 导航栏级别 （一级分类，二级分类）
     */
    private Integer navbarLevel;

    /**
     * 导航栏介绍
     */
    private String summary;

    /**
     * 父UID
     */
    private String parentUid;

    /**
     * URL地址
     */
    private String url;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 是否显示  1:是  0:否
     */
    private String isShow;

    /**
     * 是否跳转外部URL
     */
    private Integer isJumpExternalUrl;

    /**
     * 父菜单
     */
    @TableField(exist = false)
    private WebNavbar parentWebNavbar;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<WebNavbar> childWebNavbar;

    @Override
    public int compareTo(WebNavbar o) {

        if (this.sort >= o.getSort()) {
            return -1;
        }
        return 1;
    }
}

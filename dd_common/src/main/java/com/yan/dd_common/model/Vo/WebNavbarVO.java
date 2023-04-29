package com.yan.dd_common.model.Vo;

import com.yan.dd_common.validator.annotion.IntegerNotNull;
import com.yan.dd_common.validator.annotion.NotBlank;
import com.yan.dd_common.validator.group.Insert;
import com.yan.dd_common.validator.group.Update;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/28 17:03
 */
@Data
public class WebNavbarVO  {

    /**
     * 唯一UID
     */
    private Integer id;

    /**
     * 菜单名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String name;

    /**
     * 导航栏级别 （一级分类，二级分类）
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer navbarLevel;

    /**
     * 介绍
     */
    private String summary;


    /**
     * 父UID
     */
    private String parentUid;

    /**
     * URL地址
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String url;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 是否显示  1: 是  0: 否
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer isShow;

    /**
     * 是否跳转外部URL，如果是，那么路由为外部的链接
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer isJumpExternalUrl;

    private String status;
}

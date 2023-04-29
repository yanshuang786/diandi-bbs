package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 15:52
 */
@Data
@TableName("d_blog_sort")
public class BlogSort extends SuperBaseEntity<BlogSort> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 分类名
     */
    private String sortName;

    /**
     * 分类介绍
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String content;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;
}


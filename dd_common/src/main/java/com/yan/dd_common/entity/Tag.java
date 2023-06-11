package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * 标签表
 *
 * @author yanshuang
 * @date 2023/4/27 15:52
 */
@Data
@TableName("d_tag")
public class Tag extends SuperBaseEntity<Tag> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 标签内容
     */
    private String content;


    /** 删除标志（1代表存在 0代表删除） */
    @TableLogic(value = "1",delval = "0")
    private String delFlag;

    /**
     * 标签点击量
     */
    private int clickCount;

    /**
     * 标签介绍
     */
    private String summary;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;
}

package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yan.dd_common.entity.SuperBaseEntity;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 17:49
 */
@Data
@TableName("d_sys_dict_data")
public class SysDictData extends SuperBaseEntity<SysDictData> {

    /**
     * 自增键 id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型ID
     */
    private Integer dictTypeId;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 排序字段
     */
    private Integer sort;


    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格回显样式
     */
    private String listClass;

    /**
     * 是否默认（1是 0否）,默认为0
     */
    private Integer isDefault;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
    private String isPublish;

    /**
     * 创建人UID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 最后更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 备注
     */
    private String remark;


    // 以下字段不存入数据库，封装为了前端使用
    /**
     * 字典类型
     */
    @TableField(exist = false)
    private SysDictType sysDictType;
}

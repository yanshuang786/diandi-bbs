package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yan.dd_common.entity.SuperBaseEntity;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 16:22
 */
@Data
@TableName("d_sys_dict_type")
public class SysDictType extends SuperBaseEntity<SysDictType> {

    /**
     * 自增键 oid
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
    private String isPublish;

    /**
     * 创建人
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

    /**
     * 排序字段
     */
    private Integer sort;

}



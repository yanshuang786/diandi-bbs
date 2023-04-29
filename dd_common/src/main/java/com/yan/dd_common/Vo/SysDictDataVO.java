package com.yan.dd_common.Vo;

import com.yan.dd_common.validator.annotion.IntegerNotNull;
import com.yan.dd_common.validator.annotion.NotBlank;
import com.yan.dd_common.validator.group.Insert;
import com.yan.dd_common.validator.group.Update;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 16:30
 */
@Data
public class SysDictDataVO extends BaseVO<SysDictDataVO> {


    /**
     * 自增键 oid
     */
    private Long oid;

    /**
     * 字典标签
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String dictLabel;

    /**
     * 字典键值
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String dictValue;

    /**
     * 字典类型UID
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String dictTypeUid;

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
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer isDefault;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String isPublish;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer sort;

}

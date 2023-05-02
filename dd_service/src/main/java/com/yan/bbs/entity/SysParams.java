package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yan.dd_common.entity.SuperEntity;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/5/1 22:08
 */
@Data
@TableName("t_sys_params")
public class SysParams extends SuperEntity<SysParams> {

    /**
     * 参数名称
     */
    private String paramsName;

    /**
     * 参数键名
     */
    private String paramsKey;

    /**
     * 参数键值
     */
    private String paramsValue;

    /**
     * 参数类型，是否系统内置（1：是，0：否）
     */
    private Integer paramsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;
}

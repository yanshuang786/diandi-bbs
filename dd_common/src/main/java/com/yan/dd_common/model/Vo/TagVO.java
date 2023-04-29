package com.yan.dd_common.model.Vo;

import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.yan.dd_common.base.BaseVo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yanshuang
 * @date 2023/4/28 16:38
 */
@Data
public class TagVO extends BaseVo<TagVO> {

    /**
     * 标签内容
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 介绍
     */
    private String summary;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;


}

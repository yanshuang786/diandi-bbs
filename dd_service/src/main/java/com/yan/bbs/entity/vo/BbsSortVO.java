package com.yan.bbs.entity.vo;

import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.yan.dd_common.base.BaseVo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yanshuang
 * @date 2023/4/28 16:44
 */
@Data
public class BbsSortVO extends BaseVo<BbsSortVO> {

    /**
     * 分类名
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String sortName;

    /**
     * 分类介绍
     */
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;


    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;


}

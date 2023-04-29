package com.yan.dd_common.model.Vo;

import com.yan.dd_common.Vo.SysDictDataVO;
import com.yan.dd_common.base.BaseVo;
import lombok.Data;
/**
 * @author yanshuang
 * @date 2023/4/27 16:24
 */
@Data
public class SysDictDataVo extends BaseVo<SysDictDataVO> {


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

}

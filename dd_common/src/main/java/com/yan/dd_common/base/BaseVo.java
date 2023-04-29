package com.yan.dd_common.base;

import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 16:25
 */
@Data
public class BaseVo <T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    private Integer id;

    private String status;
}

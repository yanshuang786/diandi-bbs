package com.yan.dd_common.Vo;

import com.yan.dd_common.base.PageInfo;
import com.yan.dd_common.validator.annotion.IdValid;
import com.yan.dd_common.validator.group.Delete;
import com.yan.dd_common.validator.group.Update;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/27 16:30
 */
@Data
public class BaseVO<T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    @IdValid(groups = {Update.class, Delete.class})
    private String uid;

    private Integer status;
}

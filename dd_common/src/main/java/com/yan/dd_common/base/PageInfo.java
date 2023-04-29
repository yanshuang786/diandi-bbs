package com.yan.dd_common.base;

import com.yan.dd_common.annotation.LongNotNull;
import com.yan.dd_common.validator.Messages;
import com.yan.dd_common.validator.group.GetList;
import lombok.Data;

/**
 * 用于分页
 *
 * @author yanshuang
 * @date 2023/4/27 16:25
 */
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.PAGE_NOT_NULL)
    private Long currentPage;

    /**
     * 页大小
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.SIZE_NOT_NULL)
    private Long pageSize;
}

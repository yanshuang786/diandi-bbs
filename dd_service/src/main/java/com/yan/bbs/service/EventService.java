package com.yan.bbs.service;

import com.yan.bbs.entity.Event;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.core.R;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/6/11 01:21
 */
public interface EventService extends SuperService<Event> {

    /**
     * 获取当前用的的消息数量
     * @param userId 用户id
     * @return 消息数量
     */
    R getNoticeCount(Long userId);


    /**
     * 查询当前用户的消息列表
     * @param userId 用户ID
     * @return 消息
     */
    List<Event> getNewNoticeList(Long userId);
}

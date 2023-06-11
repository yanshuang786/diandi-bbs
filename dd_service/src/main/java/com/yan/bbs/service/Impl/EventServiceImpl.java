package com.yan.bbs.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yan.bbs.entity.Event;
import com.yan.bbs.mapper.EventMapper;
import com.yan.bbs.service.EventService;
import com.yan.dd_common.core.R;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/6/11 01:22
 */
@Service
public class EventServiceImpl extends SuperServiceImpl<EventMapper, Event> implements EventService {


    private final EventMapper dao;

    public EventServiceImpl(EventMapper dao) {
        this.dao = dao;
    }

    /**
     * 获取当前用的的消息数量
     * @param userId 用户id
     * @return 消息数量
     */
    @Override
    public R getNoticeCount(Long userId) {
        LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<>();
        // 未读
        queryWrapper.eq(Event::getStatus,0);
        queryWrapper.eq(Event::getEntityUserId,userId);
        List<Event> list = dao.selectList(queryWrapper);
        Map<String, Integer> map = new HashMap<>();

        Integer like = 0;
        Integer comments = 0;
        for(Event event : list) {
            if(event.getTopic().equals("0")) {
                like ++;
            } else if (event.getTopic().equals("1")){
                comments ++;
            }
        }
        map.put("like", like);
        map.put("comments", comments);
        return R.success(map);
    }


    /**
     * 查询当前用户的消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    @Override
    public List<Event> getNewNoticeList(Long userId) {
        LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Event::getEntityUserId,userId);
        return dao.selectList(queryWrapper);
    }
}


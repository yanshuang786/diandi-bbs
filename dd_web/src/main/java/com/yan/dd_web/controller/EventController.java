package com.yan.dd_web.controller;

import com.yan.bbs.service.EventService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知
 *
 * @author yanshuang
 * @date 2023/4/24 21:21
 */
@RestController
@RequestMapping("notice")
public class EventController {


    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/getNewNoticeList")
    public R getNewNoticeList() {
        return R.success(eventService.getNewNoticeList(SecurityUtils.getLoginUser().getUserId()));
    }

    @GetMapping("/getCount")
    public R getCount() {
        return eventService.getNoticeCount(SecurityUtils.getLoginUser().getUserId());
    }

}

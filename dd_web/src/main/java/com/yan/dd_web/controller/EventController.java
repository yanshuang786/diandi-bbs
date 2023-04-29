package com.yan.dd_web.controller;

import com.yan.dd_common.core.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知
 *
 * @author yanshuang
 * @date 2023/4/24 21:21
 */
@RestController
@RequestMapping("notice")
public class EventController {


    @GetMapping("/getCount")
    public R getCount() {

        Map<String, Integer> map = new HashMap<>();

        Integer like = 0;
        Integer comments = 0;
        map.put("like", like);
        map.put("comments", comments);
        return R.success(map);
    }

}

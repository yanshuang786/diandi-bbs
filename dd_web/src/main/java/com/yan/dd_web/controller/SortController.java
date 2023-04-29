package com.yan.dd_web.controller;

import com.yan.bbs.service.BlogSortService;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanshuang
 * @date 2021/10/24 4:04 下午
 */
@RestController
@RequestMapping("/sort")
@Api(value = "博客归档相关接口", tags = {"博客归档相关接口"})
@Slf4j
public class SortController {

    @Autowired
    private BlogSortService blogSortService;

    @ApiOperation(value = "归档", notes = "归档")
    @GetMapping("/getSortList")
    public R getSortList() {
        log.info("获取归档日期");
        return R.success(SysConf.SUCCESS, blogSortService.getList());
    }
}

package com.yan.dd_web.controller;


import com.yan.bbs.service.TagService;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.model.Vo.TagVO;
import com.yan.dd_common.utils.ResultUtil;
import com.yan.dd_common.utils.ThrowableUtils;
import com.yan.dd_common.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yanshuang
 * @date 2021/10/24 4:07 下午
 */
@RestController
@RequestMapping("/tag")
@Api(value = "博客标签相关接口", tags = {"博客标签相关接口"})
@Slf4j
public class TagController {

    @Autowired
    private TagService tagService;


    @ApiOperation(value = "获取标签的信息", notes = "获取标签的信息")
    @PostMapping("/getList")
    public R getTagList() {
        log.info("获取标签信息");
        return R.success(SysConf.SUCCESS, tagService.getList());
    }
}

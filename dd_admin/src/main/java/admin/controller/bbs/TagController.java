package admin.controller.bbs;

import com.yan.dd_common.model.Vo.TagVO;
import com.yan.bbs.service.TagService;
import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.ThrowableUtils;
import com.yan.dd_common.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客标签
 *
 * @author yanshuang
 * @date 2022/5/19 3:59 下午
 */
@Api(value = "标签相关接口", tags = {"标签相关接口"})
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Autowired
    private TagService tagService;


    @ApiOperation(value = "获取标签列表", notes = "获取标签列表")
    @PostMapping("/getList")
    public R getList(@Validated({GetList.class}) @RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return R.success(SysConf.SUCCESS, tagService.getPageList(tagVO));
    }

    @ApiOperation(value = "增加标签", notes = "增加标签")
    @PostMapping("/add")
    public R add(@Validated({Insert.class}) @RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加标签");
        return tagService.addTag(tagVO);
    }

    @ApiOperation(value = "编辑标签", notes = "编辑标签")
    @PostMapping("/edit")
    public R edit(@Validated({Update.class}) @RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑标签");
        return tagService.editTag(tagVO);
    }


    @ApiOperation(value = "批量删除标签", notes = "批量删除标签")
    @PostMapping("/deleteBatch")
    public R delete(@Validated({Delete.class}) @RequestBody List<TagVO> tagVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除标签");
        return tagService.deleteBatchTag(tagVoList);
    }



    @ApiOperation(value = "置顶标签", notes = "置顶标签")
    @PostMapping("/stick")
    public R stick(@Validated({Delete.class}) @RequestBody TagVO tagVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶标签");
        return tagService.stickTag(tagVO);
    }
}

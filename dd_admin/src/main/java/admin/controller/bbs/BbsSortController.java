package admin.controller.bbs;

import com.yan.bbs.entity.vo.BbsSortVO;
import com.yan.bbs.service.BlogSortService;
import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.Update;
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
 * @author yanshuang
 * @date 2022/5/19 4:31 下午
 */
@RestController
@RequestMapping("/blogSort")
@Api(value = "博客分类相关接口", tags = {"博客分类相关接口"})
@Slf4j
public class BbsSortController {

    @Autowired
    private BlogSortService blogSortService;

    @ApiOperation(value = "获取博客分类列表", notes = "获取博客分类列表", response = String.class)
    @PostMapping("/getList")
    public R getList(@Validated({GetList.class}) @RequestBody BbsSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取博客分类列表");
        return R.success(blogSortService.getPageList(blogSortVO));
    }


    @ApiOperation(value = "增加博客分类", notes = "增加博客分类", response = String.class)
    @PostMapping("/add")
    public R add(@Validated({Insert.class}) @RequestBody BbsSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加博客分类");
        return blogSortService.addBlogSort(blogSortVO);
    }

    @ApiOperation(value = "编辑博客分类", notes = "编辑博客分类", response = String.class)
    @PostMapping("/edit")
    public R edit(@Validated({Update.class}) @RequestBody BbsSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑博客分类");
        return blogSortService.editBlogSort(blogSortVO);
    }

    @ApiOperation(value = "批量删除博客分类", notes = "批量删除博客分类", response = String.class)
    @PostMapping("/deleteBatch")
    public R delete(@Validated({Delete.class}) @RequestBody List<BbsSortVO> blogSortVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除博客分类");
        return blogSortService.deleteBatchBlogSort(blogSortVoList);
    }

    @ApiOperation(value = "置顶分类", notes = "置顶分类", response = String.class)
    @PostMapping("/stick")
    public R stick(@Validated({Delete.class}) @RequestBody BbsSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶分类");
        return blogSortService.stickBlogSort(blogSortVO);

    }

    @ApiOperation(value = "通过点击量排序博客分类", notes = "通过点击量排序博客分类", response = String.class)
    @PostMapping("/blogSortByClickCount")
    public R blogSortByClickCount() {
        log.info("通过点击量排序博客分类");
        return blogSortService.blogSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return
     */
    @ApiOperation(value = "通过引用量排序博客分类", notes = "通过引用量排序博客分类", response = String.class)
    @PostMapping("/blogSortByCite")
    public R blogSortByCite() {
        log.info("通过引用量排序博客分类");
        return blogSortService.blogSortByCite();
    }
}

package admin.controller.bbs;

import com.yan.bbs.entity.vo.BlogVO;
import com.yan.bbs.service.BlogService;
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

/**
 * 博客审核
 *
 * @author yanshuang
 * @date 2022/5/19 5:45 下午
 */
@RestController
@RequestMapping("/blog")
@Api(value = "博客相关接口", tags = {"博客相关接口"})
@Slf4j
public class BlogAuditController {

    @Autowired
    private BlogService blogService;

    @ApiOperation(value = "获取审核列表", notes = "获取审核列表")
    @PostMapping("/getAuditList")
    public R getAuditList(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return R.success(blogService.getPageAuditList(blogVO));
    }

    @ApiOperation(value = "审核博客", notes = "审核博客")
    @PostMapping("/editAudit")
    public R edit(@RequestBody BlogVO blogVO, BindingResult result) {
        return blogService.editAuditBlog(blogVO);
    }

}

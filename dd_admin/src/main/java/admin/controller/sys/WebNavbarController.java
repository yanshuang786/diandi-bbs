package admin.controller.sys;

import com.yan.dd_common.model.Vo.WebNavbarVO;
import com.yan.bbs.service.WebNavbarService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.ThrowableUtils;
import com.yan.dd_common.validator.group.Delete;
import com.yan.dd_common.validator.group.Insert;
import com.yan.dd_common.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yanshuang
 * @date 2023/4/28 17:02
 */
@RestController
@RequestMapping("/webNavbar")
@Api(value = "门户导航栏管理", tags = {"门户导航栏相关接口"})
@Slf4j
public class WebNavbarController extends BaseController {

    @Autowired
    private WebNavbarService webNavbarService;


    @ApiOperation(value = "获取门户导航栏所有列表", notes = "获取门户导航栏所有列表")
    @GetMapping("/getAllList")
    public R getAllList() {
        return R.success(webNavbarService.getAllList());
    }

    @ApiOperation(value = "增加门户导航栏", notes = "增加门户导航栏", response = String.class)
    @PostMapping("/add")
    public R add(@Validated({Insert.class}) @RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加门户导航栏");
        return toAjax(webNavbarService.addWebNavbar(webNavbarVO));
    }


    @ApiOperation(value = "编辑门户导航栏", notes = "编辑门户导航栏")
    @PostMapping("/edit")
    public R edit(@Validated({Update.class}) @RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑门户导航栏");
        return toAjax(webNavbarService.editWebNavbar(webNavbarVO));
    }


    @ApiOperation(value = "删除门户导航栏", notes = "删除门户导航栏")
    @PostMapping("/delete")
    public R delete(@Validated({Delete.class}) @RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除门户导航栏");
        return toAjax(webNavbarService.deleteWebNavbar(webNavbarVO));
    }

    @ApiOperation(value = "开关门户导航栏状态", notes = "开关门户导航栏状态")
    @PostMapping("/chageStatus")
    public R changeStatus(@Validated({Update.class}) @RequestBody WebNavbarVO webNavbarVO, BindingResult result) {
        return toAjax(webNavbarService.updateStatus(webNavbarVO));
    }
}


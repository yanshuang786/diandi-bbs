package admin.controller.sys;

import com.yan.dd_common.model.Vo.WebConfigVO;
import com.yan.bbs.service.WebConfigService;
import com.yan.dd_common.core.R;
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
 * @date 2023/4/28 17:08
 */
@Api(value = "网站配置相关接口", tags = {"网站配置相关接口"})
@RestController
@RequestMapping("/webConfig")
@Slf4j
public class WebConfigController {

    @Autowired
    public WebConfigService webConfigService;

    @ApiOperation(value = "获取网站配置", notes = "获取网站配置")
    @GetMapping("/getWebConfig")
    public R getWebConfig() {
        return R.success(webConfigService.getWebConfig());
    }

    @ApiOperation(value = "修改网站配置", notes = "修改网站配置")
    @PostMapping("/editWebConfig")
    public R editWebConfig(@Validated({Update.class}) @RequestBody WebConfigVO webConfigVO, BindingResult result) {
        return webConfigService.editWebConfig(webConfigVO);
    }
}

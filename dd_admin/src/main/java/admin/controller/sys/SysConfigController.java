package admin.controller.sys;

import com.yan.bbs.entity.SysConfig;
import com.yan.bbs.service.SysConfigService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yanshuang
 * @date 2023/4/27 17:36
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {


    @Autowired
    private SysConfigService configService;

    /**
     * 获取参数配置列表
     */
//    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config) {
        IPage<SysConfig> list = configService.selectConfigList(config);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(list.getRecords());
        tableDataInfo.setMsg("成功");
        tableDataInfo.setTotal(list.getTotal());
        return tableDataInfo;
    }


    /**
     * 根据参数编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public R getInfo(@PathVariable Long configId) {
        return R.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public R getConfigKey(@PathVariable String configKey) {
        return R.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
//    @PreAuthorize("@ss.hasPermi('system:config:add')")
//    @Log(title = "参数管理", businessType = LogType.INSERT)
    @PostMapping
//    @RepeatSubmit
    public R add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return R.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
//    @PreAuthorize("@ss.hasPermi('system:config:edit')")
//    @Log(title = "参数管理", businessType = LogType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return R.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
//    @PreAuthorize("@ss.hasPermi('system:config:remove')")
//    @Log(title = "参数管理", businessType = LogType.DELETE)
    @DeleteMapping("/{configIds}")
    public R remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     */
//    @PreAuthorize("@ss.hasPermi('system:config:remove')")
//    @Log(title = "参数管理", businessType = LogType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R refreshCache() {
        configService.resetConfigCache();
        return R.success();
    }

//    @ApiOperation(value = "获取系统配置", notes = "获取系统配置")
    @GetMapping("/getSystemConfig")
    public R getSystemConfig() {
        return R.success(configService.getConfig());
    }


}

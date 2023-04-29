package admin.controller.sys;

import com.yan.bbs.entity.SysDictData;
import com.yan.dd_common.model.Vo.SysDictDataVo;
import com.yan.bbs.service.ISysDictDataService;
import com.yan.bbs.service.ISysDictTypeService;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 16:19
 */
@RestController
@RequestMapping("/system/dict/data")
@Api(value = "字典数据相关接口", tags = {"字典数据相关接口"})
public class SysDictDataController {

    private String prefix = "system/dict/data";

    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

//    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public R list(SysDictDataVo dictData) {

        return R.success(dictDataService.getDictDataList(dictData));
    }

    /**
     * 查询详情
     * @param dictCode 详情
     * @return
     */
    @GetMapping(value = "/{dictCode}")
    public R getInfo(@PathVariable Long dictCode) {
        return R.success(dictDataService.getDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public R dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictTypeService.getDictDataByType(dictType);
        return R.success(data);
    }

    /**
     * 修改保存字典类型
     */
//    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
//    @Log(title = "字典数据", businessType = LogType.UPDATE)
    @PostMapping
    public R edit(@Validated @RequestBody SysDictData dict) {
        return R.success(dictTypeService.updateDictData(dict));
    }

    /**
     * 新增字典类型
     */
//    @PreAuthorize("@ss.hasPermi('system:dict:add')")
//    @Log(title = "字典数据", businessType = LogType.INSERT)
    @PostMapping("/add")
    public R add(@Validated @RequestBody SysDictData dict) {
        return R.success(dictDataService.insertDictData(dict));
    }

    /**
     * 删除字典类型
     */
//    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
//    @Log(title = "字典类型", businessType = LogType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return R.success();
    }

    @ApiOperation(value = "根据字典类型数组获取字典数据", notes = "根据字典类型数组获取字典数据", response = String.class)
    @PostMapping("/getListByDictTypeList")
    public R getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        if (dictTypeList.size() <= 0) {
            return R.success(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return R.success(SysConf.SUCCESS, dictDataService.getListByDictTypeList(dictTypeList));
    }
}




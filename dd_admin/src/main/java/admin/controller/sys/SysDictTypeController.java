package admin.controller.sys;

import com.yan.bbs.entity.SysDictType;
import com.yan.bbs.service.ISysDictTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.R;
import com.yan.dd_common.core.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典数据相关接口
 *
 * @author yanshuang
 * @date 2023/4/27 16:20
 */
@RestController
@RequestMapping("/system/dict/type")
@Slf4j
public class SysDictTypeController extends BaseController {


    @Autowired
    public ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        IPage<SysDictType> dictTypeList = dictTypeService.getDictTypeList(dictType);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(dictTypeList.getRecords());
        tableDataInfo.setMsg("成功");
        tableDataInfo.setTotal(dictTypeList.getTotal());
        return tableDataInfo;
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    public R getInfo(@PathVariable Long dictId)
    {
        return R.success(dictTypeService.getDictTypeById(dictId));
    }


    /**
     * 新增字典类型
     */
    @PostMapping
    public R add(@Validated @RequestBody SysDictType dict)
    {
        if (dictTypeService.checkDictTypeUnique(dict) == UserConstants.NOT_UNIQUE){
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(getUsername());
        dict.setUpdateBy(getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }
}


package com.yan.bbs.service;

import com.yan.bbs.entity.SysDictData;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.model.Vo.SysDictDataVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/4/27 16:23
 */
public interface ISysDictDataService extends SuperService<SysDictData> {

    SysDictData getDictDataById(Long dictCode);

    IPage<SysDictData> getDictDataList(SysDictDataVo dictData);

    List<SysDictData> getDictDataList(SysDictData dictData);

    public Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public int insertDictData(SysDictData dictData);


    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    public void deleteDictDataByIds(Long[] dictCodes);
}


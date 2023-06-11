package com.yan.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yan.bbs.entity.SysDictData;
import com.yan.bbs.entity.SysDictType;
import com.yan.bbs.service.Impl.SuperService;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 16:20
 */
public interface ISysDictTypeService extends SuperService<SysDictType> {

    IPage<SysDictType> getDictTypeList(SysDictType dictType);

    List<SysDictData> getDictDataByType(String dictType);

    SysDictType getDictTypeById(Long dictId);

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public boolean updateDictData(SysDictData dictData);


    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    public String checkDictTypeUnique(SysDictType dictType);


    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);
}

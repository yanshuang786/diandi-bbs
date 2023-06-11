package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.SysDictData;
import com.yan.bbs.entity.SysDictType;
import com.yan.bbs.mapper.ISysDictTypeMapper;
import com.yan.bbs.service.ISysDictDataService;
import com.yan.bbs.service.ISysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.page.PageDomain;
import com.yan.dd_common.core.page.TableSupport;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 字典类型
 *
 * @author yanshuang
 * @date 2023/4/27 16:22
 */
@Service
public class ISysDictTypeServiceImpl extends SuperServiceImpl<ISysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Autowired
    public ISysDictTypeMapper dao;

    @Autowired
    public ISysDictDataService sysDictDataService;

    @Override
    public IPage<SysDictType> getDictTypeList(SysDictType dictType) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<SysDictType> page;
        try {
            page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        } catch (Exception e) {
            page = new Page<>(1,10);
        }
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<SysDictType>();
        queryWrapper.eq(SysDictType::getStatus, StatusCode.ENABLE);
        if(StringUtils.isNotNull(dictType.getDictName())){
            queryWrapper.like(SysDictType::getDictName,dictType.getDictName());
        }
        if(StringUtils.isNotNull(dictType.getDictType())){
            queryWrapper.like(SysDictType::getDictType,dictType.getDictType());
        }
        if(StringUtils.isNotNull(dictType.getStatus())){
            queryWrapper.eq(SysDictType::getStatus,dictType.getStatus());
        }
        if(StringUtils.isNotNull(dictType.getParams())){
            String a = (String) dictType.getParams().get("beginTime");
            String b = (String) dictType.getParams().get("endTime");
            queryWrapper.ge(SysDictType::getCreateTime,a);
            queryWrapper.le(SysDictType::getCreateTime,b);
        }
        IPage<SysDictType> sysDictTypePage = dao.selectPage(page, queryWrapper);
        return sysDictTypePage;
    }

    /**
     * 根据参数类型查询参数信息
     * @param dictType
     * @return
     */
    @Override
    public List<SysDictData> getDictDataByType(String dictType) {

        SysDictData dSysDictData = new SysDictData();
        dSysDictData.setDictType(dictType);
        List<SysDictData> dictDataList = sysDictDataService.getDictDataList(dSysDictData);
        return dictDataList;
    }

    /**
     * 根据参数类型id查询详细信息
     * @param dictId
     * @return
     */
    @Override
    public SysDictType getDictTypeById(Long dictId) {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getId,dictId);
        SysDictType dSysDictType = dao.selectOne(queryWrapper);
        return dSysDictType;
    }

    /**
     * 修改保存字典数据信息
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public boolean updateDictData(SysDictData dictData) {
        return dictData.updateById();
    }


    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict)
    {
        return this.baseMapper.insert(dict);
    }


    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getId()) ? -1L : dict.getId();
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getDictType, dict.getDictType());
        List<SysDictType> sysDictTypes = this.baseMapper.selectList(queryWrapper);
        if(sysDictTypes.size() == 0) {
            return UserConstants.UNIQUE;
        } else if(sysDictTypes.size() == 1 && sysDictTypes.get(1).getId().longValue() == dictId.longValue()){
            return UserConstants.UNIQUE;
        } else {
            return UserConstants.NOT_UNIQUE;
        }
    }

}

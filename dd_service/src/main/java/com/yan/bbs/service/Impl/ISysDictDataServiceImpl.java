package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.SysDictData;
import com.yan.bbs.entity.SysDictType;
import com.yan.bbs.mapper.ISysDictDataMapper;
import com.yan.bbs.mapper.ISysDictTypeMapper;
import com.yan.dd_common.model.Vo.SysDictDataVo;
import com.yan.bbs.service.ISysDictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.enums.EPublish;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.JsonUtils;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yanshuang
 * @date 2023/4/27 16:39
 */
@Service
public class ISysDictDataServiceImpl extends SuperServiceImpl<ISysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Autowired
    private ISysDictTypeMapper sysDictTypeMapper;

    @Autowired
    private ISysDictDataMapper dao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<SysDictData> getDictDataList(SysDictDataVo dictData) {
        Page<SysDictData> page = new Page<>();
        page.setSize(dictData.getPageSize());
        page.setCurrent(dictData.getCurrentPage());
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotNull(dictData.getDictType())){
            queryWrapper.eq(SysDictData::getDictType,dictData.getDictType());
        }
        if(StringUtils.isNotNull(dictData.getStatus())) {
            queryWrapper.eq(SysDictData::getStatus,dictData.getStatus());
        }
        if(StringUtils.isNotNull(dictData.getDictLabel())) {
            queryWrapper.like(SysDictData::getDictLabel,dictData.getDictLabel());
        }
        return page(page,queryWrapper);

    }


    @Override
    public List<SysDictData> getDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotNull(dictData.getDictType())) {
            queryWrapper.eq(SysDictData::getDictType,dictData.getDictType());
        }
        queryWrapper.eq(SysDictData::getStatus, StatusCode.ENABLE);
        List<SysDictData> dSysDictData = this.baseMapper.selectList(queryWrapper);
        return dSysDictData;
    }


    @Override
    public Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<String> tempTypeList = new ArrayList<>();
        dictTypeList.forEach(item -> {
            //从Redis中获取内容
            String jsonResult = (String) redisUtil.get(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + item);
            //判断redis中是否有字典
            if (StringUtils.isNotEmpty(jsonResult)) {
                Map<String, Object> tempMap = JsonUtils.jsonToMap(jsonResult);
                map.put(item, tempMap);
            } else {
                // 如果redis中没有该字典，那么从数据库中查询
                tempTypeList.add(item);
            }
        });
        // 表示数据全部从redis中获取到了，直接返回即可
        if (tempTypeList.size() <= 0) {
            return map;
        }
        // 查询 dict_type 在 tempTypeList中的
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConf.DICT_TYPE, tempTypeList);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        List<SysDictType> sysDictTypeList = sysDictTypeMapper.selectList(queryWrapper);
        sysDictTypeList.forEach(item -> {
            QueryWrapper<SysDictData> sysDictDataQueryWrapper = new QueryWrapper<>();
            sysDictDataQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
            sysDictDataQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            sysDictDataQueryWrapper.eq(SQLConf.DICT_TYPE_UID, item.getId());
            sysDictDataQueryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
            List<SysDictData> list = list(sysDictDataQueryWrapper);
            String defaultValue = null;
            for (SysDictData sysDictData : list) {
                // 获取默认值
                if (sysDictData.getIsDefault() == SysConf.ONE) {
                    defaultValue = sysDictData.getDictValue();
                    break;
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put(SysConf.DEFAULT_VALUE, defaultValue);
            result.put(SysConf.LIST, list);
            map.put(item.getDictType(), result);
            redisUtil.setEx(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + item.getDictType(), JsonUtils.objectToJson(result).toString(), 1, TimeUnit.DAYS);
        });
        return map;
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data) {
        int row = dao.insert(data);
        if (row > 0) {
//            List<SysDictData> dictDatas = dao.selectDictDataByType(data.getDictType());
//            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = dao.selectById(dictCode);
            dao.deleteById(dictCode);
            LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDictData::getDictType,data.getDictType());
            List<SysDictData> dictDatas = dao.selectList(queryWrapper);
        }
    }

    /**
     * 根据参数类型的id查询对应的参数数据
     * @param dictCode
     * @return
     */
    @Override
    public SysDictData getDictDataById(Long dictCode) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<SysDictData>();
        if(StringUtils.isNotNull(dictCode)) {
            queryWrapper.eq(SysDictData::getId,dictCode);
        }
        SysDictData dSysDictData = this.baseMapper.selectOne(queryWrapper);
        return dSysDictData;
    }
}


package com.yan.bbs.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yan.bbs.entity.SysParams;
import com.yan.bbs.mapper.SysParamsMapper;
import com.yan.bbs.service.SysParamsService;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.RedisConf;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.exception.exceptionType.QueryException;
import com.yan.dd_common.global.ErrorCode;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yanshuang
 * @date 2023/5/1 22:09
 */
@Service
public class SysParamsServiceImpl extends SuperServiceImpl<SysParamsMapper, SysParams> implements SysParamsService {

    @Autowired
    SysParamsService sysParamsService;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public SysParams getSysParamsByKey(String paramsKey) {
        QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.PARAMS_KEY, paramsKey);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        SysParams sysParams = sysParamsService.getOne(queryWrapper);
        return sysParams;
    }

    /**
     * 通过参数名
     * 获取参数配置信息
     *
     * @param paramsKey
     * @return
     */
    @Override
    public String getSysParamsValueByKey(String paramsKey) {
        // 判断Redis中是否包含该key的数据
        String redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + paramsKey;
        String paramsValue = (String) redisUtil.get(redisKey);
        // 如果Redis中不存在，那么从数据库中获取
        if (StringUtils.isEmpty(paramsValue)) {
            SysParams sysParams = sysParamsService.getSysParamsByKey(paramsKey);
            // 如果数据库也不存在，将抛出异常【需要到找到 doc/数据库脚本 更新数据库中的 t_sys_params表】
            if (sysParams == null || StringUtils.isEmpty(sysParams.getParamsValue())) {
                throw new QueryException(ErrorCode.PLEASE_CONFIGURE_SYSTEM_PARAMS, MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
            }
            paramsValue = sysParams.getParamsValue();
            redisUtil.set(redisKey, paramsValue);
        }
        return paramsValue;
    }

}


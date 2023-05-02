package com.yan.bbs.service;

import com.yan.bbs.entity.SysParams;
import com.yan.bbs.service.Impl.SuperService;

/**
 * @author yanshuang
 * @date 2023/5/1 22:08
 */
public interface SysParamsService extends SuperService<SysParams> {


    /**
     * 通过 参数键名 获取参数配置
     *
     * @param paramsKey
     * @return
     */
    public SysParams getSysParamsByKey(String paramsKey);

    /**
     * 通过 参数键名 获取参数值
     *
     * @param paramsKey
     * @return
     */
    public String getSysParamsValueByKey(String paramsKey);

}

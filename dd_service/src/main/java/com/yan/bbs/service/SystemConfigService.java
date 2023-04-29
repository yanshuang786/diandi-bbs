package com.yan.bbs.service;

import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.model.Vo.SystemConfigVO;
import com.yan.dd_common.entity.SystemConfig;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 17:17
 */
public interface SystemConfigService extends SuperService<SystemConfig> {

    /**
     * 获取系统配置
     *
     * @return
     */
    public SystemConfig getConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param key
     * @return
     */
    public String cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     *
     * @param systemConfigVO
     * @return
     */
    public String editSystemConfig(SystemConfigVO systemConfigVO);
}


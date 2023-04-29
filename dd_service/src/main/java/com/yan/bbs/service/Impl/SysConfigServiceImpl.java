package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.SysConfig;
import com.yan.bbs.mapper.SysConfigMapper;
import com.yan.bbs.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.dd_common.constant.Constants;
import com.yan.dd_common.constant.UserConstants;
import com.yan.dd_common.core.page.PageDomain;
import com.yan.dd_common.core.page.TableSupport;
import com.yan.dd_common.core.text.Convert;
import com.yan.dd_common.exception.ServiceException;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/27 17:37
 */
@Service
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    private SysConfigMapper dao;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public IPage<SysConfig> selectConfigList(SysConfig config) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<SysConfig> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(config.getConfigName())) {
            queryWrapper.like(SysConfig::getConfigName,config.getConfigName());
        }
        if(StringUtils.isNotEmpty(config.getConfigType())) {
            queryWrapper.eq(SysConfig::getConfigType,config.getConfigType());
        }
        if(StringUtils.isNotEmpty(config.getConfigKey())) {
            queryWrapper.eq(SysConfig::getConfigKey,config.getConfigKey());
        }
        if(config.getParams().size() > 0){
            String a = (String) config.getParams().get("beginTime");
            String b = (String) config.getParams().get("endTime");
            queryWrapper.ge(SysConfig::getCreateTime,a);
            queryWrapper.le(SysConfig::getCreateTime,b);
        }
        return dao.selectPage(page, queryWrapper);
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
//    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId) {
        return dao.selectById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        // 在redis查询出来的redis值
        String configValue = Convert.toStr(redisUtil.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        // redis没有，去数据库查
        SysConfig retConfig = dao.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey));
        if (StringUtils.isNotNull(retConfig)) {
            //将查询出来存入redis
            redisUtil.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaOnOff() {
        // 去redis查询是否开启验证码
        String captchaOnOff = selectConfigByKey("sys.account.captchaOnOff");
        if (StringUtils.isEmpty(captchaOnOff)) {
            return true;
        }
        // 类型转换
        return Convert.toBool(captchaOnOff);
    }



    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        int row = dao.insert(config);
        if (row > 0) {
            redisUtil.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        int row = dao.updateById(config);
        if (row > 0) {
            redisUtil.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            dao.deleteById(configId);
            redisUtil.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = dao.selectList(new LambdaQueryWrapper<>());
        for (SysConfig config : configsList) {
            redisUtil.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisUtil.keys(Constants.SYS_CONFIG_KEY + "*");
        redisUtil.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey,config.getConfigKey());
        SysConfig info = dao.selectOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public SysConfig getConfig() {

        return null;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
}

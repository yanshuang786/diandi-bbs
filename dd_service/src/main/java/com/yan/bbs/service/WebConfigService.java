package com.yan.bbs.service;

import com.yan.bbs.entity.WebConfig;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.model.Vo.WebConfigVO;
import com.yan.dd_common.core.R;

/**
 * @author yanshuang
 * @date 2023/4/28 17:08
 */
public interface WebConfigService extends SuperService<WebConfig> {


    /**
     * 获取网站配置
     *
     * @return
     */
    WebConfig getWebConfig();

    /**
     * 通过显示列表获取配置
     *
     * @return
     */
    WebConfig getWebConfigByShowList();

    /**
     * 修改网站配置
     *
     * @param webConfigVO
     * @return
     */
    R editWebConfig(WebConfigVO webConfigVO);

    /**
     * 是否开启该登录方式【账号密码、码云、Github、QQ、微信】
     * @param loginType
     * @return
     */
    Boolean isOpenLoginType(String loginType);
}

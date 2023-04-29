package com.yan.bbs.service;

import com.yan.bbs.entity.WebNavbar;
import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.model.Vo.WebNavbarVO;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 17:03
 */
public interface WebNavbarService extends SuperService<WebNavbar> {


    /**
     * 获取所有门户导航栏
     *
     * @return
     */
    public List<WebNavbar> getAllList();

    /**
     * 增加导航栏
     * @param webNavbarVO
     * @return
     */
    public int addWebNavbar(WebNavbarVO webNavbarVO);

    /**
     * 修改
     * @param webNavbarVO
     * @return
     */
    public boolean editWebNavbar(WebNavbarVO webNavbarVO);

    /**
     * 删除
     * @param webNavbarVO
     * @return
     */
    public int deleteWebNavbar(WebNavbarVO webNavbarVO);

    /**
     * 更新门户状态
     * @param webNavbarVO
     * @return
     */
    int updateStatus(WebNavbarVO webNavbarVO);
}


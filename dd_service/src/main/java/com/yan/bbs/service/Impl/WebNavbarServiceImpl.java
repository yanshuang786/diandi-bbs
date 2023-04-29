package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.WebNavbar;
import com.yan.bbs.mapper.WebNavbarMapper;
import com.yan.dd_common.model.Vo.WebNavbarVO;
import com.yan.bbs.service.WebNavbarService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yan.dd_common.constant.Constants;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/28 17:04
 */
@Service
public class WebNavbarServiceImpl extends SuperServiceImpl<WebNavbarMapper, WebNavbar> implements WebNavbarService {

    @Autowired
    WebNavbarService webNavbarService;

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<WebNavbar> getAllList() {
        QueryWrapper<WebNavbar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.NAVBAR_LEVEL, Constants.STR_ONE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);

        List<WebNavbar> list = webNavbarService.list(queryWrapper);
        //获取所有的ID，去寻找他的子目录
        List<Integer> ids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotNull(item.getId())) {
                ids.add(item.getId());
            }
        });
        QueryWrapper<WebNavbar> childWrapper = new QueryWrapper<>();
        childWrapper.in(SQLConf.PARENT_UID, ids);
        childWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Collection<WebNavbar> childList = webNavbarService.list(childWrapper);

        // 给一级导航栏设置二级导航栏
        for (WebNavbar parentItem : list) {
            List<WebNavbar> tempList = new ArrayList<>();
            for (WebNavbar item : childList) {
                if (item.getParentUid().equals(parentItem.getId())) {
                    tempList.add(item);
                }
            }
            Collections.sort(tempList);
            parentItem.setChildWebNavbar(tempList);
        }
        return list;
    }

    @Override
    public int addWebNavbar(WebNavbarVO webNavbarVO) {
        //如果是一级菜单，将父ID清空
        if (webNavbarVO.getNavbarLevel() == 1) {
            webNavbarVO.setParentUid("");
        }
        WebNavbar webNavbar = new WebNavbar();
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(webNavbarVO, webNavbar, SysConf.STATUS);
        webNavbar.setStatus("1");
        if (webNavbarVO.getIsShow() == 0) {
            webNavbar.setIsShow("0");
        } else {
            webNavbar.setIsShow("1");
        }
        return this.baseMapper.insert(webNavbar);
    }

    @Override
    public boolean editWebNavbar(WebNavbarVO webNavbarVO) {
        //如果是一级菜单，将父ID清空
        if (webNavbarVO.getNavbarLevel() == 1) {
            webNavbarVO.setParentUid("");
        }
        WebNavbar webNavbar = webNavbarService.getById(webNavbarVO.getId());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(webNavbarVO, webNavbar);
        webNavbar.setUpdateTime(new Date());
        if (webNavbarVO.getIsShow() == 0) {
            webNavbar.setIsShow("0");
        } else {
            webNavbar.setIsShow("1");
        }
        return webNavbarService.updateById(webNavbar);
    }

    @Override
    public int deleteWebNavbar(WebNavbarVO webNavbarVO) {
        WebNavbar webNavbar = new WebNavbar();
        BeanUtils.copyProperties(webNavbarVO,webNavbar);
        webNavbar.setStatus("0");
        return this.baseMapper.updateById(webNavbar);
    }

    /**
     * 更新状态
     * @param webNavbarVO
     * @return
     */
    @Override
    public int updateStatus(WebNavbarVO webNavbarVO) {
        WebNavbar webNavbar = new WebNavbar();
        BeanUtils.copyProperties(webNavbarVO,webNavbar);
        if (webNavbarVO.getIsShow() == 0) {
            webNavbar.setIsShow("0");
        } else {
            webNavbar.setIsShow("1");
        }
        return this.baseMapper.updateById(webNavbar);
    }

}


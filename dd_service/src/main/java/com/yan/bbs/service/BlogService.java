package com.yan.bbs.service;

import com.yan.bbs.entity.vo.BlogVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yanshuang
 * @date 2023/4/28 15:25
 */
public interface BlogService extends IService<Blog> {

    /**
     * 获取最新博客
     * @param currentPage 页数
     * @param pageSize 数量
     * @return 博客详情
     */
    IPage<Blog> getNewBlog(Long currentPage, Long pageSize);

    /**
     * 根据ID获取博客详情
     * @param id 博客ID
     * @return 博客详情
     */
    public R getBlogById(Integer id);

    R addBlog(BlogVO blogVO);

    /**
     * 获取博客列表
     *
     * @param blogVO
     * @return
     */
    public IPage<Blog> getPageList(BlogVO blogVO);

    /**
     * 删除和博客标签有关的Redis缓存
     */
    public void deleteRedisByBlogTag();

    /**
     * 删除和博客有关的Redis缓存
     */
    public void deleteRedisByBlog();

    /**
     * 删除和博客分类有关的Redis缓存
     */
    public void deleteRedisByBlogSort();

    /**
     * 获取审查博客列表
     * @param blogVO
     * @return
     */
    public IPage<Blog> getPageAuditList(BlogVO blogVO);

    /**
     * 获取用户博客
     * @param request 请求信息
     * @param currentPage 当前页数
     * @param pageSize 每页显示数目
     * @return 分页数据
     */
    IPage<Blog> getBlogListByUser(HttpServletRequest request, Long currentPage, Long pageSize, Long userId);

    /**
     * 审核博客
     * @param blogVO 审核信息
     * @return
     */
    R editAuditBlog(BlogVO blogVO);

    /**
     * 更新博客
     * @param blogVO
     * @return
     */
    R updateBlog(BlogVO blogVO, Long id);

    /**
     * 删除博客
     * @param blogId 博客ID
     * @return
     */
    R deleteById(Integer blogId);

    /**
     * mogu-search调用获取博客的接口[包含内容]
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public IPage<Blog> getBlogBySearch(Long currentPage, Long pageSize);

}


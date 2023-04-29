package com.yan.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yan.bbs.entity.vo.BbsSortVO;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.BlogSort;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 16:43
 */
public interface BlogSortService extends IService<BlogSort> {
    /**
     * 获取博客分类列表
     *
     * @param blogSortVO
     * @return
     */
    public IPage<BlogSort> getPageList(BbsSortVO blogSortVO);

    /**
     * 获取博客分类列表
     *
     * @return
     */
    public List<BlogSort> getList();

    /**
     * 新增博客分类
     *
     * @param blogSortVO
     */
    public R addBlogSort(BbsSortVO blogSortVO);

    /**
     * 编辑博客分类
     *
     * @param blogSortVO
     */
    public R editBlogSort(BbsSortVO blogSortVO);

    /**
     * 批量删除博客分类
     *
     * @param blogSortVoList
     */
    public R deleteBatchBlogSort(List<BbsSortVO> blogSortVoList);

    /**
     * 置顶博客分类
     *
     * @param blogSortVO
     */
    public R stickBlogSort(BbsSortVO blogSortVO);

    /**
     * 通过点击量排序博客
     *
     * @return
     */
    public R blogSortByClickCount();

    /**
     * 通过引用量排序博客
     *
     * @return
     */
    public R blogSortByCite();

    /**
     * 获取排序最高的一个博客分类
     *
     * @return
     */
    public BlogSort getTopOne();
}


package com.yan.bbs.service;

import com.yan.dd_common.model.Vo.TagVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Tag;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/28 16:39
 */
public interface TagService extends IService<Tag> {
    /**
     * 获取博客标签列表
     *
     * @param tagVO
     * @return
     */
    public IPage<Tag> getPageList(TagVO tagVO);

    /**
     * web 获取全部博客标签列表
     *
     * @return
     */
    public List<Tag> getList();

    /**
     * 新增博客标签
     *
     * @param tagVO
     */
    public R addTag(TagVO tagVO);

    /**
     * 编辑博客标签
     *
     * @param tagVO
     */
    public R editTag(TagVO tagVO);

    /**
     * 批量删除博客标签
     *
     * @param tagVOList
     */
    public R deleteBatchTag(List<TagVO> tagVOList);

    /**
     * 置顶博客标签
     *
     * @param tagVO
     */
    public R stickTag(TagVO tagVO);

    /**
     * 通过点击量排序博客
     *
     * @return
     */
    public R tagSortByClickCount();

    /**
     * 通过引用量排序博客
     *
     * @return
     */
    public R tagSortByCite();

    /**
     * 获取热门标签
     *
     * @return
     */
    public List<Tag> getHotTag(Integer hotTagCount);

    /**
     * 获取一个排序最高的标签
     *
     * @return
     */
    public Tag getTopTag();
}


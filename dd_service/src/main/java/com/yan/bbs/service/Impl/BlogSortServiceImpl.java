package com.yan.bbs.service.Impl;

import com.yan.bbs.entity.vo.BbsSortVO;
import com.yan.bbs.mapper.BlogSortMapper;
import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.BlogSortService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.entity.BlogSort;
import com.yan.dd_common.enums.EPublish;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/28 16:44
 */
@Service
public class BlogSortServiceImpl extends ServiceImpl<BlogSortMapper, BlogSort> implements BlogSortService {

    @Autowired
    private BlogSortMapper dao;

    @Autowired
    private BlogService blogService;


    /**
     * 查询所有
     * @return 博客分类
     */
    @Override
    public List<BlogSort> getList() {
        LambdaQueryWrapper<BlogSort> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogSort::getStatus, StatusCode.ENABLE);
        queryWrapper.orderByDesc(BlogSort::getSort);
        return dao.selectList(queryWrapper);
    }



    /**
     * 分页查询
     * @param blogSortVO
     * @return
     */
    @Override
    public IPage<BlogSort> getPageList(BbsSortVO blogSortVO) {
        QueryWrapper<BlogSort> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(blogSortVO.getKeyword()) && !StringUtils.isEmpty(blogSortVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.SORT_NAME, blogSortVO.getKeyword().trim());
        }
        // 排序
        if(StringUtils.isNotEmpty(blogSortVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogSortVO.getOrderByAscColumn()).toString());
            queryWrapper.orderByAsc(column);
        }else if(StringUtils.isNotEmpty(blogSortVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogSortVO.getOrderByDescColumn()).toString());
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        Page<BlogSort> page = new Page<>();
        page.setCurrent(blogSortVO.getCurrentPage());
        page.setSize(blogSortVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<BlogSort> pageList = page(page, queryWrapper);
        return pageList;
    }

    /**
     * 添加分类
     * @param blogSortVO
     * @return
     */
    @Override
    public R addBlogSort(BbsSortVO blogSortVO) {
        // 判断添加的分类是否存在
        LambdaQueryWrapper<BlogSort> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogSort::getSortName, blogSortVO.getSortName());
        queryWrapper.eq(BlogSort::getStatus, StatusCode.ENABLE);
        BlogSort tempSort = getOne(queryWrapper);
        if (tempSort != null) {
            return R.error(MessageConf.ENTITY_EXIST);
        }

        BlogSort blogSort = new BlogSort();
        BeanUtils.copyProperties(blogSortVO,blogSort);
        blogSort.setStatus(StatusCode.ENABLE);
        dao.insert(blogSort);
        return R.success(MessageConf.INSERT_SUCCESS);
    }

    /**
     * 编辑分类
     * @param blogSortVO
     * @return
     */
    @Override
    public R editBlogSort(BbsSortVO blogSortVO) {
        BlogSort blogSort = getById(blogSortVO.getId());
        /**
         * 判断需要编辑的博客分类是否存在
         */
        if (!blogSort.getSortName().equals(blogSortVO.getSortName())) {
            LambdaQueryWrapper<BlogSort> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BlogSort::getSortName, blogSortVO.getSortName());
            queryWrapper.eq(BlogSort::getStatus, StatusCode.ENABLE);
            BlogSort tempSort = getOne(queryWrapper);
            if (tempSort != null) {
                return R.error(MessageConf.ENTITY_EXIST);
            }
        }
        BeanUtils.copyProperties(blogSortVO,blogSort);
        blogSort.setStatus(StatusCode.ENABLE);
        blogSort.setUpdateTime(new Date());
        dao.updateById(blogSort);
        // 删除和博客相关的Redis缓存
        blogService.deleteRedisByBlogSort();
        return R.success(MessageConf.UPDATE_SUCCESS);
    }

    /**
     * 批量删除
     * @param blogSortVoList
     * @return
     */
    @Override
    public R deleteBatchBlogSort(List<BbsSortVO> blogSortVoList) {
        if (blogSortVoList.size() <= 0) {
            return R.error(MessageConf.PARAM_INCORRECT);
        }
        List<Integer> uids = new ArrayList<>();

        blogSortVoList.forEach(item -> {
            uids.add(item.getId());
        });

        // 判断要删除的分类，是否有博客
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        blogQueryWrapper.in(SQLConf.BLOG_SORT_UID, uids);
        Integer blogCount = blogService.count(blogQueryWrapper);
        if (blogCount > 0) {
            return R.error(MessageConf.BLOG_UNDER_THIS_SORT);
        }
        Collection<BlogSort> blogSortList = listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(StatusCode.DISABLED);
        });
        Boolean save = updateBatchById(blogSortList);
        if (save) {
            // 删除和博客相关的Redis缓存
            blogService.deleteRedisByBlogSort();
            return R.success(MessageConf.DELETE_SUCCESS);
        } else {
            return R.error(MessageConf.DELETE_FAIL);
        }
    }


    @Override
    public R stickBlogSort(BbsSortVO blogSortVO) {
        BlogSort blogSort = getById(blogSortVO.getId());

        //查找出最大的那一个
        QueryWrapper<BlogSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.SORT);
        Page<BlogSort> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<BlogSort> pageList = page(page, queryWrapper);
        List<BlogSort> list = pageList.getRecords();
        BlogSort maxSort = list.get(0);

        if (StringUtils.isEmpty(maxSort.getId()+"")) {
            return R.error(MessageConf.PARAM_INCORRECT);
        }
        if (maxSort.getId().equals(blogSort.getId())) {
            return R.error(MessageConf.THIS_SORT_IS_TOP);
        }
        Integer sortCount = maxSort.getSort() + 1;
        blogSort.setSort(sortCount);
        blogSort.setUpdateTime(new Date());
        dao.updateById(blogSort);
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public R blogSortByClickCount() {
        QueryWrapper<BlogSort> queryWrapper = new QueryWrapper();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 按点击从高到低排序
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        List<BlogSort> blogSortList = list(queryWrapper);
        // 设置初始化最大的sort值
        Integer maxSort = blogSortList.size();
        for (BlogSort item : blogSortList) {
            item.setSort(item.getClickCount());
            item.setUpdateTime(new Date());
        }
        updateBatchById(blogSortList);
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public R blogSortByCite() {
        // 定义Map   key：tagUid,  value: 引用量
        Map<Integer, Integer> map = new HashMap<>();
        QueryWrapper<BlogSort> blogSortQueryWrapper = new QueryWrapper<>();
        blogSortQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<BlogSort> blogSortList = list(blogSortQueryWrapper);
        // 初始化所有标签的引用量
        blogSortList.forEach(item -> {
            map.put(item.getId(), 0);
        });
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        // 过滤content字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> blogList = blogService.list(queryWrapper);

        blogList.forEach(item -> {
            Integer blogSortUid = item.getBlogSortId();
            if (map.get(blogSortUid) != null) {
                Integer count = map.get(blogSortUid) + 1;
                map.put(blogSortUid, count);
            } else {
                map.put(blogSortUid, 0);
            }
        });

        blogSortList.forEach(item -> {
            item.setSort(map.get(item.getId()));
            item.setUpdateTime(new Date());
        });
        updateBatchById(blogSortList);
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public BlogSort getTopOne() {
        QueryWrapper<BlogSort> blogSortQueryWrapper = new QueryWrapper<>();
        blogSortQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        blogSortQueryWrapper.last(SysConf.LIMIT_ONE);
        blogSortQueryWrapper.orderByDesc(SQLConf.SORT);
        BlogSort blogSort = getOne(blogSortQueryWrapper);
        return blogSort;
    }
}
package com.yan.bbs.service.Impl;

import com.yan.bbs.mapper.TagMapper;
import com.yan.dd_common.model.Vo.TagVO;
import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.TagService;
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
import com.yan.dd_common.entity.Tag;
import com.yan.dd_common.enums.EPublish;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/28 16:40
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TagMapper dao;

    /**
     * 分页查询标签，模糊查询
     *
     * @param tagVO
     * @return
     */
    @Override
    public IPage<Tag> getPageList(TagVO tagVO) {
        Page<Tag> page = new Page<>();
        page.setCurrent(tagVO.getCurrentPage());
        page.setSize(tagVO.getPageSize());

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(tagVO.getKeyword())) {
            queryWrapper.like(SQLConf.CONTENT, tagVO.getKeyword().trim());
        }

        queryWrapper.eq(SQLConf.DEL_FLAG, EStatus.ENABLE);
        if(StringUtils.isNotEmpty(tagVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(tagVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if(StringUtils.isNotEmpty(tagVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(tagVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<Tag> pageList = dao.selectPage(page, queryWrapper);
        return pageList;
    }


    /**
     * web端添加博客
     * @return
     */
    @Override
    public List<Tag> getList() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        List<Tag> tagList = dao.selectList(queryWrapper);
        return tagList;
    }

    /**
     * 添加
     * @param tagVO
     * @return
     */
    @Override
    public R addTag(TagVO tagVO) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getContent, tagVO.getContent());
        queryWrapper.eq(Tag::getStatus, StatusCode.ENABLE);
        Tag tempTag = getOne(queryWrapper);
        if (tempTag != null) {
            return R.error(MessageConf.ENTITY_EXIST);
        }
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagVO,tag);
        tag.setClickCount(0);
        tag.setStatus(StatusCode.ENABLE);
        dao.insert(tag);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return R.success(MessageConf.INSERT_SUCCESS);
    }

    /**
     * 修改
     * @param tagVO
     * @return
     */
    @Override
    public R editTag(TagVO tagVO) {
        Tag tag = dao.selectById(tagVO.getId());

        if (tag != null && !tag.getContent().equals(tagVO.getContent())) {
            LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getContent, tagVO.getContent());
            queryWrapper.eq(Tag::getStatus, StatusCode.ENABLE);
            Tag tempTag = dao.selectOne(queryWrapper);
            if (tempTag != null) {
                return R.error(MessageConf.ENTITY_EXIST);
            }
        }

        BeanUtils.copyProperties(tagVO,tag);
        tag.setStatus(StatusCode.ENABLE);
        tag.setUpdateTime(new Date());
        dao.updateById(tag);
        // 删除和标签相关的博客缓存
        blogService.deleteRedisByBlogTag();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return R.success(MessageConf.UPDATE_SUCCESS);
    }

    /**
     * 删除
     * @param tagVOList
     * @return
     */
    @Override
    public R deleteBatchTag(List<TagVO> tagVOList) {
        if (tagVOList.size() <= 0) {
            return R.error(MessageConf.PARAM_INCORRECT);
        }
        List<Integer> ids = new ArrayList<>();
        tagVOList.forEach(item -> {
            ids.add(item.getId());
        });

        // 判断要删除的分类，是否有博客
        LambdaQueryWrapper<Blog> blogQueryWrapper = new LambdaQueryWrapper<>();
        blogQueryWrapper.eq(Blog::getStatus, EStatus.ENABLE);
        blogQueryWrapper.in(Blog::getTagId, ids);
        Integer blogCount = blogService.count(blogQueryWrapper);
        if (blogCount > 0) {
            return R.error(MessageConf.BLOG_UNDER_THIS_TAG);
        }

        Collection<Tag> tagList = dao.selectBatchIds(ids);

        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(StatusCode.DISABLED);
        });

        Boolean save = updateBatchById(tagList);
        // 删除和标签相关的博客缓存
        blogService.deleteRedisByBlogTag();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        if (save) {
            return R.success(MessageConf.DELETE_SUCCESS);
        } else {
            return R.error(MessageConf.DELETE_FAIL);
        }
    }

    /**
     * 置顶 在最大值基础上加一
     * @param tagVO
     * @return
     */
    @Override
    public R stickTag(TagVO tagVO) {
        Tag tag = dao.selectById(tagVO.getId());

        //查找出最大的那一个
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.SORT);
        Page<Tag> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<Tag> pageList = dao.selectPage(page, queryWrapper);
        List<Tag> list = pageList.getRecords();
        Tag maxTag = list.get(0);

        if (StringUtils.isEmpty( maxTag.getId()+"")) {
            return R.error(MessageConf.PARAM_INCORRECT);
        }
        if (maxTag.getId().equals(tag.getId())) {
            return R.error(MessageConf.THIS_TAG_IS_TOP);
        }

        Integer sortCount = maxTag.getSort() + 1;

        tag.setSort(sortCount);
        tag.setUpdateTime(new Date());
        dao.updateById(tag);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    /**
     * 通过点击量排序标签
     * @return
     */
    @Override
    public R tagSortByClickCount() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 按点击从高到低排序
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        List<Tag> tagList = dao.selectList(queryWrapper);
        // 设置初始化最大的sort值
        Integer maxSort = tagList.size();
        for (Tag item : tagList) {
            item.setSort(item.getClickCount());
            item.setCreateTime(new Date());
        }
        updateBatchById(tagList);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public R tagSortByCite() {
        // 定义Map   key：tagUid,  value: 引用量
        Map<Integer, Integer> map = new HashMap<>();
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Tag> tagList = list(tagQueryWrapper);
        // 初始化所有标签的引用量
        tagList.forEach(item -> {
            map.put(item.getId(), 0);
        });

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        // 过滤content字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> blogList = blogService.list(queryWrapper);


//        blogList.forEach(item -> {
//            Integer tagUids = item.getTagUid();
//            List<Integer> tagUidList = StringUtils.changeStringToString(tagUids, SysConf.FILE_SEGMENTATION);
//            for (Integer tagUid : tagUidList) {
//                if (map.get(tagUid) != null) {
//                    Integer count = map.get(tagUid) + 1;
//                    map.put(tagUid, count);
//                } else {
//                    map.put(tagUid, 0);
//                }
//            }
//        });

        tagList.forEach(item -> {
            item.setSort(map.get(item.getId()));
            item.setUpdateTime(new Date());
        });
        updateBatchById(tagList);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return R.success(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public List<Tag> getHotTag(Integer hotTagCount) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        Page<Tag> page = new Page<>();
        page.setCurrent(1);
        page.setSize(hotTagCount);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        IPage<Tag> pageList = page(page, queryWrapper);
        return pageList.getRecords();
    }

    @Override
    public Tag getTopTag() {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        tagQueryWrapper.last(SysConf.LIMIT_ONE);
        tagQueryWrapper.orderByDesc(SQLConf.SORT);
        Tag tag = getOne(tagQueryWrapper);
        return tag;
    }

    /**
     * TODO
     * 删除Redis中的友链列表
     */
    private void deleteRedisBlogTagList() {
        // 删除Redis中的BLOG_LINK
//        Set<String> keys = redisUtil.keys(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + "*");
//        redisUtil.delete(keys);
    }
}


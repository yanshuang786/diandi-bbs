package com.yan.bbs.service.Impl;

import com.yan.dd_common.entity.User;
import com.yan.bbs.entity.UserLike;
import com.yan.bbs.entity.vo.BlogVO;
import com.yan.bbs.mapper.BlogMapper;
import com.yan.bbs.mapper.BlogSortMapper;
import com.yan.bbs.mapper.TagMapper;
import com.yan.bbs.service.BlogLikeService;
import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.SysConfigService;
import com.yan.bbs.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yan.dd_common.constant.*;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.entity.BlogSort;
import com.yan.dd_common.entity.Tag;
import com.yan.dd_common.enums.*;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanshuang
 * @date 2023/4/28 15:31
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogMapper dao;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogSortMapper blogSortMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlogLikeService blogLikeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public IPage<Blog> getNewBlog(Long currentPage, Long pageSize) {
        IPage<Blog> pageList = new Page<>(currentPage,pageSize);


        // 1.获取'NEW_BLOG'文章个数
        Long size = 0L;
        try {
            size = redisUtil.getListSize(RedisConf.NEW_BLOG);
        } catch (Exception e) {
        }
        if(size >= currentPage*pageSize) {
            long start = 0;
            long end = 0;
            start += pageSize*(currentPage-1);
            end += pageSize*currentPage-1;
            List<String> strings = redisUtil.lRange(RedisConf.NEW_BLOG, start, end);
            List<Blog> blogList = new ArrayList<>();
            for(String s: strings) {
                Blog blog = JSONObject.parseObject(s, Blog.class);
                blogList.add(blog);
            }
            setBlogLike(blogList);
            pageList.setRecords(blogList);

            return pageList;
        }

        // 一、主页内容主要包含文章列表和点赞数量
        Page<Blog> page = new Page<>(currentPage,pageSize);
        // 1.1、如果没有足够的文章去数据库查询
        LambdaQueryWrapper<Blog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Blog::getStatus, EStatus.ENABLE);
        lambdaQueryWrapper.eq(Blog::getIsPublish, EPublish.PUBLISH);
        lambdaQueryWrapper.eq(Blog::getIsAudit, StatusCode.ENABLE);
        lambdaQueryWrapper.orderByDesc(Blog::getCreateTime);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        lambdaQueryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        pageList = dao.selectPage(page, lambdaQueryWrapper);
        List<Blog> list = pageList.getRecords();


        if (list.size() <= 0) {
            return pageList;
        }
        page.setSize(list.size());
        // 1.2、文章列表（文章ID，文章标题，文章简介，作者头像，作者名，浏览量，点赞数量（数据库中），文章封面，创建时间）
        list = setIndexBlog(list);

        // 点赞数量

        pageList.setRecords(list);

        // 获取点赞数量（Redis中点赞数量+数据库中的数量）
        getIndexBlogLikeCount(list);


        //将从最新博客缓存到redis中
        for(Blog blog : list) {
            redisUtil.lRightPush(RedisConf.NEW_BLOG,JSONObject.toJSONString(blog));
        }

        return pageList;
    }

    /**
     * 获取当前用户是否点赞，和文章点赞个数
     * @param list
     */
    private void setBlogLike(List<Blog> list) {
        for (Blog item : list) {
            // 点赞数量
            HashSet<Integer> userIdSet = new HashSet<>();
            try {
                userIdSet = JSONObject.parseObject(redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY, String.valueOf(item.getId())).toString(),HashSet.class);
            } catch (Exception e) {}

            item.setLikeCount(item.getLikeCount()+userIdSet.size());
        }
        // set保存着当前文章的点赞用户ID
        getIndexBlogLikeCount(list);
    }

    /**
     * 第一次查询，要查Redis和Mysql
     * 第二次查，只需要查询，Redis中的点赞
     * @param list
     */
    private void getIndexBlogLikeCount(List<Blog> list) {
        try {
            // todo
//            HttpServletRequest request = RequestHolder.getRequest();
//            LoginUser loginUser = (LoginUser) request.getAttribute("user");
            User user = new User();
            // 当前用户点赞的文章
            List<UserLike> userLikeBlog = blogLikeService.getUserLikeBlog(user.getUserId());
            list.stream().map(m -> {
                userLikeBlog.stream().filter(m2 ->
                        m.getId().equals(m2.getBlogId())).forEach(m2 -> {
                    m.setIsLike(true);
                    // 点赞数不加，保存到数据库的数据已经加过了。
                });
                return m;
            }).collect(Collectors.toList());
            list.forEach(item -> {
                HashSet<Integer> userIdSet = new HashSet<>();
                redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY,String.valueOf(item.getId()));
                try {
                    userIdSet = JSONObject.parseObject(redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY, String.valueOf(item.getId())).toString(),HashSet.class);
                    if(userIdSet.contains(Integer.valueOf(Math.toIntExact(user.getUserId())))) {
                        item.setIsLike(true);
                    }
                } catch (Exception e) { }
            });
        } catch (Exception e) {

        }
    }


    private List<Blog> setIndexBlog(List<Blog> list) {
        final StringBuffer fileUids = new StringBuffer();
        for (Blog item : list) {
            // 设置用户头像
            if (StringUtils.isNotEmpty(userService.getUserById(item.getUserId()).getAvatar())) {
                item.setAvatar(userService.getUserById(item.getUserId()).getAvatar());
            }
            HashSet<Integer> userIdSet = new HashSet<>();
            try {
                userIdSet = JSONObject.parseObject(redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY, String.valueOf(item.getId())).toString(),HashSet.class);
            } catch (Exception e) {}

            item.setLikeCount(item.getLikeCount()+userIdSet.size());
        }
        return list;
    }

    /**
     * 设置博客的分类标签和内容
     *
     * @param list 查询的博客
     * @return 博客的详情
     */
    private List<Blog> setBlog(List<Blog> list) {
        final StringBuffer fileUids = new StringBuffer();
        List<Integer> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();

        list.forEach(item -> {
            if (StringUtils.isNull(item.getBlogSortId())) {
                sortUids.add(item.getBlogSortId());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortId()+"")) {
                tagUids.add(item.getBlogSortId()+"");
            }

        });
        String pictureList = null;

        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortMapper.selectBatchIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagMapper.selectBatchIds(tagUids);
        }

        Map<Integer, BlogSort> sortMap = new HashMap<>();
        Map<Integer, Tag> tagMap = new HashMap<>();

        sortList.forEach(item -> {
            sortMap.put(item.getId(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getId(), item);
        });


        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNull(item.getBlogSortId())) {
                item.setBlogSort(sortMap.get(item.getBlogSortId()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getBlogSortId()+"")) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getBlogSortId()+"", SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    if (tagMap.get(tag) != null) {
                        tagListTemp.add(tagMap.get(tag));
                    }
                });
                item.setTagList(tagListTemp);
            }

            // 设置用户头像
            if (StringUtils.isNotEmpty(userService.getUserById(item.getUserId()).getAvatar())) {
                item.setAvatar(userService.getUserById(item.getUserId()).getAvatar());
            }
        }
        return list;
    }

    /**
     * 根据ID获取博客详情
     * @param id 博客ID
     * @return
     */
    @Override
    public R getBlogById(Integer id) {
        Blog blog = null;
//        if (StringUtils.isNull(id)) {
//           return R.success(blog);
//        }
        if (StringUtils.isNotNull(id)) {
            blog = dao.selectById(id);
        } else {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            queryWrapper.last(SysConf.LIMIT_ONE);
            blog = this.getOne(queryWrapper);
        }
        if (blog == null || blog.getStatus() == StatusCode.DISABLED || EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
            return R.error("该文章已下架或被删除");
        }

        // 设置对象头像
        blog.setAvatar(userService.getUserById(blog.getUserId()).getAvatar());
        // 设置文章版权申明
        setBlogCopyright(blog);

        //设置博客标签
        setTagByBlog(blog);

        //获取分类
        setSortByBlog(blog);

        //设置博客标题图
//        setPhotoListByBlog(blog);

        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = stringRedisTemplate.opsForValue().get("BLOG_CLICK:"  + "#" + blog.getId());

        if (StringUtils.isEmpty(jsonResult)) {

            //给博客点击数增加
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            saveOrUpdate(blog);

            //将该用户点击记录存储到redis中, 24小时后过期
//            stringRedisTemplate.opsForValue().set(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON  + Constants.SYMBOL_WELL + blog.getId(), blog.getClickCount().toString(),
//                    24, TimeUnit.HOURS);
        }
        return R.success(blog);
    }


    private void setBlogCopyright(Blog blog) {
        blog.setCopyright("本文由"+blog.getAuthor() +"原创，"+ "不代表本站观点。" + " 如需转载请注明来自点滴社区：https://diandicoding.com");
    }
    /**
     * web端 添加博客
     * @param blogVO 博客详情
     * @return 结果
     */
    @Override
    public R addBlog(BlogVO blogVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        // step02:
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVO, blog);
        User user = userService.getById(request.getAttribute(SysConf.USER_UID).toString());
        blog.setUserId(user.getUserId());
        // 如果是原创，作者为用户的昵称
        if (EOriginal.ORIGINAL.equals(blogVO.getIsOriginal())) {
            // 获取用户信息
            assert request != null;
            if(StringUtils.isNotEmpty(user.getNickName())) {
                blog.setAuthor(user.getNickName());
            } else {
                return R.error("请创建用户昵称");
            }
            blog.setAdminId(user.getUserId().intValue());
            // 文章出处
            blog.setArticlesPart("点滴论坛");
        } else {
            // 不是原创直接设置
            blog.setAuthor(blogVO.getAuthor());
            blog.setArticlesPart(blogVO.getArticlesPart());
        }

        // 用户投稿查看是否审核
        String cacheObject = "1";
        try {
            cacheObject = redisUtil.getCacheObject("sys_config:sys_blog_audit");
        } catch (Exception e) {
            configService.resetConfigCache();
            cacheObject = redisUtil.getCacheObject("sys_config:sys_blog_audit");
        }
        if(cacheObject == null) {
            configService.resetConfigCache();
            cacheObject = redisUtil.getCacheObject("sys_config:sys_blog_audit");
        }
        if (cacheObject.equals(StatusCode.ENABLE)) {
            // 不审核
            blog.setIsAudit(StatusCode.ENABLE);
        } else {
            // 审核
            blog.setIsAudit(StatusCode.DISABLED);
        }

        blog.setStatus(StatusCode.ENABLE);
        blog.setPhotoUrl(blogVO.getFileId());
        // 文章出处为用户投稿
        blog.setArticlesPart(StatusCode.ENABLE);
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());
        blog.setIsPublish("1");
        Boolean isSave = this.save(blog);

        //保存成功后，需要发送消息给MQ，通知 es 和 redis 更新
        if(isSave && cacheObject.equals(StatusCode.ENABLE)) {
            Blog blog1 = dao.selectOne(new LambdaQueryWrapper<Blog>()
                    .eq(Blog::getUserId, user.getUserId())
                    .eq(Blog::getTitle, blogVO.getTitle())
                    .eq(Blog::getSummary, blogVO.getSummary())
            );
            updateEsAndRedis(blog1, 1);
        }
        return R.success("创建成功",blog.getId());
    }

    /**
     * 分页查询全部博客
     * @param blogVO
     * @return
     */
    @Override
    public IPage<Blog> getPageList(BlogVO blogVO) {

        // step1: 查询所有
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        // 构建搜索条件
        if (StringUtils.isNotEmpty(blogVO.getKeyword()) && !StringUtils.isEmpty(blogVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, blogVO.getKeyword().trim());
        }
        if (!StringUtils.isNull(blogVO.getTagId())) {
            // 标签有好多种类
            queryWrapper.like(SQLConf.TAG_UID, blogVO.getTagId());
        }
        if (!StringUtils.isNull(blogVO.getBlogSortId())) {
            // 分类只有一个
            queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogVO.getBlogSortId());
        }
        if (!StringUtils.isEmpty(blogVO.getLevelKeyword())) {
            queryWrapper.eq(SQLConf.LEVEL, blogVO.getLevelKeyword());
        }
        if (!StringUtils.isEmpty(blogVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, blogVO.getIsPublish());
        }
        if (!StringUtils.isEmpty(blogVO.getIsOriginal())) {
            queryWrapper.eq(SQLConf.IS_ORIGINAL, blogVO.getIsOriginal());
        }
        if (!StringUtils.isEmpty(blogVO.getType())) {
            queryWrapper.eq(SQLConf.TYPE, blogVO.getType());
        }
        //分页
        Page<Blog> page = new Page<>();
        page.setCurrent(blogVO.getCurrentPage());
        page.setSize(blogVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE); // 上架和下架
        queryWrapper.eq(SQLConf.IS_AUDIT,EStatus.ENABLE); // 审核通过
        if (StringUtils.isNotEmpty(blogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogVO.getOrderByAscColumn()).toString());
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(blogVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogVO.getOrderByDescColumn()).toString());
            queryWrapper.orderByDesc(column);
        } else {
            // 是否启动排序字段
            if (blogVO.getUseSort() == 0) {
                // 未使用，默认按时间倒序
                queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
            } else {
                // 使用，默认按sort值大小倒序
                queryWrapper.orderByDesc(SQLConf.SORT);
            }
        }
        IPage<Blog> pageList = dao.selectPage(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        if (list.size() == 0) {
            return pageList;
        }


        // step02: 查询博客对应的分类id、标签id、图片id
        final StringBuffer fileids = new StringBuffer();
        List<Integer> sortids = new ArrayList<>();
        List<Integer> tagids = new ArrayList<>();
        list.forEach(item -> {
//            if (StringUtils.isNotEmpty(item.getFileId())) {
//                fileids.append(item.getFileId() + SysConf.FILE_SEGMENTATION);
//            }
            if (StringUtils.isNotNull(item.getBlogSortId())) {
                sortids.add(item.getBlogSortId());
            }
            if (StringUtils.isNotNull(item.getTagId())) {
                List<Integer> tagUidsTemp = StringUtils.changeStringToInt(item.getTagId(), SysConf.FILE_SEGMENTATION);
                for (Integer itemTagUid : tagUidsTemp) {
                    tagids.add(itemTagUid);
                }
            }
        });
        // 2.1查询图片
        String pictureList = null;
        if (fileids != null) {
//            pictureList = this.pictureFeignClient.getPicture(fileids.toString(), SysConf.FILE_SEGMENTATION);
        }
//        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        // 2.2查询分类
        Collection<BlogSort> sortList = new ArrayList<>();
        // 2.3查询标签
        Collection<Tag> tagList = new ArrayList<>();
        if (sortids.size() > 0) {
            sortList = blogSortMapper.selectBatchIds(sortids);
        }
        if (tagids.size() > 0) {
            tagList = tagMapper.selectBatchIds(tagids);
        }


        // 2.4 预备操作
        Map<Integer, BlogSort> sortMap = new HashMap<>();
        Map<Integer, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();


        sortList.forEach(item -> {
            sortMap.put(item.getId(), item);
        });
        tagList.forEach(item -> {
            tagMap.put(item.getId(), item);
        });
//        picList.forEach(item -> {
//            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
//        });


        // step3: 设置详细信息
        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNotNull(item.getBlogSortId())) {
                item.setBlogSort(sortMap.get(item.getBlogSortId()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagId())) {
                List<Integer> tagUidsTemp = StringUtils.changeStringToInt(item.getTagId(), SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
//            if (StringUtils.isNotEmpty(item.getFileId())) {
//                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileId(), SysConf.FILE_SEGMENTATION);
//                List<String> pictureListTemp = new ArrayList<>();
//
//                pictureUidsTemp.forEach(picture -> {
//                    pictureListTemp.add(pictureMap.get(picture));
//                });
//                item.setPhotoList(pictureListTemp);
//            }
        }

        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public void deleteRedisByBlogTag() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        // 删除博客相关缓存
        deleteRedisByBlog();
    }

    @Override
    public void deleteRedisByBlog() {
        // 删除博客相关缓存
        redisUtil.delete(RedisConf.NEW_BLOG);
        redisUtil.delete(RedisConf.HOT_BLOG);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FIRST);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.SECOND);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.THIRD);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FOURTH);
    }

    @Override
    public void deleteRedisByBlogSort() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
        // 删除博客相关缓存
        deleteRedisByBlog();
    }

    /**
     * admin
     * 分页查询全部需要审核博客
     * @param blogVO
     * @return
     */
    @Override
    public IPage<Blog> getPageAuditList(BlogVO blogVO) {

        // step1: 查询所有
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        // 构建搜索条件
        if (StringUtils.isNotEmpty(blogVO.getKeyword()) && !StringUtils.isEmpty(blogVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, blogVO.getKeyword().trim());
        }
        if (!StringUtils.isNull(blogVO.getTagId())) {
            // 标签有好多种类
            queryWrapper.like(SQLConf.TAG_UID, blogVO.getTagId());
        }
        if (!StringUtils.isNull(blogVO.getBlogSortId())) {
            // 分类只有一个
            queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogVO.getBlogSortId());
        }
        if (!StringUtils.isEmpty(blogVO.getLevelKeyword())) {
            queryWrapper.eq(SQLConf.LEVEL, blogVO.getLevelKeyword());
        }
        if (!StringUtils.isEmpty(blogVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, blogVO.getIsPublish());
        }
        if (!StringUtils.isEmpty(blogVO.getIsOriginal())) {
            queryWrapper.eq(SQLConf.IS_ORIGINAL, blogVO.getIsOriginal());
        }
        if (!StringUtils.isEmpty(blogVO.getType())) {
            queryWrapper.eq(SQLConf.TYPE, blogVO.getType());
        }
        //分页
        Page<Blog> page = new Page<>();
        page.setCurrent(blogVO.getCurrentPage());
        page.setSize(blogVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE); // 删除的博客
        queryWrapper.eq(SQLConf.IS_AUDIT,StatusCode.DISABLED); // 博客审核中
        if (StringUtils.isNotEmpty(blogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogVO.getOrderByAscColumn()).toString());
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(blogVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.toUnderScoreCase(new StringBuffer(blogVO.getOrderByDescColumn()).toString());
            queryWrapper.orderByDesc(column);
        } else {
            // 是否启动排序字段
            if (blogVO.getUseSort() == 0) {
                // 未使用，默认按时间倒序
                queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
            } else {
                // 使用，默认按sort值大小倒序
                queryWrapper.orderByDesc(SQLConf.SORT);
            }
        }
        IPage<Blog> pageList = page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        if (list.size() == 0) {
            return pageList;
        }


        // step02: 查询博客对应的分类id、标签id、图片id
        final StringBuffer fileids = new StringBuffer();
        List<Integer> sortids = new ArrayList<>();
        List<Integer> tagids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getPhotoUrl())) {
                fileids.append(item.getPhotoUrl() + SysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotNull(item.getBlogSortId())) {
                sortids.add(item.getBlogSortId());
            }
            if (StringUtils.isNotNull(item.getTagId())) {
                List<Integer> tagUidsTemp = StringUtils.changeStringToInt(item.getTagId(), SysConf.FILE_SEGMENTATION);
                for (Integer itemTagUid : tagUidsTemp) {
                    tagids.add(itemTagUid);
                }
            }
        });
        // 2.1查询图片
        String pictureList = null;
        if (fileids != null) {
//            pictureList = this.pictureFeignClient.getPicture(fileids.toString(), SysConf.FILE_SEGMENTATION);
        }
//        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        // 2.2查询分类
        Collection<BlogSort> sortList = new ArrayList<>();
        // 2.3查询标签
        Collection<Tag> tagList = new ArrayList<>();
        if (sortids.size() > 0) {
            sortList = blogSortMapper.selectBatchIds(sortids);
        }
        if (tagids.size() > 0) {
            tagList = tagMapper.selectBatchIds(tagids);
        }


        // 2.4 预备操作
        Map<Integer, BlogSort> sortMap = new HashMap<>();
        Map<Integer, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();


        sortList.forEach(item -> {
            sortMap.put(item.getId(), item);
        });
        tagList.forEach(item -> {
            tagMap.put(item.getId(), item);
        });
//        picList.forEach(item -> {
//            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
//        });


        // step3: 设置详细信息
        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNotNull(item.getBlogSortId())) {
                item.setBlogSort(sortMap.get(item.getBlogSortId()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagId())) {
                List<Integer> tagUidsTemp = StringUtils.changeStringToInt(item.getTagId(), SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
//            if (StringUtils.isNotEmpty(item.getFileId())) {
//                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileId(), SysConf.FILE_SEGMENTATION);
//                List<String> pictureListTemp = new ArrayList<>();
//
////                pictureUidsTemp.forEach(picture -> {
////                    pictureListTemp.add(pictureMap.get(picture));
////                });
//                pictureListTemp.add(item.getFileId());
//                item.setPhotoList(pictureListTemp);
//            }
        }

        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogListByUser(HttpServletRequest request, Long currentPage, Long pageSize, Long userId) {

//        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
//        if (StringUtils.isEmpty(blogNewCount)) {
//            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
//        }
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        User user = userService.getUserById(userId);
        queryWrapper.eq(BaseSQLConf.USERID,user.getUserId());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));

        IPage<Blog> pageList = page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();

        if (list.size() <= 0) {
            return pageList;
        }
        page.setSize(list.size());

        list = setBlog(list);
        pageList.setRecords(list);

        //将从最新博客缓存到redis中

        return pageList;
    }

    /**
     * 博客审核
     * @param blogVO
     * @return
     */
    @Override
    public R editAuditBlog(BlogVO blogVO) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId,blogVO.getId());
        Blog blog = dao.selectOne(queryWrapper);
        blog.setReason(blogVO.getReason());
        blog.setIsAudit(blogVO.getIsAudit());
        blog.setIsPublish(blogVO.getIsPublish());
        int update = dao.updateById(blog);
        updateEsAndRedis(blog,1);
        if(update != 0) {
            return R.success(update);
        }
        return R.error();
    }

    @Override
    public R updateBlog(BlogVO blogVO , Long id) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId,blogVO.getId());
        Blog blog = dao.selectOne(queryWrapper);
        redisUtil.lRemove(RedisConf.NEW_BLOG, 0, JSONObject.toJSONString(blog));
        blog.setTitle(blogVO.getTitle());
        blog.setContent(blog.getContent());
        if(blogVO.getFileId() != null) {
            blog.setPhotoUrl(blogVO.getFileId());
        }
        blog.setSummary(blogVO.getSummary());
        blog.setBlogSortId(blogVO.getBlogSortId());
        blog.setTagId("");
        int i = dao.updateById(blog);
        if(i > 0 ) {
            updateEsAndRedis(blog,2);
        }
        return R.success("更新成功",blog.getId());
    }

    @Override
    public R deleteById(Integer blogId) {
        if(StringUtils.isNull(blogId)) {
            return R.error("删除失败");
        }
        dao.deleteById(blogId);
        updateEsAndRedis(new Blog(),blogId);
        return R.success("删除成功");
    }


    /**
     * 保存成功后，需要发送消息到solr 和 redis
     *
     * @param blog
     */
    private void updateEsAndRedis(Blog blog ,Integer type) {

        if(type == 1) {
            // 增加
            //         保存操作，并且文章已设置发布
            HashMap<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.ADD);
            map.put(SysConf.BLOG_ID, blog.getId());
            map.put(SysConf.LEVEL, blog.getLevel());
            map.put(SysConf.CREATE_TIME, blog.getCreateTime());

            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.DD_BLOG, map);
        } else if (type == 2) {
            // 更新
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.UPDATE);
            map.put(SysConf.BLOG_ID, blog.getId());
            map.put(SysConf.LEVEL, blog.getLevel());
            map.put(SysConf.CREATE_TIME, blog.getCreateTime());
            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.DD_BLOG, map);
        } else if(type == 3) {
            // 删除
            //这是需要做的是，是删除redis中的该条博客数据
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.DELETE);
            map.put(SysConf.BLOG_ID, blog.getId());
            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.DD_BLOG, map);
        }

    }



    /**
     * 根据博客设置标签
     * @param blog 博客
     * @return 博客
     */
    public Blog setTagByBlog(Blog blog) {
        String tagUid = blog.getTagId();
        if (!StringUtils.isEmpty(tagUid)) {
            String[] uids = tagUid.split(SysConf.FILE_SEGMENTATION);
            List<Tag> tagList = new ArrayList<>();
            for (String uid : uids) {
                Tag tag = tagMapper.selectById(uid);
                if (tag != null && tag.getStatus() != StatusCode.DISABLED) {
                    tagList.add(tag);
                }
            }
            blog.setTagList(tagList);
        }
        return blog;
    }

    public Blog setSortByBlog(Blog blog) {

        if (blog != null && !StringUtils.isNull(blog.getBlogSortId())) {
            BlogSort blogSort = blogSortMapper.selectById(blog.getBlogSortId());
            blog.setBlogSort(blogSort);
        }
        return blog;
    }
}


package com.yan.bbs.service.Impl;


import com.yan.bbs.entity.UserLike;
import com.yan.bbs.mapper.BlogMapper;
import com.yan.bbs.mapper.UserLikeMapper;
import com.yan.bbs.service.BlogLikeService;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yan.dd_common.constant.Constants;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/28 15:36
 */
@Service
@Slf4j
public class BlogLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements BlogLikeService {


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private DataSourceTransactionManager transactionManager;


    @Autowired
    private UserLikeMapper dao;


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public R likeBlog(Integer blogId, Long userId) {
        log.info("点赞数据存入redis开始，blogId:{}，userId:{}", blogId, userId);
        // 检验是否点赞
        HashSet<Long> userIdSet = null;
        try {
            userIdSet = likeArticleLogicValidate(blogId,userId);
        } catch (Exception e) {
            return R.error("已点赞过，不可重复点赞");
        }
        // 如果没有点赞
        if(userIdSet == null) {
            userIdSet = new HashSet<>();
        }
        userIdSet.add(userId);
        redisUtil.setCacheMap(Constants.USER_LIKE_BLOG_KEY,String.valueOf(blogId),userIdSet);
        return R.success("点赞成功👍");
    }

    @Override
    public R unLikeBlog(Integer blogId,Long userId) {
        log.info("取消点赞数据存入redis开始，blogId:{}，userId:{}", blogId, userId);
        try {
            unlikeArticleLogicValidate(blogId,userId);
            return R.success("取消点赞成功");
        } catch (Exception e) {
            return R.error("还没点赞，请点赞");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transLikedFromRedis2Db() {
        // key是博客ID,value是用户ID集合
        try {
            Map<String, Object> blogIdMap = redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY);
            blogIdMap.forEach((key, value) -> {
                ArrayList<Integer> list = (ArrayList<Integer>) value;
                List<UserLike> userLikeList = new ArrayList<>();
                if(list.size() > 0) {
                    list.forEach(item -> {
                        UserLike userLike = new UserLike();
                        userLike.setBlogId(StringUtils.stringToInteger(key));
                        userLike.setUserId(Long.valueOf(item));
                        userLike.setCreateTime(new Date());
                        userLike.setUpdateTime(new Date());
                        userLikeList.add(userLike);
                    });
                    Blog blog = blogMapper.selectById(Integer.valueOf(key));
                    blog.setLikeCount(blog.getLikeCount()+1);
                    blogMapper.updateById(blog);
                    // 由binlog日志去通知MQ更新ES和Redis
                    saveBatch(userLikeList);
                    // 放入MQ,通知用户
                    HashMap<String, String> map = new HashMap<>();
                    map.put("userId", JSON.toJSONString(list));
                    map.put("topic","0");// 点赞
                    map.put("entityId",key); // 博客ID
                    map.put("entityUserId",JSON.toJSONString(blog.getUserId()));
                    // 通知RabbitMQ
                    rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.DD_EMAIL, map);
                    redisUtil.delCacheMapValue(Constants.USER_LIKE_BLOG_KEY,String.valueOf(key));
                }
            });
        } catch (Exception e) {
        }

    }

    @Override
    public List<UserLike> getUserLikeBlog(Long userId) {
        LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLike::getUserId,userId);
        queryWrapper.eq(UserLike::getStatus,1);
        return dao.selectList(queryWrapper);
    }

    /**
     * 点赞文章逻辑校验
     *
     * @throws
     */
    private HashSet<Long> likeArticleLogicValidate(Integer blogId, Long likedUserId) {
        HashSet<Long> userIdSet = new HashSet<>();
        try {
            userIdSet = JSON.parseObject(redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY, String.valueOf(blogId)).toString(),HashSet.class);
        } catch (Exception e) {
            // 如果没有找到
        }
        if (userIdSet == null){
            // 如果没有找到，查询数据库
            LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserLike::getUserId,likedUserId);
            queryWrapper.eq(UserLike::getStatus, 1);
            queryWrapper.eq(UserLike::getBlogId,blogId);
            UserLike userLike = dao.selectOne(queryWrapper);
            if(StringUtils.isNull(userLike)) {
                return userIdSet;
            } else {
                throw new ServiceException("已点赞过，不可重复点赞");
            }
        } else {
            if (userIdSet.contains(Integer.valueOf(Math.toIntExact(likedUserId)))) {
                throw new ServiceException("已点赞过，不可重复点赞");
            } else {
                return userIdSet;
            }
        }
    }

    /**
     * 取消点赞文章逻辑校验
     *
     * @return
     */
    private boolean unlikeArticleLogicValidate(Integer blogId, Long likedUserId) {
        // 先判断Redis是否存在，如果存在直接删除（此时还没有保存到数据库）
        HashSet<Integer> userIdSet = new HashSet<>();
        try {
            userIdSet = JSONObject.parseObject(redisUtil.getCacheMap(Constants.USER_LIKE_BLOG_KEY, String.valueOf(blogId)).toString(),HashSet.class);
        } catch (Exception e) {
        }

        // 如果Redis不存在的情况，已经是是10分钟后，可以直接删数据
        if(userIdSet == null) {
            LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserLike::getUserId,likedUserId);
            queryWrapper.eq(UserLike::getBlogId,blogId);
            queryWrapper.eq(UserLike::getStatus, 1);
            int delete = dao.delete(queryWrapper);
            if(delete == 0) {
                throw new ServiceException("还没点赞，请点赞");
            } else {
                Blog blog = blogMapper.selectById(blogId);
                blog.setLikeCount(blog.getLikeCount()-1);
                blogMapper.updateById(blog);
                return true;
            }
        } else {
            if(!userIdSet.contains(Integer.valueOf(Math.toIntExact(likedUserId)))) {

                // 如果要取消点赞

                // 1.删除用户点赞表数据
                LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserLike::getUserId,likedUserId);
                queryWrapper.eq(UserLike::getBlogId,blogId);
                queryWrapper.eq(UserLike::getStatus, 1);
                int delete = dao.delete(queryWrapper);
                if(delete == 0) {
                    throw new ServiceException("还没点赞，请点赞");
                } else {
                    // 2.删除博客表中的点赞数量
                    Blog blog = blogMapper.selectById(blogId);
                    blog.setLikeCount(blog.getLikeCount()-1);
                    blogMapper.updateById(blog);

                    // 3.删除log通知表数据
                    return true;
                }
            } else  {
                // 存在Redis中删除
                userIdSet.remove(Integer.valueOf(Math.toIntExact(likedUserId)));
                redisUtil.setCacheMap(Constants.USER_LIKE_BLOG_KEY,String.valueOf(blogId),userIdSet);
                return true;
            }
        }
    }
}

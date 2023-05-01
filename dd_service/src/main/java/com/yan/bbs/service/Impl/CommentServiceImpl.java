package com.yan.bbs.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.bbs.entity.Comment;
import com.yan.dd_common.entity.User;
import com.yan.bbs.mapper.CommentMapper;
import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.CommentService;
import com.yan.bbs.service.UserService;
import com.yan.dd_common.constant.BaseSQLConf;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.enums.ECommentSource;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.exception.DeleteException;
import com.yan.dd_common.model.Vo.CommentVO;
import com.yan.dd_common.utils.ResultUtil;
import com.yan.dd_common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yanshuang
 * @date 2023/4/28 21:47
 */
@Service
public class CommentServiceImpl extends SuperServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
//    @Resource
//    private PictureFeignClient pictureFeignClient;

    @Override
    public Integer getCommentCount(int status) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<Comment> getPageList(CommentVO commentVO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getKeyword()) && !StringUtils.isEmpty(commentVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.CONTENT, commentVO.getKeyword().trim());
        }

        if (commentVO.getType() != null) {
            queryWrapper.eq(SQLConf.TYPE, commentVO.getType());
        }

        if (StringUtils.isNotEmpty(commentVO.getSource()) && !SysConf.ALL.equals(commentVO.getSource())) {
            queryWrapper.eq(SQLConf.SOURCE, commentVO.getSource());
        }

        if (StringUtils.isNotEmpty(commentVO.getUserName())) {
            String userName = commentVO.getUserName();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like(SQLConf.NICK_NAME, userName);
            userQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            List<User> list = userService.list(userQueryWrapper);
            if (list.size() > 0) {
                List<String> userUid = new ArrayList<>();
                list.forEach(item -> {
//                    userUid.add(item.getUid());
                });
                queryWrapper.in(SQLConf.USER_UID, userUid);
            } else {
                // 当没有查询到用户时，默认UID
                queryWrapper.in(SQLConf.USER_UID, SysConf.DEFAULT_UID);
            }
        }

        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> commentList = pageList.getRecords();
        Set<Integer> userUidSet = new HashSet<>();
        Set<Integer> blogUidSet = new HashSet<>();
        commentList.forEach(item -> {
            if (StringUtils.isNotNull(item.getUserId())) {
                userUidSet.add(item.getUserId());
            }
            if (StringUtils.isNotNull(item.getToUserId())) {
                userUidSet.add(item.getToUserId());
            }
            if (StringUtils.isNotNull(item.getBlogId())) {
                blogUidSet.add(item.getBlogId());
            }
        });

        // 获取博客
        Collection<Blog> blogList = new ArrayList<>();
        if (blogUidSet.size() > 0) {
            blogList = blogService.listByIds(blogUidSet);
        }
        Map<Integer, Blog> blogMap = new HashMap<>();
        blogList.forEach(item -> {
            // 评论管理并不需要查看博客内容，因此将其排除
            item.setContent("");
            blogMap.put(item.getId(), item);
        });

        // 获取头像
        Collection<User> userCollection = new ArrayList<>();
        if (userUidSet.size() > 0) {
            userCollection = userService.listByIds(userUidSet);
        }

        final StringBuffer fileUids = new StringBuffer();
        userCollection.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        if (fileUids != null) {
            pictureList = "";
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });
        Map<String, User> userMap = new HashMap<>();
        userCollection.forEach(item -> {
            // 判断头像是否为空
            if (pictureMap.get(item.getAvatar()) != null) {
//                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
//            userMap.put(item.getUid(), item);
        });

//        commentList.forEach(item -> {
//            ECommentSource commentSource = ECommentSource.valueOf(item.getSource());
//            item.setSourceName(commentSource.getName());
//            if (StringUtils.isNotEmpty(item.getUserUid())) {
//                item.setUser(userMap.get(item.getUserUid()));
//            }
//            if (StringUtils.isNotEmpty(item.getToUserUid())) {
//                item.setToUser(userMap.get(item.getToUserUid()));
//            }
//            if (StringUtils.isNotEmpty(item.getBlogUid())) {
//                item.setBlog(blogMap.get(item.getBlogUid()));
//            }
//        });

        for (Comment item : commentList) {

            try {
                ECommentSource commentSource = ECommentSource.valueOf(item.getSource());
                item.setSourceName(commentSource.getName());
            } catch (Exception e) {
                log.error("ECommentSource 转换异常");
            }

            if (StringUtils.isNotNull(item.getUserId())) {
                item.setUser(userMap.get(item.getUserId()));
            }
            if (StringUtils.isNotNull(item.getToUserId())) {
                item.setToUser(userMap.get(item.getToUserId()));
            }
            if (StringUtils.isNotNull(item.getBlogId())) {
                item.setBlog(blogMap.get(item.getBlogId()));
            }
        }

        pageList.setRecords(commentList);
        return pageList;
    }

    @Override
    public String addComment(CommentVO commentVO) {
        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogId(commentVO.getBlogId());
        comment.setContent(commentVO.getContent());
        comment.setUserId(commentVO.getUserId());
        comment.setToId(commentVO.getToId());
        comment.setToUserId(commentVO.getToUserId());
        comment.setStatus(StatusCode.ENABLE);
        comment.setUpdateTime(new Date());
        comment.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editComment(CommentVO commentVO) {
        Comment comment = commentService.getById(commentVO.getUid());
        comment.setSource(commentVO.getSource());
        comment.setBlogId(commentVO.getBlogId());
        comment.setContent(commentVO.getContent());
        comment.setUserId(commentVO.getUserId());
        comment.setToId(commentVO.getToId());
        comment.setToUserId(commentVO.getToUserId());
        comment.setStatus(StatusCode.ENABLE);
        comment.setUpdateTime(new Date());
        comment.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteComment(CommentVO commentVO) {
        Comment comment = commentService.getById(commentVO.getUid());
        comment.setStatus(StatusCode.DISABLED);
        comment.setUpdateTime(new Date());
        comment.updateById();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String deleteBatchComment(List<CommentVO> commentVOList) {
        if (commentVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        commentVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<Comment> commentList = commentService.listByIds(uids);

        commentList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(StatusCode.DISABLED);
        });
        commentService.updateBatchById(commentList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String batchDeleteCommentByBlogUid(List<String> blogUidList) {
        if(blogUidList.size() <= 0) {
            throw new DeleteException();
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConf.BLOG_UID, blogUidList);
        List<Comment> commentList = commentService.list(queryWrapper);
        if(commentList.size() > 0) {
            commentList.forEach(item -> {
                item.setStatus(StatusCode.DISABLED);
            });
            commentService.updateBatchById(commentList);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

}

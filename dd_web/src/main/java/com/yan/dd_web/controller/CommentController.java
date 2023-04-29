package com.yan.dd_web.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yan.bbs.entity.Comment;
import com.yan.dd_common.entity.User;
import com.yan.bbs.service.BlogService;
import com.yan.bbs.service.CommentService;
import com.yan.bbs.service.UserService;
import com.yan.dd_common.base.BaseController;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.entity.SuperBaseEntity;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.StatusCode;
import com.yan.dd_common.model.Vo.CommentVO;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.StringUtils;
import com.yan.dd_common.utils.ThrowableUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author yanshuang
 * @date 2022/5/26 6:38 下午
 */
@RestController
@RequestMapping("/comment")
@Api(value = "评论相关接口", tags = {"评论相关接口"})
public class CommentController extends BaseController {


    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;


    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BlogService blogService;

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @return
     */
    @ApiOperation(value = "获取评论列表", notes = "获取评论列表")
    @PostMapping("/getList")
    public R getList(@RequestBody CommentVO commentVO) {
        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(commentVO.getBlogId())) {
            queryWrapper.eq(Comment::getBlogId, commentVO.getBlogId());
        }
        queryWrapper.eq(Comment::getStatus, StatusCode.ENABLE);
        // 加载一级评论
        queryWrapper.isNull(Comment::getToId);
        queryWrapper.orderByDesc(SuperBaseEntity::getCreateTime);
        queryWrapper.eq(Comment::getType,0);

        // step01: 查询出所有的一级评论，进行分页显示
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<Integer> firstUidList = new ArrayList<>();
        list.forEach(item -> {
            firstUidList.add(item.getId());
        });


        // step02
        if (firstUidList.size() > 0) {
            // 查询一级评论下的子评论
            QueryWrapper<Comment> notFirstQueryWrapper = new QueryWrapper<>();
            notFirstQueryWrapper.in(SQLConf.FIRST_COMMENT_UID, firstUidList);
            notFirstQueryWrapper.eq(SQLConf.STATUS, StatusCode.ENABLE);
            List<Comment> notFirstList = commentService.list(notFirstQueryWrapper);
            // 将子评论加入总的评论中
            if (notFirstList.size() > 0) {
                list.addAll(notFirstList);
            }
        }

        // step03: 查询用户信息
        List<Integer> userUidList = new ArrayList<>();
        list.forEach(item -> {
            int userUid = item.getUserId();
            int toUserUid = item.getUserId();
            if (StringUtils.isNotNull(userUid)) {
                userUidList.add(item.getUserId());
            }
            if (StringUtils.isNotNull(toUserUid)) {
                userUidList.add(item.getUserId());
            }
        });
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUserId(item.getUserId());
            user.setNickName(item.getNickName());
            user.setUserTag(item.getUserTag());
            filterUserList.add(user);
        });

        // 获取用户头像

        Map<Long, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            userMap.put(item.getUserId(), item);
        });

        list.forEach(item -> {
            if (StringUtils.isNotNull(item.getUserId())) {
                item.setUser(userMap.get(Long.valueOf(item.getUserId())));
            }
            if (StringUtils.isNotNull(item.getToUserId())) {
                item.setToUser(userMap.get(Long.valueOf(item.getToUserId())));
            }
        });

        // 设置一级评论下的子评论
        Map<Integer, List<Comment>> toCommentListMap = new HashMap<>();
        for (int a = 0; a < list.size(); a++) {
            List<Comment> tempList = new ArrayList<>();
            for (int b = 0; b < list.size(); b++) {
                if (list.get(a).getId().equals(list.get(b).getToId())) {
                    tempList.add(list.get(b));
                }
            }
            toCommentListMap.put(list.get(a).getId(), tempList);
        }
        // 一级评论
        List<Comment> firstComment = new ArrayList<>();
        list.forEach(item -> {
            if (item.getToId() == null) {
                firstComment.add(item);
            }
        });
        pageList.setRecords(getCommentReplys(firstComment, toCommentListMap));
        return R.success(pageList);
    }

    /**
     * 获取评论所有回复
     *
     * @param list
     * @param toCommentListMap
     * @return
     */
    private List<Comment> getCommentReplys(List<Comment> list, Map<Integer, List<Comment>> toCommentListMap) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        } else {
            list.forEach(item -> {
                int commentUid = item.getId();
                List<Comment> replyCommentList = toCommentListMap.get(commentUid);
                List<Comment> replyComments = getCommentReplys(replyCommentList, toCommentListMap);
                item.setReplyList(replyComments);
            });
            return list;
        }
    }



    @ApiOperation(value = "增加评论", notes = "增加评论")
    @PostMapping("/add")
    public R add(@RequestBody CommentVO commentVO, BindingResult result) {
        // 判断该博客是否开启评论功能
        Blog blog = blogService.getById(commentVO.getBlogId());
        // 判断是否开启全局评论功能
        if (SysConf.CAN_NOT_COMMENT.equals(blog.getOpenComment())) {
            return R.error(SysConf.ERROR, MessageConf.BLOG_NO_OPEN_COMMENTS);
        }
//        Admin admin = getLoginUser();
        // TODO 获取当前用户
        User user = new User();
        // 判断字数是否超过限制
        if (commentVO.getContent().length() > SysConf.ONE_ZERO_TWO_FOUR) {
            return R.success(SysConf.ERROR, MessageConf.COMMENT_CAN_NOT_MORE_THAN_1024);
        }
        // 判断该用户是否被禁言
        if (user.getCommentStatus() == SysConf.ZERO+"") {
            return R.success(SysConf.ERROR, MessageConf.YOU_DONT_HAVE_PERMISSION_TO_SPEAK);
        }
        // 判断是否垃圾评论

        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogId(commentVO.getBlogId());
        comment.setContent(commentVO.getContent());
        comment.setToUserId(commentVO.getToUserId());

        // 当该评论不是一级评论时，需要设置一级评论UID字段
        if (StringUtils.isNotNull(commentVO.getToId())) {
            Comment toComment = commentService.getById(commentVO.getToId());
            // 表示 toComment是非一级评论
            if (toComment != null && StringUtils.isNotNull(toComment.getFirstCommentId())) {
                comment.setFirstCommentId(toComment.getFirstCommentId());
            } else {
                // 表示父评论是一级评论，直接获取UID
                comment.setFirstCommentId(toComment.getId());
            }
        } else {
            // 判断是否开启邮件通知
        }

        comment.setUserId(commentVO.getUserId());
        comment.setToId(commentVO.getToId());
        comment.setStatus(StatusCode.ENABLE);
        comment.insert();

        //获取图片

        comment.setUser(user);

        // 如果是回复某人的评论，那么需要向该用户Redis收件箱中中写入一条记录
        // MQ通知
        HashMap<String, String> map = new HashMap<>();
        ArrayList<Integer> userIdList = new ArrayList<>();
        userIdList.add(commentVO.getUserId()); // 评论人
        map.put("userId", JSON.toJSONString(userIdList));
        map.put("topic","1");// 评论
        map.put("entityId",String.valueOf(commentVO.getBlogId())); // 博客ID
        map.put("content",commentVO.getContent()); // 评论内容
        map.put("entityUserId",JSON.toJSONString(blog.getUserId()));
        // 通知RabbitMQ
        rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.DD_EMAIL, map);
        return R.success(SysConf.SUCCESS, comment);
    }

    /**
     * 通过UID删除评论
     *
     * @param request
     * @param commentVO
     * @param result
     * @return
     */
    @ApiOperation(value = "删除评论", notes = "删除评论")
    @PostMapping("/delete")
    public R deleteBatch(HttpServletRequest request, @RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        Comment comment = commentService.getById(commentVO.getUid());
        // 判断该评论是否能够删除
        if (!comment.getUserId().equals(commentVO.getUserId())) {
            return R.success(SysConf.ERROR, MessageConf.DATA_NO_PRIVILEGE);
        }
        comment.setStatus(EStatus.DISABLED+"");
        comment.updateById();

        // 获取该评论下的子评论进行删除
        // 传入需要被删除的评论 【因为这里是一条，我们需要用List包装一下，以后可以用于多评论的子评论删除】
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);

        // 判断删除的是一级评论还是子评论
        Integer firstCommentUid;
        if (!StringUtils.isNull(comment.getFirstCommentId())) {
            // 删除的是子评论
            firstCommentUid = comment.getFirstCommentId();
        } else {
            // 删除的是一级评论
            firstCommentUid = comment.getId();
        }

        // 获取该评论一级评论下所有的子评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.FIRST_COMMENT_UID, firstCommentUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Comment> toCommentList = commentService.list(queryWrapper);
        List<Comment> resultList = new ArrayList<>();
        this.getToCommentList(comment, toCommentList, resultList);
        // 将所有的子评论也删除
        if (resultList.size() > 0) {
            resultList.forEach(item -> {
                item.setStatus(EStatus.DISABLED+"");
                item.setUpdateTime(new Date());
            });
            commentService.updateBatchById(resultList);
        }

        return R.success(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    /**
     * 获取某条评论下的所有子评论
     *
     * @return
     */
    private void getToCommentList(Comment comment, List<Comment> commentList, List<Comment> resultList) {
        if (comment == null) {
            return;
        }
        Integer commentUid = comment.getId();
        for (Comment item : commentList) {
            if (commentUid.equals(item.getToId())) {
                resultList.add(item);
                // 寻找子评论的子评论
                getToCommentList(item, commentList, resultList);
            }
        }
    }

}

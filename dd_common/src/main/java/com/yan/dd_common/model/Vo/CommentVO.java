package com.yan.dd_common.model.Vo;

import com.yan.dd_common.Vo.BaseVO;
import com.yan.dd_common.validator.annotion.NotBlank;
import com.yan.dd_common.validator.group.GetList;
import com.yan.dd_common.validator.group.Insert;
import lombok.Data;
import lombok.ToString;

/**
 * @author yanshuang
 * @date 2023/4/28 21:40
 */
@ToString
@Data
public class CommentVO extends BaseVO<CommentVO> {

    /**
     * 用户uid
     */
    private Integer userId;

    /**
     * 回复某条评论的uid
     */
    private Integer toId;

    /**
     * 回复某个人的uid
     */
    private Integer toUserId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 评论内容
     */
    @NotBlank(groups = {Insert.class})
    private String content;

    /**
     * 博客uid
     */
    private Integer blogId;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    @NotBlank(groups = {Insert.class, GetList.class})
    private String source;
}


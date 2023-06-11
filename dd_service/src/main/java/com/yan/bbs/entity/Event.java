package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;

/**
 * 系统通知表
 *
 * @author yanshuang
 * @date 2023/6/11 01:22
 */
@Data
@TableName("d_event")
public class Event {

    /**
     * 唯一oid【自动递增】
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 事件类型，1、点赞，2、评论、3、关注
     */
    private String topic;

    /**
     * 事件由谁触发
     */
    private Integer userId;

    /**
     * 事件触发者name
     */
    private String userName;

    /**
     * 事件触发这头像
     */
    private String avatar;

    /**
     * 实体 id，博客ID，评论ID
     */
    private Integer entityId;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 实体类型,0:博客，1评论
     */
    private Integer entityType;

    /**
     * 实体的作者(该通知发送给他）
     */
    private int entityUserId;

    /**
     * 状态 0：没有查看  1：查看过
     */
    private String status;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    /**
     * 请求参数
     * 存储未来可能需要用到的数据
     * */
    @TableField(exist = false)
    private Map<String, Object> data;

}

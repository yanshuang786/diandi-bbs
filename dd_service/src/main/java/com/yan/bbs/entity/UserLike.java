package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yanshuang
 * @date 2023/4/28 15:35
 */
@Data
@ToString
@TableName("d_like_blog")
public class UserLike {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long userId;

    private Integer blogId;

    /**
     * 1:是点赞，0是删除点赞
     */
    private Boolean status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}


package com.yan.dd_common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;

/**
 * @author yanshuang
 * @date 2023/4/27 15:40
 */
@Data
@SuppressWarnings("rawtypes")
public class SuperBaseEntity <T extends Model> extends Model {

    /**
     *
     */
    private static final long serialVersionUID = -4851055162892178225L;


    /**
     * 状态 0：失效  1：生效
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

    /** 请求参数 */
    @TableField(exist = false)
    private Map<String, Object> params;

    public SuperBaseEntity() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }
}

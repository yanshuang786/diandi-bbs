package com.yan.dd_common.annotation;

import java.lang.annotation.*;

/**
 * @author yanshuang
 * @date 2023/4/27 15:58
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 部门表的别名
     */
    public String deptAlias() default "";

    /**
     * 用户表的别名
     */
    public String userAlias() default "";
}


package com.lhstack.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 11:35
 * @Modify By
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {
    /**
     * 需要切换的数据源名称
     *
     * @return
     */
    String value();
}

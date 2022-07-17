package com.lhstack.dynamic.datasource;

import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 11:24
 * @Modify By
 */
public class DynamicRoutingDataSourceHolder {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    private static String PRIMARY = null;

    public static void ds(String ds) {
        THREAD_LOCAL.set(ds);
    }

    public static String ds() {
        return Optional.ofNullable(THREAD_LOCAL.get()).orElse(PRIMARY);
    }

    public static synchronized void setPrimary(String primary) {
        if (StringUtils.isEmpty(PRIMARY)) {
            PRIMARY = primary;
        }
    }

    public static String getPrimary() {
        return PRIMARY;
    }

    public static void reset() {
        THREAD_LOCAL.remove();
    }
}

package com.lhstack.dynamic.datasource.utils;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/19 15:38
 * @Modify by
 */
public class CacheUtils {

    public static String generateUniqueCacheKey(MethodInvocation methodInvocation) {
        return methodInvocation.getThis().getClass().getCanonicalName().concat(methodInvocation.getMethod().getName());
    }
}

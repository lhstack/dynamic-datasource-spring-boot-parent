package com.lhstack.dynamic.datasource.annotation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lhstack.dynamic.datasource.DynamicRoutingDataSourceHolder;
import com.lhstack.dynamic.datasource.utils.CacheUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 11:41
 * @Modify By
 */

public class DsMethodInterceptor implements MethodInterceptor {

    private final Map<String, String> cache = Caffeine.newBuilder()
            .<String, String>build()
            .asMap();

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            String dsName = cache.computeIfAbsent(CacheUtils.generateUniqueCacheKey(methodInvocation), method -> {
                DS ds = Optional.ofNullable(AnnotationUtils.findAnnotation(methodInvocation.getMethod(), DS.class))
                        .orElseGet(() -> AnnotationUtils.findAnnotation(methodInvocation.getThis().getClass(), DS.class));
                return ds.value();
            });
            DynamicRoutingDataSourceHolder.ds(dsName);
            return methodInvocation.proceed();
        } finally {
            DynamicRoutingDataSourceHolder.reset();
        }
    }

}

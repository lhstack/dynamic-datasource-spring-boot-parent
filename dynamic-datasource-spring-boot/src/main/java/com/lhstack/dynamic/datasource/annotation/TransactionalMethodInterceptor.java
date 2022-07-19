package com.lhstack.dynamic.datasource.annotation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lhstack.dynamic.datasource.DynamicRoutingTransactionalHolder;
import com.lhstack.dynamic.datasource.proxy.ConnectionProxyFactory;
import com.lhstack.dynamic.datasource.utils.CacheUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 14:39
 * @Modify By
 */
public class TransactionalMethodInterceptor implements MethodInterceptor {

    private final Map<String, Transactional> cache = Caffeine.newBuilder()
            .<String, Transactional>build()
            .asMap();

    /**
     * 记录栈深度
     */
    private static final ThreadLocal<String> FIRST_STACK_CACHE_KEY = new ThreadLocal<>();

    /**
     * 执行事务添加，排除以及最终提交和回滚
     *
     * @param invocation the method invocation joinpoint
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean flag = false;
        Object o;
        String firstStackCacheKey = FIRST_STACK_CACHE_KEY.get();
        if (Objects.isNull(firstStackCacheKey)) {
            firstStackCacheKey = CacheUtils.generateUniqueCacheKey(invocation);
            FIRST_STACK_CACHE_KEY.set(firstStackCacheKey);
        }
        try {
            Transactional transactional = cache.computeIfAbsent(CacheUtils.generateUniqueCacheKey(invocation), method -> Optional
                    .ofNullable(AnnotationUtils.findAnnotation(invocation.getMethod(), Transactional.class))
                    .orElseGet(() -> AnnotationUtils.findAnnotation(invocation.getThis().getClass(), Transactional.class)));
            DynamicRoutingTransactionalHolder.pushTransactional(transactional);
            o = invocation.proceed();
        } catch (Throwable e) {
            flag = true;
            ConnectionProxyFactory.rollback(e);
            DynamicRoutingTransactionalHolder.reset();
            FIRST_STACK_CACHE_KEY.remove();
            throw e;
        } finally {
            DynamicRoutingTransactionalHolder.popTransactional();
            if (!flag) {
                if (firstStackCacheKey.equals(CacheUtils.generateUniqueCacheKey(invocation))) {
                    ConnectionProxyFactory.commit();
                    DynamicRoutingTransactionalHolder.reset();
                    FIRST_STACK_CACHE_KEY.remove();
                }
            }
        }
        return o;
    }

}

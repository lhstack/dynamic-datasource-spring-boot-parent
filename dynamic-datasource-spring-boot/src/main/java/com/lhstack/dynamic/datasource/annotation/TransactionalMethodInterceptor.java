package com.lhstack.dynamic.datasource.annotation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lhstack.dynamic.datasource.DynamicRoutingTransactionalHolder;
import com.lhstack.dynamic.datasource.proxy.ConnectionProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
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

    private final Map<Method, Transactional> cache = Caffeine.newBuilder()
            .<Method, Transactional>build()
            .asMap();

    /**
     * 记录第一个入栈的方法
     */
    private static final ThreadLocal<Method> METHOD_LOCAL = new ThreadLocal<>();

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
        try {
            Transactional transactional = cache.computeIfAbsent(invocation.getMethod(), method -> Optional
                    .ofNullable(AnnotationUtils.findAnnotation(method, Transactional.class))
                    .orElseGet(() -> AnnotationUtils.findAnnotation(invocation.getThis().getClass(), Transactional.class)));
            DynamicRoutingTransactionalHolder.pushTransactional(transactional);
            Method method = METHOD_LOCAL.get();
            if (Objects.isNull(method)) {
                METHOD_LOCAL.set(invocation.getMethod());
            }
            o = invocation.proceed();
        } catch (Throwable e) {
            flag = true;
            ConnectionProxyFactory.rollback(e);
            DynamicRoutingTransactionalHolder.reset();
            throw e;
        } finally {
            if (!flag) {
                DynamicRoutingTransactionalHolder.popTransactional();
                if (invocation.getMethod().equals(METHOD_LOCAL.get())) {
                    ConnectionProxyFactory.commit();
                    DynamicRoutingTransactionalHolder.reset();
                    METHOD_LOCAL.remove();
                }
            }
        }
        return o;
    }

}

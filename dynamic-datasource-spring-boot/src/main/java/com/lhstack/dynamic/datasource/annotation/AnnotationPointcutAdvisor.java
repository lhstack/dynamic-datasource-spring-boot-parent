package com.lhstack.dynamic.datasource.annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 11:56
 * @Modify By
 */
public class AnnotationPointcutAdvisor extends AbstractGenericPointcutAdvisor {

    private final Class<? extends Annotation> annotation;
    private final Integer order;

    public AnnotationPointcutAdvisor(Class<? extends Annotation> annotation, Advice advice, Integer order) {
        this.annotation = annotation;
        this.setAdvice(advice);
        this.order = order;
    }

    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return ClassFilter.TRUE;
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return AnnotatedElementUtils.hasAnnotation(method, annotation) || AnnotatedElementUtils.hasAnnotation(targetClass, annotation);
                    }

                    @Override
                    public boolean isRuntime() {
                        return false;
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object... args) {
                        return matches(method, targetClass);
                    }
                };
            }
        };
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}

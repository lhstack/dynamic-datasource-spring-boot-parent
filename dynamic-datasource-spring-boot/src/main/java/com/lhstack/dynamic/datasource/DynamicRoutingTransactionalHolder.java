package com.lhstack.dynamic.datasource;

import com.lhstack.dynamic.datasource.annotation.Transactional;

import java.util.Objects;
import java.util.Stack;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 15:00
 * @Modify By
 */
public class DynamicRoutingTransactionalHolder {

    private static final ThreadLocal<Stack<Transactional>> THREAD_LOCAL = new ThreadLocal<>();

    public static void pushTransactional(Transactional transactional) {
        Stack<Transactional> stack = THREAD_LOCAL.get();
        if (Objects.isNull(stack)) {
            stack = new Stack<>();
            THREAD_LOCAL.set(stack);
        }
        stack.push(transactional);
    }

    public static Transactional peekTransactional() {
        Stack<Transactional> stack = THREAD_LOCAL.get();
        if (Objects.nonNull(stack)) {
            return stack.peek();
        }
        return null;
    }

    public static void popTransactional() {
        Stack<Transactional> stack = THREAD_LOCAL.get();
        if (Objects.nonNull(stack)) {
            stack.pop();
        }
    }

    public static void reset() {
        THREAD_LOCAL.remove();
    }


}

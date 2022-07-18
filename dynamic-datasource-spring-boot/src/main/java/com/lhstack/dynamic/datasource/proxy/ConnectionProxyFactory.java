package com.lhstack.dynamic.datasource.proxy;

import com.lhstack.dynamic.datasource.DynamicRoutingDataSourceHolder;
import com.lhstack.dynamic.datasource.DynamicRoutingTransactionalHolder;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 15:24
 * @Modify By
 */
public class ConnectionProxyFactory {


    private static final ThreadLocal<Map<String, Stack<ConnectionProxy>>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 回滚
     * @param e
     * @throws SQLException
     */
    public static void rollback(Throwable e) throws SQLException {
        Map<String, Stack<ConnectionProxy>> stackMap = THREAD_LOCAL.get();
        if (Objects.nonNull(stackMap)) {
            for (Stack<ConnectionProxy> value : stackMap.values()) {
                for (ConnectionProxy connectionProxy : value) {
                    connectionProxy.transactionRollback(e);
                }
            }
        }
        THREAD_LOCAL.remove();
    }

    /**
     * 提交事务
     * @throws SQLException
     */
    public static void commit() throws SQLException {
        Map<String, Stack<ConnectionProxy>> stackMap = THREAD_LOCAL.get();
        if (Objects.nonNull(stackMap)) {
            //获取栈里面的连接
            for (Stack<ConnectionProxy> value : stackMap.values()) {
                //便利连接，提交事务
                for (ConnectionProxy connectionProxy : value) {
                    connectionProxy.transactionCommit();
                }
            }
        }
        //清除缓存
        THREAD_LOCAL.remove();
    }


    /**
     * 新增或者根据事务传播机制重用连接
     * @param sqlExceptionSupplier
     * @return
     * @throws SQLException
     */
    public static Connection pushOrGetConnectionProxy(SqlExceptionSupplier<ConnectionProxy> sqlExceptionSupplier) throws SQLException {
        //获取数据源对应栈连接的map对象
        Map<String, Stack<ConnectionProxy>> stackMap = getOrInit();
        //获取当前事务
        Transactional transactional = DynamicRoutingTransactionalHolder.peekTransactional();
        //如果不存在事务，则用原始连接
        if (Objects.isNull(transactional)) {
            return sqlExceptionSupplier.get().getConnection();
        }
        //拿到当前线程对应的数据源
        String ds = DynamicRoutingDataSourceHolder.ds();
        //获取栈上面的所有连接
        Stack<ConnectionProxy> connectionProxies = stackMap.get(ds);
        if (Objects.isNull(connectionProxies)) {
            //如果是空的，则添加新的连接
            connectionProxies = new Stack<>();
            ConnectionProxy connectionProxy = startTransactional(transactional, sqlExceptionSupplier.get());
            connectionProxies.push(connectionProxy);
            stackMap.put(ds, connectionProxies);
            return connectionProxy;
        }
        //已存在事务的情况
        ConnectionProxy peek = connectionProxies.peek();
        //获取上一个连接的事务
        Transactional lastTransactional = peek.getTransactional();
        if (Objects.nonNull(lastTransactional)) {
            //判断是否满足事务传播机制
            switch (lastTransactional.propagation()) {
                case REQUIRED:
                case REQUIRES_NEW:
                case NESTED: {
                    switch (transactional.propagation()) {
                        case REQUIRED:
                        case SUPPORTS:
                        case MANDATORY: {
                            //判断是否是同一个事务隔离机制
                            if (transactional.isolation() == lastTransactional.isolation()) {
                                //判断事务和当前连接读取模式是否一致，或者当前连接为非只读模式，非只读模式也包含只读，要么当前事务都是只读模式，要么连接为非只读模式
                                if (!peek.isReadOnly() || transactional.readOnly() == peek.isReadOnly()) {
                                    return peek;
                                }
                            }
                        }
                        default:
                    }
                }
                default:
            }
        }
        //否则启动一个新的事务
        ConnectionProxy connectionProxy = startTransactional(transactional, sqlExceptionSupplier.get());
        connectionProxies.push(connectionProxy);
        stackMap.put(ds, connectionProxies);
        return connectionProxy;
    }

    private static ConnectionProxy startTransactional(Transactional transactional, ConnectionProxy connectionProxy) throws SQLException {
        //修改连接的处理方式
        connectionProxy.setReadOnly(transactional.readOnly());
        switch (transactional.propagation()) {
            case MANDATORY:
            case NEVER:
                return connectionProxy;
            default:
        }
        //关闭自动提交
        connectionProxy.setAutoCommit(false);
        //设置事务隔离级别
        if (transactional.isolation() != Isolation.DEFAULT) {
            connectionProxy.setTransactionIsolation(transactional.isolation().value());
        }
        //缓存当前事务对象到连接
        connectionProxy.setTransactional(transactional);
        return connectionProxy;
    }

    private static Map<String, Stack<ConnectionProxy>> getOrInit() {
        Map<String, Stack<ConnectionProxy>> stackMap = THREAD_LOCAL.get();
        if (Objects.isNull(stackMap)) {
            stackMap = new HashMap<>(1);
            THREAD_LOCAL.set(stackMap);
        }
        return stackMap;
    }

}

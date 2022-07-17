package com.lhstack.dynamic.datasource.proxy;

import com.lhstack.dynamic.datasource.DynamicRoutingDataSourceHolder;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 12:08
 * @Modify By
 */
public class DataSourceProxy implements DataSource {

    private final DataSource datasource;

    public DataSourceProxy(DataSource dataSource) {
        this.datasource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ConnectionProxyFactory.pushOrGetConnectionProxy(() -> new ConnectionProxy(DynamicRoutingDataSourceHolder.ds(), this.datasource.getConnection()));
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return ConnectionProxyFactory.pushOrGetConnectionProxy(() -> new ConnectionProxy(DynamicRoutingDataSourceHolder.ds(), this.datasource.getConnection(username, password)));
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return datasource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return datasource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return datasource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        datasource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        datasource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return datasource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return datasource.getParentLogger();
    }

    @Override
    public String toString() {
        return datasource.toString();
    }

    public void close() {
        Method method = ReflectionUtils.findMethod(this.datasource.getClass(), "close");
        if(Objects.nonNull(method)){
            ReflectionUtils.invokeMethod(method, this.datasource);
        }
    }
}

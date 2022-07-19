package com.lhstack.dynamic.datasource;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lhstack.dynamic.datasource.proxy.DataSourceProxy;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 8:30
 * @Modify By
 */
public class DynamicRoutingDataSource extends AbstractDataSource {

    private final String primary;
    private final Map<String, DataSourceProxy> targetDataSources;
    private DataSourceProxy defaultTargetDataSource;

    /**
     * 如果没有匹配到对应数据源，则返回默认的
     */
    private boolean noMatchReturnDefault = false;

    public DynamicRoutingDataSource(String primary, DataSource dataSource) {
        this(primary, dataSource, false);
    }

    public DynamicRoutingDataSource(String primary, DataSource dataSource, boolean enableWeakReference) {
        Map<String, DataSourceProxy> dataSources;
        if (enableWeakReference) {
            dataSources = Caffeine.newBuilder().weakKeys().weakValues().<String, DataSourceProxy>build().asMap();
        } else {
            dataSources = Caffeine.newBuilder().<String, DataSourceProxy>build().asMap();
        }
        DataSourceProxy dataSourceProxy = new DataSourceProxy(dataSource);
        dataSources.put(primary, dataSourceProxy);
        this.defaultTargetDataSource = dataSourceProxy;
        this.targetDataSources = dataSources;
        this.primary = primary;
    }

    public DynamicRoutingDataSource(String primary, Map<String, DataSource> dataSources) {
        this(primary, dataSources, false);
    }

    public DynamicRoutingDataSource(String primary, Map<String, DataSource> dataSources, boolean enableWeakReference) {
        DataSource dataSource = dataSources.get(primary);
        if (Objects.isNull(dataSource)) {
            throw new RuntimeException("dataSources do not contain primary dataSources");
        }
        Map<String, DataSourceProxy> dataSourceProxyMap = dataSources.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> new DataSourceProxy(item.getValue())));
        Map<String, DataSourceProxy> ds;
        if (enableWeakReference) {
            ds = Caffeine.newBuilder().weakKeys().weakValues().<String, DataSourceProxy>build().asMap();
        } else {
            ds = Caffeine.newBuilder().<String, DataSourceProxy>build().asMap();
        }
        ds.putAll(dataSourceProxyMap);
        this.defaultTargetDataSource = dataSourceProxyMap.get(primary);
        this.targetDataSources = ds;
        this.primary = primary;
    }

    public void setNoMatchReturnDefault(boolean noMatchReturnDefault) {
        this.noMatchReturnDefault = noMatchReturnDefault;
    }

    /**
     * 新增数据源
     *
     * @param ds
     * @param dataSource
     */
    public void add(String ds, DataSource dataSource) {
        this.add(ds, dataSource, false);
    }

    /**
     * 是否重写数据源
     *
     * @param ds
     * @param dataSource
     * @param overwrite
     */
    public void add(String ds, DataSource dataSource, boolean overwrite) {
        Assert.notNull(dataSource, "dataSource cannot be null");
        Assert.hasText(ds, "ds cannot be null");
        DataSourceProxy existDataSource = this.targetDataSources.get(ds);
        //如果获取到的数据源不为空，同时支持重写
        if (Objects.nonNull(existDataSource) && overwrite) {
            DataSourceProxy dataSourceProxy = new DataSourceProxy(dataSource);
            //如果是主数据源，则替换默认的
            if (this.primary.equals(ds)) {
                this.defaultTargetDataSource = dataSourceProxy;
                this.targetDataSources.put(ds, dataSourceProxy);
            } else {
                this.targetDataSources.put(ds, dataSourceProxy);
            }
            //关闭之前的数据源
            existDataSource.close();
        } else if (Objects.nonNull(existDataSource)) {
            //如果不支持重写，则抛出异常
            throw new RuntimeException("The current data source cannot be overwritten. To do so, set this parameter overwrite = true");
        }
        if (Objects.isNull(existDataSource)) {
            this.targetDataSources.put(ds, new DataSourceProxy(dataSource));
        }
    }

    /**
     * 删除数据源
     *
     * @param ds
     */
    public void remove(String ds) {
        if (ds.equals(primary)) {
            throw new RuntimeException("cannot be remove primary dataSource");
        }
        Optional.ofNullable(this.targetDataSources.remove(ds)).ifPresent(DataSourceProxy::close);
    }

    /**
     * 替换数据源
     *
     * @param dataSources
     */
    public void replace(Map<String, DataSource> dataSources) {
        dataSources.forEach((k, v) -> add(k, new DataSourceProxy(v), true));
    }

    public DataSource getDefaultTargetDataSource() {
        return defaultTargetDataSource;
    }

    public Map<String, DataSource> getTargetDataSources() {
        return Collections.unmodifiableMap(this.targetDataSources);
    }

    /**
     * 查找数据源所在的key
     *
     * @return
     */
    protected String determineCurrentLookupKey() {
        return DynamicRoutingDataSourceHolder.ds();
    }


    public void close() {
        Collection<DataSourceProxy> values = this.targetDataSources.values();
        values.forEach(DataSourceProxy::close);
    }

    public void init() {
        Collection<DataSourceProxy> values = this.targetDataSources.values();
        values.forEach(DataSourceProxy::init);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return determineTargetDataSource().isWrapperFor(iface);
    }

    /**
     * 获取当前数据源
     *
     * @return
     */
    public DataSource determineTargetDataSource() {
        String lookupKey = this.determineCurrentLookupKey();
        if (Objects.isNull(lookupKey)) {
            return this.defaultTargetDataSource;
        }
        DataSource dataSource = this.targetDataSources.get(lookupKey);
        if (dataSource == null) {
            if(noMatchReturnDefault){
                return this.defaultTargetDataSource;
            }
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

}

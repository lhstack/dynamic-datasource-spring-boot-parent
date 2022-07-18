package com.lhstack.dynamic.datasource;

import com.lhstack.dynamic.datasource.proxy.DataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 8:30
 * @Modify By
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    public DynamicRoutingDataSource(String primary, DataSource dataSource) {
        Map<Object, Object> dataSources = new HashMap<>(1);
        dataSources.put(primary, dataSource);
        this.setDefaultTargetDataSource(dataSource);
        this.setTargetDataSources(dataSources);
    }

    public DynamicRoutingDataSource(Object primary, Map<Object, Object> dataSources) {
        Object dataSource = dataSources.get(primary);
        if (Objects.isNull(dataSource)) {
            throw new RuntimeException("dataSources do not contain primary dataSources");
        }
        this.setDefaultTargetDataSource(dataSource);
        this.setTargetDataSources(dataSources);
    }


    @Override
    public Object determineCurrentLookupKey() {
        return DynamicRoutingDataSourceHolder.ds();
    }


    public void close() {
        Collection<DataSource> values = this.getResolvedDataSources().values();
        for (DataSource value : values) {
            if (value instanceof DataSourceProxy) {
                DataSourceProxy proxy = (DataSourceProxy) value;
                proxy.close();
            }
        }
    }
}

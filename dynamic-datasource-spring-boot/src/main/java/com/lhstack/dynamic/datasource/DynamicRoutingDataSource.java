package com.lhstack.dynamic.datasource;

import com.lhstack.dynamic.datasource.properties.DynamicRoutingDataSourceProperties;
import com.lhstack.dynamic.datasource.proxy.DataSourceProxy;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 8:30
 * @Modify By
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {


    public DynamicRoutingDataSource(DynamicRoutingDataSourceProperties properties, Environment environment) {
        Map<Object, Object> dataSources = new HashMap<>();
        Map<String, DynamicRoutingDataSourceProperties.DsProps> ds = properties.getDataSources();
        if (!ds.containsKey(properties.getPrimary())) {
            throw new RuntimeException("Set the primary data source");
        }
        ds.forEach((k, v) -> {
            if (StringUtils.hasText(v.getReference())) {
                dataSources.put(k, new DataSourceProxy(bindOrCreate(k, v.getReference(), v.getType(), environment)));
            } else {
                dataSources.put(k, new DataSourceProxy(bindOrCreate(k, "spring.dynamic.data-sources.".concat(k), v.getType(), environment)));
            }
        });
        DynamicRoutingDataSourceHolder.setPrimary(properties.getPrimary());
        this.setDefaultTargetDataSource(dataSources.get(properties.getPrimary()));
        this.setTargetDataSources(dataSources);
    }

    private DataSource bindOrCreate(String key, String group, Class<? extends DataSource> type, Environment environment) {
        Iterable<ConfigurationPropertySource> configurationPropertySources = ConfigurationPropertySources.get(environment);
        List<ConfigurationPropertySource> list = new ArrayList<>();
        ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
        aliases.addAliases(group.concat(".url"), group.concat(".jdbc-url"));
        aliases.addAliases(group.concat(".user"), group.concat(".username"));
        aliases.addAliases(group.concat(".pass"), group.concat(".password"));
        aliases.addAliases(group.concat(".driver-class"), group.concat(".driver-class-name"));
        String defaultGroup = "spring.dynamic.data-sources.".concat(key);
        if (!StringUtils.pathEquals(defaultGroup, group)) {
            aliases.addAliases(defaultGroup.concat(".url"), defaultGroup.concat(".jdbc-url"));
            aliases.addAliases(defaultGroup.concat(".user"), defaultGroup.concat(".username"));
            aliases.addAliases(defaultGroup.concat(".pass"), defaultGroup.concat(".password"));
            aliases.addAliases(defaultGroup.concat(".driver-class"), defaultGroup.concat(".driver-class-name"));
        }
        for (ConfigurationPropertySource configurationPropertySource : configurationPropertySources) {
            list.add(configurationPropertySource.withAliases(aliases));
        }
        Binder binder = new Binder(list, new PropertySourcesPlaceholdersResolver(environment));
        DataSource dataSource = binder.bindOrCreate(group, type);
        if (!StringUtils.pathEquals(defaultGroup, group)) {
            BindResult<DataSource> bindResult = binder.bind(defaultGroup, Bindable.ofInstance(dataSource));
            return bindResult.get();
        }
        return dataSource;
    }


    @Override
    public Object determineCurrentLookupKey() {
        return DynamicRoutingDataSourceHolder.ds();
    }

    @Override
    public DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
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

package com.lhstack.dynamic.datasource.utils;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/18 9:35
 * @Modify by
 */
public class DataSourceUtils {

    public static DataSource bindOrCreate(String key, String group, Class<? extends DataSource> type, Environment environment) {
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
}

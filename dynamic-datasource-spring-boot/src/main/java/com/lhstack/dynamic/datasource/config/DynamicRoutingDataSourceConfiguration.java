package com.lhstack.dynamic.datasource.config;

import com.lhstack.dynamic.datasource.DynamicRoutingDataSource;
import com.lhstack.dynamic.datasource.DynamicRoutingDataSourceHolder;
import com.lhstack.dynamic.datasource.annotation.*;
import com.lhstack.dynamic.datasource.properties.DynamicRoutingDataSourceProperties;
import com.lhstack.dynamic.datasource.proxy.DataSourceProxy;
import com.lhstack.dynamic.datasource.utils.DataSourceUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 8:38
 * @Modify By
 */
@EnableConfigurationProperties(DynamicRoutingDataSourceProperties.class)
@EnableAspectJAutoProxy
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DynamicRoutingDataSourceConfiguration {

    private final DynamicRoutingDataSourceProperties properties;


    public DynamicRoutingDataSourceConfiguration(DynamicRoutingDataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public AnnotationPointcutAdvisor dsAnnotationPointcutAdvisor() {
        return new AnnotationPointcutAdvisor(DS.class, new DsMethodInterceptor(), Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public AnnotationPointcutAdvisor transactionalAnnotationPointcutAdvisor() {
        return new AnnotationPointcutAdvisor(Transactional.class, new TransactionalMethodInterceptor(), Ordered.HIGHEST_PRECEDENCE + 1);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public DynamicRoutingDataSource dataSource(Environment environment) {
        Map<String, DynamicRoutingDataSourceProperties.DsProps> ds = properties.getDataSources();
        Map<Object, Object> dataSources = new HashMap<>(ds.size());
        if (!ds.containsKey(properties.getPrimary())) {
            throw new RuntimeException("Set the primary data source");
        }
        ds.forEach((k, v) -> {
            if (StringUtils.hasText(v.getReference())) {
                dataSources.put(k, new DataSourceProxy(DataSourceUtils.bindOrCreate(k, v.getReference(), v.getType(), environment)));
            } else {
                dataSources.put(k, new DataSourceProxy(DataSourceUtils.bindOrCreate(k, "spring.dynamic.data-sources.".concat(k), v.getType(), environment)));
            }
        });
        DynamicRoutingDataSourceHolder.setPrimary(properties.getPrimary());
        return new DynamicRoutingDataSource(properties.getPrimary(), dataSources);
    }


}

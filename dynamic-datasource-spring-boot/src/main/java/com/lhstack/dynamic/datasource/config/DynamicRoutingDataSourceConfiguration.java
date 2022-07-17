package com.lhstack.dynamic.datasource.config;

import com.lhstack.dynamic.datasource.DynamicRoutingDataSource;
import com.lhstack.dynamic.datasource.annotation.*;
import com.lhstack.dynamic.datasource.properties.DynamicRoutingDataSourceProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

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

    private final DynamicRoutingDataSourceProperties dynamicRoutingDataSourceProperties;


    public DynamicRoutingDataSourceConfiguration(DynamicRoutingDataSourceProperties dynamicRoutingDataSourceProperties) {
        this.dynamicRoutingDataSourceProperties = dynamicRoutingDataSourceProperties;
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
        return new DynamicRoutingDataSource(dynamicRoutingDataSourceProperties, environment);
    }

}

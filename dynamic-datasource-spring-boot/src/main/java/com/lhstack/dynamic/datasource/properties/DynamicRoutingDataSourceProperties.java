package com.lhstack.dynamic.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 8:33
 * @Modify By
 */
@ConfigurationProperties(prefix = "spring.dynamic")
public class DynamicRoutingDataSourceProperties {

    public static class DsProps {
        /**
         * 数据源类型
         */
        private Class<? extends DataSource> type;

        private String username;

        private String password;

        private String driverClassName;

        private String url;

        /**
         * 引用共用配置，如spring.ds.hikari,spring.ds.druid
         */
        private String reference;

        public String getUsername() {
            return username;
        }

        public DsProps setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public DsProps setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public DsProps setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public DsProps setUrl(String url) {
            this.url = url;
            return this;
        }

        public Class<? extends DataSource> getType() {
            return type;
        }

        public DsProps setType(Class<? extends DataSource> type) {
            this.type = type;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public DsProps setReference(String reference) {
            this.reference = reference;
            return this;
        }
    }

    /**
     * 默认数据源,对应dataSources的key
     */
    private String primary;

    /**
     * 数据源配置，key为数据源名称，value为数据源类型,对应key下面添加对应类型的数据源配置参数
     */
    private Map<String, DsProps> dataSources;

    public String getPrimary() {
        return primary;
    }

    public DynamicRoutingDataSourceProperties setPrimary(String primary) {
        this.primary = primary;
        return this;
    }

    public Map<String, DsProps> getDataSources() {
        return dataSources;
    }

    public DynamicRoutingDataSourceProperties setDataSources(Map<String, DsProps> dataSources) {
        this.dataSources = dataSources;
        return this;
    }
}

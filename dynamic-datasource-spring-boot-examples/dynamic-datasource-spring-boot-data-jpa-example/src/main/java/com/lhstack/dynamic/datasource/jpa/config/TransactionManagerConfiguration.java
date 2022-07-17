package com.lhstack.dynamic.datasource.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 11:33
 * @Modify By
 */
@Configuration
public class TransactionManagerConfiguration {

    @Bean("nonTransactionManager")
    public TransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setNestedTransactionAllowed(false);
        return jpaTransactionManager;
    }
}

package com.lhstack.dynamic.datasource.jpa;

import com.lhstack.dynamic.datasource.jpa.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:20
 * @Modify By
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lhstack.dynamic.datasource.jpa.repository",transactionManagerRef = "nonTransactionManager")
public class DynamicDataSourceApplication implements ApplicationRunner {

    @Autowired
    private TestService testService;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDataSourceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            testService.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            testService.rollbackExpectArithmeticException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

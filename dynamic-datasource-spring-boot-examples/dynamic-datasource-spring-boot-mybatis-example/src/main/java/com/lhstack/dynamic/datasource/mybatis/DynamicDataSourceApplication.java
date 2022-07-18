package com.lhstack.dynamic.datasource.mybatis;

import com.lhstack.dynamic.datasource.annotation.DS;
import com.lhstack.dynamic.datasource.mybatis.service.TestService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:20
 * @Modify By
 */
@SpringBootApplication
@MapperScan(basePackages = "com.lhstack.dynamic.datasource.mybatis.mapper")
public class DynamicDataSourceApplication implements ApplicationRunner, CommandLineRunner {

    @Autowired
    private TestService testService;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDataSourceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        try{
//            testService.rollback();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try{
//            testService.rollbackExpectArithmeticException();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    @DS("slave")
    public void run(String... args) throws Exception {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        connection.setReadOnly(true);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sss");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(resultSet);
        connection.commit();
        connection.setReadOnly(false);
        PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO sss(`value`) VALUES('测试数据')");
        System.out.println(preparedStatement1.executeUpdate());
        connection.commit();
        System.out.println(connection);
    }
}

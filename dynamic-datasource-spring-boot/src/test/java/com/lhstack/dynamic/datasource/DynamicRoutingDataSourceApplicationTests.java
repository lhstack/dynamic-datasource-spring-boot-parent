package com.lhstack.dynamic.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/18 15:29
 * @Modify by
 */
public class DynamicRoutingDataSourceApplicationTests {

    @Test
    public void testOneDataSource() throws Exception {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManagerDataSource.setUsername("root");
        driverManagerDataSource.setPassword("123456");
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource("master", driverManagerDataSource, true);
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM aaa");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String value = resultSet.getString("value");
            System.out.printf("id=%s,value=%s\r\n", id, value);
        }
    }

    @Test
    public void testTwoDataSource() throws Exception {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/test1?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManagerDataSource.setUsername("root");
        driverManagerDataSource.setPassword("123456");
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource("master", driverManagerDataSource, true);
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sss");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String value = resultSet.getString("value");
            System.out.printf("id=%s,value=%s\r\n", id, value);
        }
    }

    @Test
    public void testMixinDataSource() throws Exception {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("123456");
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource("master", hikariDataSource, true);
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM aaa");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String value = resultSet.getString("value");
            System.out.printf("id=%s,value=%s\r\n", id, value);
        }
        connection.close();
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test1?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("123456");
        dataSource.add("slave", hikariDataSource);
        DynamicRoutingDataSourceHolder.reset();
        DynamicRoutingDataSourceHolder.ds("slave");
        connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("SELECT * FROM sss");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String value = resultSet.getString("value");
            System.out.printf("id=%s,value=%s\r\n", id, value);
        }
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test1?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("123456");
        dataSource.add("slave", hikariDataSource, true);

        connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("SELECT * FROM sss");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String value = resultSet.getString("value");
            System.out.printf("id=%s,value=%s\r\n", id, value);
        }
        dataSource.remove("slave");
        dataSource.close();
    }
}

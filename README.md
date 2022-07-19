# 如果在使用过程中有什么需求或者问题，欢迎提issue
`目前支持jpa,mybatis,mybatis-plus以及其他,相关demo请查看dynamic-datasource-spring-boot-examples`
## 配置，支持配置引用以及定制配置
- `配置引用，相当于通用配置，通过ref指定通用配置的前缀`
- `定制配置，只属于对应数据源的配置，优先级比ref高，可覆盖ref引用的配置`
- `配置参数无自动提示，需要使用者针对对应的数据库连接池中的set方法做相应设置`
```yaml
spring:
  ds:
    hikari:
      maximum-pool-size: 10
      driver-class-name: com.mysql.cj.jdbc.Driver
      idleTimeout: 300000
      auto-commit: false
      connection-timeout: 5000
  dynamic:
    primary: master
    data-sources:
      master:
        type: com.zaxxer.hikari.HikariDataSource
        maxLifetime: 600000
        pool-name: master
        connection-test-query: SELECT 1
        minimumIdle: 5
        url: jdbc:mysql://mysql.lhstack.dev:3306/test?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true
        ref: spring.ds.hikari
        connection-timeout: 3000
        idle-timeout: 300000
        username: root
        password: 123456
      slave:
        type: com.zaxxer.hikari.HikariDataSource
        url: jdbc:mysql://mysql.lhstack.dev:3306/test1?characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true
        driver-class-name: com.mysql.cj.jdbc.Driver
        connection-test-query: SELECT 'X'
        pool-name: slave
        maxLifetime: 1800000
        idleTimeout: 600000
        maximum-pool-size: 3
        minimumIdle: 1
        username: root
        password: 123456
```
## 切换数据源
`在类或者方法上面加上DS指定需要使用的数据源即可`
```java
@DS("slave")
@Repository
public interface SssMapper extends BaseMapper<Sss> {

    @Insert("INSERT INTO sss(`value`) VALUES(#{value})")
    int insert(@Param("value") String value);
}
```
```java
    @Override
    @DS("slave")
    public void run(String... args) throws Exception {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("SET TRANSACTION READ ONLY");
        }
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sss");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(resultSet);
        connection.commit();
        PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO sss(`value`) VALUES('测试数据')");
        System.out.println(preparedStatement1.executeUpdate());
        connection.commit();
        System.out.println(connection);
    }
```
## 多数据源事务
- `如下TestService中的rollbackService方法，当抛出异常，aaaService.insert和sssService.insert会回滚，而aaaService.insert1不会回滚，因为他没有加入事务`
- `如下rollbackServiceExpectArithmeticException，当抛出异常，都不会回滚，因为事务期望的异常是ArithmeticException`

`TestService.java`
```java
package com.lhstack.dynamic.datasource.mp.service;

import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.mapper.AaaMapper;
import com.lhstack.dynamic.datasource.mp.mapper.SssMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:24
 * @Modify By
 */
@Service
public class TestService {

    @Autowired
    private AaaMapper aaaMapper;

    @Autowired
    private SssMapper sssMapper;

    @Autowired
    private SssService sssService;

    @Autowired
    private AaaService aaaService;

    @Transactional(rollbackFor = Exception.class)
    public void rollbackService(){
        this.aaaService.insert("rollback-master-service");
        this.aaaService.insert1("rollback-master1-service");
        this.sssService.insert("rollback-slave-service");
        throw new RuntimeException("err");
    }

    @Transactional(rollbackFor = ArithmeticException.class)
    public void rollbackServiceExpectArithmeticException(){
        this.aaaService.insert("master-service");
        this.aaaService.insert1("master1-service");
        this.sssService.insert("slave-service");
        throw new RuntimeException("err");
    }
    
}
```
`AaaService.java`
```java
package com.lhstack.dynamic.datasource.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.entity.Aaa;
import com.lhstack.dynamic.datasource.mp.mapper.AaaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 12:36
 * @Modify By
 */
@Service
public class AaaService extends ServiceImpl<AaaMapper, Aaa> implements IService<Aaa> {


    public void insert(String value) {
        this.save(new Aaa().setValue(value));
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.NEVER)
    public void insert1(String master) {
        this.save(new Aaa().setValue(master));
    }
}
```
`SssService.java`
```java
package com.lhstack.dynamic.datasource.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhstack.dynamic.datasource.mp.entity.Sss;
import com.lhstack.dynamic.datasource.mp.mapper.SssMapper;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 12:36
 * @Modify By
 */
@Service
public class SssService extends ServiceImpl<SssMapper, Sss> implements IService<Sss> {

    public void insert(String value) {
        this.save(new Sss().setValue(value));
    }
}
```

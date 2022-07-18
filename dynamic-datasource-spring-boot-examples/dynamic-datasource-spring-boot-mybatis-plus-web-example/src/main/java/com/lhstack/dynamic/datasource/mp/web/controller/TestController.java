package com.lhstack.dynamic.datasource.mp.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.web.entity.Aaa;
import com.lhstack.dynamic.datasource.mp.web.entity.Sss;
import com.lhstack.dynamic.datasource.mp.web.service.AaaService;
import com.lhstack.dynamic.datasource.mp.web.service.SssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 12:59
 * @Modify By
 */
@RestController
public class TestController {

    @Autowired
    private AaaService aaaService;

    @Autowired
    private SssService sssService;

    @Autowired
    private DataSource dataSource;


    @GetMapping("ds")
    public String ds(){
        return dataSource.toString();
    }

    @GetMapping("query")
    @Transactional(readOnly = true)
    public String query() {
        return this.aaaService.getOne(new LambdaQueryWrapper<Aaa>()
                .eq(Aaa::getId, 1)).getValue()
                +
                this.sssService.getOne(new LambdaQueryWrapper<Sss>()
                        .eq(Sss::getId, 1)).getValue();
    }

    @GetMapping
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    public String insert(@RequestParam(value = "msg", defaultValue = "") String msg) throws InterruptedException {
        Aaa aaa = aaaService.queryById(2003);
        System.out.println(aaa);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    aaaService.insert1("aaaServiceInsert1: " + msg);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        aaaService.insert("aaaServiceInsert: " + msg);
        aaaService.insert("aaaServiceInsert: " + msg);
        aaaService.insert1("aaaServiceInsert1: " + msg);
        sssService.insert("sssServiceInsert1: " + msg);
        if (StringUtils.isEmpty(msg)) {
            throw new RuntimeException("msg cannot be empty");
        }
        return msg;
    }
}

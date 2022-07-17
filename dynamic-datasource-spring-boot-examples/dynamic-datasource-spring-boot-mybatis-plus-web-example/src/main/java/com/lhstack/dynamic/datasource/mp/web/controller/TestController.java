package com.lhstack.dynamic.datasource.mp.web.controller;

import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.web.service.AaaService;
import com.lhstack.dynamic.datasource.mp.web.service.SssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    public String insert(@RequestParam(value = "msg", defaultValue = "") String msg) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    aaaService.insert1("aaaServiceInsert1: " + msg);
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        aaaService.insert("aaaServiceInsert: " + msg);
        aaaService.insert1("aaaServiceInsert1: " + msg);
        sssService.insert("sssServiceInsert1: " + msg);
        if (StringUtils.isEmpty(msg)) {
            throw new RuntimeException("msg cannot be empty");
        }
        return msg;
    }
}

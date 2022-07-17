package com.lhstack.dynamic.datasource.jpa.service;

import com.lhstack.dynamic.datasource.annotation.Transactional;
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
    private AaaService aaaService;

    @Autowired
    private SssService sssService;

    @Transactional(rollbackFor = Exception.class)
    public void rollback() {
        this.aaaService.insert("rollback-master");
        this.aaaService.insert1("rollback-master1");
        this.sssService.insert("rollback-slave");
        throw new RuntimeException("err");
    }

    @Transactional(rollbackFor = ArithmeticException.class)
    public void rollbackExpectArithmeticException() {
        this.aaaService.insert("master");
        this.aaaService.insert1("master1");
        this.sssService.insert("slave");
        throw new RuntimeException("err");
    }
}

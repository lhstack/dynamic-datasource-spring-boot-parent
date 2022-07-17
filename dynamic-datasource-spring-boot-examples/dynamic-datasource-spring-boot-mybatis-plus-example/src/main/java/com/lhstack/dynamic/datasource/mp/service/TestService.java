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

    @Transactional(rollbackFor = Exception.class)
    public void rollback() {
        this.aaaMapper.insert("master");
        this.aaaMapper.insert1("master1");
        this.sssMapper.insert("slave");
        throw new RuntimeException("err");
    }

    @Transactional(rollbackFor = ArithmeticException.class)
    public void rollbackExpectArithmeticException() {
        this.aaaMapper.insert("master");
        this.aaaMapper.insert1("master1");
        this.sssMapper.insert("slave");
        throw new RuntimeException("err");
    }
}

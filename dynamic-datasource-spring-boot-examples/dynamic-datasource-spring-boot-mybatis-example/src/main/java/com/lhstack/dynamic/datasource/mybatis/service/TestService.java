package com.lhstack.dynamic.datasource.mybatis.service;

import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mybatis.mapper.AaaMapper;
import com.lhstack.dynamic.datasource.mybatis.mapper.SssMapper;
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

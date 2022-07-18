package com.lhstack.dynamic.datasource.mp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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


    @Transactional(readOnly = true)
    public String queryOne() {
        return this.getOne(new QueryWrapper<Aaa>().last("LIMIT 1")).toString();
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.NEVER)
    public void insert1(String master) {
        this.save(new Aaa().setValue(master));
    }
}

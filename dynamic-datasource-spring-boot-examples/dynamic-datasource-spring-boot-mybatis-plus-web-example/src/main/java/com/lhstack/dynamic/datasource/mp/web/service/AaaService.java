package com.lhstack.dynamic.datasource.mp.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.web.entity.Aaa;
import com.lhstack.dynamic.datasource.mp.web.mapper.AaaMapper;
import org.springframework.stereotype.Service;
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

    @Transactional(readOnly = false, propagation = Propagation.NEVER)
    public void insert1(String master) {
        this.save(new Aaa().setValue(master));
    }
}

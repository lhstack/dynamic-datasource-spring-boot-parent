package com.lhstack.dynamic.datasource.mp.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhstack.dynamic.datasource.mp.web.entity.Sss;
import com.lhstack.dynamic.datasource.mp.web.mapper.SssMapper;
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

package com.lhstack.dynamic.datasource.jpa.service;

import com.lhstack.dynamic.datasource.jpa.entity.Sss;
import com.lhstack.dynamic.datasource.jpa.repository.SssRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 10:55
 * @Modify By
 */
@Service
public class SssService {

    @Autowired
    private SssRepository sssRepository;

    public void insert(String value) {
        sssRepository.save(new Sss().setValue(value));
    }
}

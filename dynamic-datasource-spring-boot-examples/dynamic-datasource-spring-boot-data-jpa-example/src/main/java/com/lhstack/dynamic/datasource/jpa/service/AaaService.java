package com.lhstack.dynamic.datasource.jpa.service;

import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.jpa.entity.Aaa;
import com.lhstack.dynamic.datasource.jpa.repository.AaaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 10:55
 * @Modify By
 */
@Service
public class AaaService {

    @Autowired
    private AaaRepository aaaRepository;

    public void insert(String value){
        aaaRepository.save(new Aaa().setValue(value));
    }

    @Transactional(readOnly = false,isolation = Isolation.REPEATABLE_READ,propagation = Propagation.NEVER)
    public void  insert1(String master){
        aaaRepository.save(new Aaa().setValue(master));
    }
}

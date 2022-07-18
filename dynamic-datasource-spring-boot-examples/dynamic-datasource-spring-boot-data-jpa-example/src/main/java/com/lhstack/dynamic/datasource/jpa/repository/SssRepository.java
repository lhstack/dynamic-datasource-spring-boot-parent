package com.lhstack.dynamic.datasource.jpa.repository;

import com.lhstack.dynamic.datasource.annotation.DS;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.jpa.entity.Sss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/17 10:55
 * @Modify By
 */

public interface SssRepository extends JpaRepository<Sss, Integer> {


    @DS("slave")
    @Transactional
    List<Sss> queryAllBy();
}

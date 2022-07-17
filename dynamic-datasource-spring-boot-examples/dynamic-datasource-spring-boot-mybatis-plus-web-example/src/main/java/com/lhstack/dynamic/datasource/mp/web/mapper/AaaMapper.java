package com.lhstack.dynamic.datasource.mp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhstack.dynamic.datasource.annotation.Transactional;
import com.lhstack.dynamic.datasource.mp.web.entity.Aaa;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:22
 * @Modify By
 */
@Repository
public interface AaaMapper extends BaseMapper<Aaa> {

    @Insert("INSERT INTO aaa(`value`) VALUES(#{value})")
    int insert(@Param("value") String value);

    @Insert("INSERT INTO aaa(`value`) VALUES(#{value})")
    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.NEVER)
    void insert1(@Param("value") String master);
}

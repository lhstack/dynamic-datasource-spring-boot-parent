package com.lhstack.dynamic.datasource.mybatis.mapper;

import com.lhstack.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:22
 * @Modify By
 */
@DS("slave")
@Repository
public interface SssMapper {

    @Insert("INSERT INTO sss(`value`) VALUES(#{value})")
    int insert(@Param("value") String value);
}

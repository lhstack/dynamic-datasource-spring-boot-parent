package com.lhstack.dynamic.datasource.mp.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:22
 * @Modify By
 */
@TableName(value = "sss")
public class Sss {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String value;

    public Integer getId() {
        return id;
    }

    public Sss setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Sss setValue(String value) {
        this.value = value;
        return this;
    }
}

package com.lhstack.dynamic.datasource.jpa.entity;

import javax.persistence.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:21
 * @Modify By
 */
@Entity
@Table(name = "aaa")
public class Aaa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String value;

    public Integer getId() {
        return id;
    }

    public Aaa setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Aaa setValue(String value) {
        this.value = value;
        return this;
    }
}

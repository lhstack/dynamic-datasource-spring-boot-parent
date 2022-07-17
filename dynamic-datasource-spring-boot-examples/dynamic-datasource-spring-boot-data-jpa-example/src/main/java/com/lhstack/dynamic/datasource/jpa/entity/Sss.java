package com.lhstack.dynamic.datasource.jpa.entity;

import javax.persistence.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 23:22
 * @Modify By
 */
@Entity
@Table(name = "sss")
public class Sss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

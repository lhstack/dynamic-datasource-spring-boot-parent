package com.lhstack.dynamic.datasource.proxy;

import java.sql.SQLException;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/7/16 15:32
 * @Modify By
 */
@FunctionalInterface
public interface SqlExceptionSupplier<T> {

    T get() throws SQLException;
}

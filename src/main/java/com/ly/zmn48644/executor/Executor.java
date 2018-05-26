package com.ly.zmn48644.executor;

import com.ly.zmn48644.mapping.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * 执行器接口
 */
public interface Executor {
    //查询
    <E> List<E> query(MappedStatement mappedStatement, Object parameter) throws SQLException;

    //更新
    int update(MappedStatement mappedStatement, Object parameter);
}

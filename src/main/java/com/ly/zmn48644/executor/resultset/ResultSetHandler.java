package com.ly.zmn48644.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 结果集处理接口
 *
 */
public interface ResultSetHandler {
    /**
     * 解析 statement 处理结果集
     *
     * @param stmt
     * @param <E>
     * @return
     * @throws SQLException
     */
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;
}

package com.ly.zmn48644.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Statement处理器接口,此接口中定义的方法是直接操作 JDBC statement 的方法.
 * 由于JDBC中提供了 三种 statement 具体实现,基于这三种实现MyBatis 提供了三种 StatementHandler 的实现.
 */
public interface StatementHandler {
    /**
     * 创建 Statement
     *
     * @param connection         根据此连接创建 statement 对象
     * @param transactionTimeout
     * @return
     */
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    /**
     * 给 Statement 设置参数
     *
     * @param statement
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * 调用底层Statement执行更新操作
     *
     * @param statement
     * @return
     */
    int update(Statement statement) throws SQLException;

    /**
     * 调用底层Statement执行查询操作
     *
     * @param statement
     * @param <E>
     * @return
     */
    <E> List<E> query(Statement statement) throws SQLException;
}

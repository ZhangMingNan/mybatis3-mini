package com.ly.zmn48644.transaction;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 将事务相关的操作,封装到 Transaction(事务管理器) 中.
 */
public interface Transaction {
    /**
     * 获取数据库连接
     *
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     *
     * @throws SQLException
     */
    void commint() throws SQLException;

    /**
     * 回滚事务
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 关闭数据库连接
     *
     * @throws SQLException
     */
    void close() throws SQLException;

}

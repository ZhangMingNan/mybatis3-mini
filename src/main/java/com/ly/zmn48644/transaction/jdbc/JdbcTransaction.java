package com.ly.zmn48644.transaction.jdbc;

import com.ly.zmn48644.session.TransactionIsolationLevel;
import com.ly.zmn48644.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 通过JDBC的方式管理事务实现
 */
public class JdbcTransaction implements Transaction {

    private DataSource dataSource;
    private Connection connection;
    private TransactionIsolationLevel level;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level) {
        this.dataSource = dataSource;
        this.level = level;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection = openConnection();
    }

    private Connection openConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        if (level != null) {
            //设置隔离级别
            connection.setTransactionIsolation(level.getLevel());
        }
        return connection;
    }

    @Override
    public void commint() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

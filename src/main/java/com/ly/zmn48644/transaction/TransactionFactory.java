package com.ly.zmn48644.transaction;

import com.ly.zmn48644.session.TransactionIsolationLevel;

import javax.sql.DataSource;

/**
 * 事务管理器工厂
 */
public interface TransactionFactory {

    /**
     * 根据数据源创建事务管理器
     * @param dataSource
     * @param level
     * @param autoCommit
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}

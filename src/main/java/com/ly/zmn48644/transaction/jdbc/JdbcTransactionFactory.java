package com.ly.zmn48644.transaction.jdbc;

import com.ly.zmn48644.session.TransactionIsolationLevel;
import com.ly.zmn48644.transaction.Transaction;
import com.ly.zmn48644.transaction.TransactionFactory;

import javax.sql.DataSource;

public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level);
    }
}

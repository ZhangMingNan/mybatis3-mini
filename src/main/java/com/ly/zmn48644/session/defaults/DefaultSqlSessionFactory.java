package com.ly.zmn48644.session.defaults;

import com.ly.zmn48644.executor.Executor;
import com.ly.zmn48644.mapping.Environment;
import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.session.ExecutorType;
import com.ly.zmn48644.session.SqlSession;
import com.ly.zmn48644.session.SqlSessionFactory;
import com.ly.zmn48644.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(ExecutorType.SIMPLE);
    }

    private SqlSession openSessionFromDataSource(ExecutorType execType) {
        try {
            Environment environment = this.configuration.getEnvironment();
            Transaction transaction = environment.getTransactionFactory().newTransaction(environment.getDatasource(), null, false);
            final Executor executor = configuration.newExecutor(transaction, execType);
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            throw new RuntimeException("打开会话异常!");
        }
    }
}

package com.ly.zmn48644.mapping;

import com.ly.zmn48644.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * 框架运行环境对象
 * 主要封装的是数据源和事务管理器工厂
 */
public class Environment {
    private String id;
    private TransactionFactory transactionFactory;
    private DataSource datasource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource datasource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.datasource = datasource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }
}

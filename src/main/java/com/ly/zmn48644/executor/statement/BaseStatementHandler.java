package com.ly.zmn48644.executor.statement;

import com.ly.zmn48644.executor.parameter.ParameterHandler;
import com.ly.zmn48644.executor.resultset.ResultSetHandler;
import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.session.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseStatementHandler implements StatementHandler {

    protected final BoundSql boundSql;

    protected final ResultSetHandler resultSetHandler;

    protected final ParameterHandler parameterHandler;

    protected final Configuration configuration;

    public BaseStatementHandler(MappedStatement ms, Object parameter, BoundSql boundSql) {
        this.configuration = ms.getConfiguration();
        this.boundSql = boundSql;
        this.parameterHandler = this.configuration.newParameterHandler(ms,parameter,boundSql);
        this.resultSetHandler = this.configuration.newResultSetHandler(ms,boundSql,parameterHandler);
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement statement = instantiateStatement(connection);
        //TODO 设置超时时间

        //TODO 设置fetchSize
        return statement;
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

}

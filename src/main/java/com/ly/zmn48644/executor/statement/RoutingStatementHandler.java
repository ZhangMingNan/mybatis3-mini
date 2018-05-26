package com.ly.zmn48644.executor.statement;

import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 并不是一个 StatementHandler 的实现类
 */
public class RoutingStatementHandler implements StatementHandler {

    private StatementHandler delegate;

    public RoutingStatementHandler(MappedStatement ms, Object parameter) {
        BoundSql boundSql = ms.getSqlSource().getBoundSql(parameter);

        switch (ms.getStatementType()) {
            case CALLABLE:
                delegate = new CallableStatementHandler(ms, parameter, boundSql);
                break;
            case PREPARED:
                delegate = new PreparedStatementHandler(ms, parameter, boundSql);
                break;
            case STATEMENT:
                delegate = new SimpleStatementHandler(ms, parameter, boundSql);
                break;
            default:
                throw new RuntimeException("StatementType 配置错误!");
        }
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        return delegate.prepare(connection, transactionTimeout);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        delegate.parameterize(statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return delegate.update(statement);
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        return delegate.query(statement);
    }
}

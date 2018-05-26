package com.ly.zmn48644.executor.statement;

import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PreparedStatementHandler extends BaseStatementHandler {
    public PreparedStatementHandler(MappedStatement ms, Object parameter, BoundSql boundSql) {
        super(ms, parameter, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(boundSql.getSql());
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
            //在这里设置参数
        parameterHandler.setParameters((PreparedStatement) statement);
    }

    @Override
    public int update(Statement statement) {
        return 0;
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) statement;
        preparedStatement.execute();
        return resultSetHandler.handleResultSets(preparedStatement);
    }
}

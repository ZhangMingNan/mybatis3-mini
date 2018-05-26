package com.ly.zmn48644.executor.statement;

import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleStatementHandler extends BaseStatementHandler {


    public SimpleStatementHandler(MappedStatement ms, Object parameter, BoundSql boundSql) {
        super(ms,parameter,boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) {

    }

    @Override
    public int update(Statement statement) throws SQLException {
        String sql = boundSql.getSql();
        int rows;
        statement.executeUpdate(sql);
        rows = statement.getUpdateCount();
        return rows;
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        String sql = boundSql.getSql();
        statement.executeQuery(sql);
        //调用ResultSetHandler处理结果集
        return resultSetHandler.handleResultSets(statement);
    }
}

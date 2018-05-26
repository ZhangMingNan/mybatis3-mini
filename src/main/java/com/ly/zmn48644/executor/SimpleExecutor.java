package com.ly.zmn48644.executor;

import com.ly.zmn48644.executor.statement.StatementHandler;
import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 普通执行器实现
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Transaction transaction) {
        super(transaction);
    }

    @Override
    public <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter) throws SQLException {
        //调用全局配置对象获取 StatementHandler 对象
        Configuration configuration = mappedStatement.getConfiguration();

        //获取 statement handleNode
        StatementHandler statementHandler = configuration.newStatementHandler(mappedStatement, parameter);

        //创建 statement
        Statement statement = prepareStatement(statementHandler);

        return statementHandler.query(statement);
    }

    private Statement prepareStatement(StatementHandler statementHandler) throws SQLException {
        //获取数据库连接
        Connection connection = getConnection();
        //创建 statement
        Statement statement = statementHandler.prepare(connection, 1);
        //处理 占位符
        statementHandler.parameterize(statement);
        return statement;
    }

    @Override
    public int doUpdate(MappedStatement mappedStatement, Object parameter) {
        return 0;
    }

}

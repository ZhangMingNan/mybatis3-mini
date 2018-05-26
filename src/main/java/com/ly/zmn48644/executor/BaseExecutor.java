package com.ly.zmn48644.executor;

import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 执行器实现类的抽象父类
 * 抽象父类中使用模板方法方法模式,具体执行过程通过抽象方法委托给具体的三个实现类完成.
 */
public abstract class BaseExecutor implements Executor {

    public BaseExecutor(Transaction transaction) {
        this.transaction = transaction;
    }

    //事务管理器
    //子类执行器中的 数据库连接从此获取.
    protected Transaction transaction;

    /**
     * 获取 数据库连接
     * @return
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        return transaction.getConnection();
    }

    /**
     * 面向上层模块的查询方法
     *
     * @param mappedStatement
     * @param parameter
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> query(MappedStatement mappedStatement, Object parameter) throws SQLException {
        return doQuery(mappedStatement, parameter);
    }

    /**
     * 面向上层模块的更新方法
     *
     * @param mappedStatement
     * @param parameter
     * @return
     */
    @Override
    public int update(MappedStatement mappedStatement, Object parameter) {
        return doUpdate(mappedStatement, parameter);
    }

    /**
     * 委托给子类的执行查询方法
     *
     * @param mappedStatement
     * @param parameter
     * @param <E>
     * @return
     */
    public abstract <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter) throws SQLException;

    /**
     * 委托给子类的执行更新方法
     *
     * @param mappedStatement
     * @param parameter
     * @return
     */
    public abstract int doUpdate(MappedStatement mappedStatement, Object parameter);
}

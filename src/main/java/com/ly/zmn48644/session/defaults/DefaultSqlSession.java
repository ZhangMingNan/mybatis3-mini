package com.ly.zmn48644.session.defaults;

import com.ly.zmn48644.executor.Executor;
import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.session.SqlSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SqlSession的默认实现类
 */
public class DefaultSqlSession implements SqlSession {
    private final Configuration configuration;

    private final Executor executor;


    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement,null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        //调用 selectList 方法
        List<T> list = this.selectList(statement,parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("selectOne 方法预期返回结果行数为1,但是返回了多行!");
        }
        return null;
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            //调用执行器完成查询操作
            return executor.query(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("执行数据库查询异常!");
        }
    }

    //这里返回的是指定接口的代理对象
    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}

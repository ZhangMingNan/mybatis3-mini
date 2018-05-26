package com.ly.zmn48644.executor.parameter;

import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.type.JdbcType;
import com.ly.zmn48644.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DefaultParameterHandler implements ParameterHandler {
    private final MappedStatement ms;
    private final Object parameter;
    private final BoundSql boundSql;

    public DefaultParameterHandler(MappedStatement ms, Object parameter, BoundSql boundSql) {
        this.ms = ms;
        this.parameter = parameter;
        this.boundSql = boundSql;
    }


    @Override
    public Object getParameterObject() {
        return parameter;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        //这里设置的参数是 通过 parameterMap 配置的参数映射, 这个功能一般不是很常用.

    }
}





























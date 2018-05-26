package com.ly.zmn48644.type.impl;

import com.ly.zmn48644.type.JdbcType;
import com.ly.zmn48644.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * int 类型转换器
 */
public class IntegerTypeHandler implements TypeHandler<Integer> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter);
    }

    @Override
    public Integer getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }
}

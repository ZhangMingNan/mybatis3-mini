package com.ly.zmn48644.type.impl;

import com.ly.zmn48644.type.JdbcType;
import com.ly.zmn48644.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字符串类型的转换器
 */
public class StringTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }
    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }
}


package com.ly.zmn48644.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

    /**
     *
     * @param ps
     * @param index
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    void setParameter(PreparedStatement ps, int index, T parameter, JdbcType jdbcType) throws SQLException;

    T getResult(ResultSet rs, String columnName) throws SQLException;

}

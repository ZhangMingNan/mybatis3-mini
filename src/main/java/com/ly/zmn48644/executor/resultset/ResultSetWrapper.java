package com.ly.zmn48644.executor.resultset;

import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.type.JdbcType;
import com.ly.zmn48644.type.TypeHandler;
import com.ly.zmn48644.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 为了更方便的操作ResultSet提供了其包装类
 */
public class ResultSetWrapper {

    private final ResultSet resultSet;

    private final List<String> columnNames = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();
    private final List<JdbcType> jdbcTypes = new ArrayList<>();

    private final List<String> unMappedColumnNames = new ArrayList<>();

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<>();

    public List<String> getUnMappedColumnNames() {
        if (unMappedColumnNames.size() == 0) {
            loadUnMappedColumnNames();
        }
        return unMappedColumnNames;
    }

    private void loadUnMappedColumnNames() {
        for (String columnName : columnNames) {
            unMappedColumnNames.add(columnName);
        }
    }

    public ResultSetWrapper(ResultSet rs, Configuration configuration) throws SQLException {
        super();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.resultSet = rs;
        final ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            //根据是否使用别名判断
            columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
            jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
            classNames.add(metaData.getColumnClassName(i));
        }
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<String> getClassNames() {
        return Collections.unmodifiableList(classNames);
    }

    public JdbcType getJdbcType(String columnName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                return jdbcTypes.get(i);
            }
        }
        return null;
    }

    public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
        TypeHandler<?> handler = null;

        Map<Class<?>, TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
        //先从缓存中查找
        if (columnHandlers == null) {
            columnHandlers = new HashMap<>();
            typeHandlerMap.put(columnName, columnHandlers);
        } else {
            handler = columnHandlers.get(propertyType);
        }
        //如果缓存中没有
        if (handler == null) {
            //JdbcType jdbcType = getJdbcType(columnName);
            handler = typeHandlerRegistry.getTypeHandler(propertyType);
            columnHandlers.put(propertyType, handler);
        }
        return handler;
    }

}

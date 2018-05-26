package com.ly.zmn48644.executor.resultset;

import com.ly.zmn48644.executor.parameter.ParameterHandler;
import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.mapping.ResultMap;
import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.factory.DefaultObjectFactory;
import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.type.TypeHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的结果集处理器
 * 核心方法 handleResultSets 用于将jdbc原生ResultSet对象中的数据设置到对应的结果对象中.
 */
public class DefaultResultSetHandler implements ResultSetHandler {
    private final MappedStatement ms;
    private final BoundSql boundSql;
    private final ParameterHandler parameterHandler;

    private final ObjectFactory objectFactory;

    //全局配置对象
    private final Configuration configuration;

    public DefaultResultSetHandler(MappedStatement ms, BoundSql boundSql, ParameterHandler parameterHandler) {
        this.ms = ms;
        this.boundSql = boundSql;
        this.parameterHandler = parameterHandler;
        this.objectFactory = ms.getConfiguration().getObjectFactory();
        this.configuration = ms.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {

        final List<Object> multipleResults = new ArrayList<>();

        ResultSet resultSet = stmt.getResultSet();

        ResultMap resultMap = ms.getResultMap();
        //包装resultSet
        ResultSetWrapper rsw = new ResultSetWrapper(resultSet, configuration);
        handleResultSet(rsw, resultMap, multipleResults);
        return multipleResults;
    }

    //处理单个ResultSet
    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults) throws SQLException {
        while (rsw.getResultSet().next()) {
            Object rowValue = getRowValue(rsw, resultMap);
            multipleResults.add(rowValue);
        }
    }

    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        //创建一个resultType指定的对象
        Object rowValue = createResultObject(resultMap);
        final MetaObject metaObject = configuration.newMetaObject(rowValue);
        applyAutomaticMappings(rsw, rowValue, metaObject);
        return rowValue;
    }

    /**
     * 简单对象的自动自动映射
     *
     * @param rowValue
     * @param metaObject
     */
    private void applyAutomaticMappings(ResultSetWrapper rsw, Object rowValue, MetaObject metaObject) throws SQLException {
        List<UnMappedColumnAutoMapping> autoMappings = createAutomaticMappings(rsw, rowValue, metaObject);
        for (UnMappedColumnAutoMapping mapping : autoMappings) {
            //转换jdbc类型为java类型.
            final Object value = mapping.getTypeHandler().getResult(rsw.getResultSet(), mapping.getColumn());
            metaObject.setValue(mapping.getProperty(), value);
        }
    }

    /**
     * 创建自动映射器列表
     *
     * @param rsw
     * @param rowValue
     * @param metaObject
     * @return
     */
    private List<UnMappedColumnAutoMapping> createAutomaticMappings(ResultSetWrapper rsw, Object rowValue, MetaObject metaObject) {
        List<UnMappedColumnAutoMapping> unMappedColumnAutoMappings = new ArrayList<>();
        List<String> unMappedColumnNames = rsw.getUnMappedColumnNames();
        for (String columnName : unMappedColumnNames) {
            String propertyName = columnName;
            //根据 propertyName 查找 property, propertyName是支持多级的比如 info.time
            final String property = metaObject.findProperty(propertyName, configuration.isMapUnderscoreToCamelCase());
            final Class<?> propertyType = metaObject.getSetterType(property);
            final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
            UnMappedColumnAutoMapping mapping = new UnMappedColumnAutoMapping(columnName, property, typeHandler, propertyType.isPrimitive());
            unMappedColumnAutoMappings.add(mapping);
        }
        return unMappedColumnAutoMappings;
    }

    /**
     * 创建结果对象
     *
     * @param resultMap
     * @return
     */
    private Object createResultObject(ResultMap resultMap) {
        final Class<?> resultType = resultMap.getType();
        return objectFactory.create(resultType);
    }

}
















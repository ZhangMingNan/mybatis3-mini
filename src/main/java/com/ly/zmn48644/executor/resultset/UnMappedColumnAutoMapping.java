package com.ly.zmn48644.executor.resultset;

import com.ly.zmn48644.type.TypeHandler;

/**
 * 封装表字段和属性的映射关系
 */
public class UnMappedColumnAutoMapping {
    //表字段
    private final String column;
    //对应的属性
    private final String property;
    //类型转换器
    private final TypeHandler<?> typeHandler;
    //属性是否是基本类型
    private final boolean primitive;

    public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
        this.column = column;
        this.property = property;
        this.typeHandler = typeHandler;
        this.primitive = primitive;
    }

    public String getColumn() {
        return column;
    }

    public String getProperty() {
        return property;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public boolean isPrimitive() {
        return primitive;
    }
}

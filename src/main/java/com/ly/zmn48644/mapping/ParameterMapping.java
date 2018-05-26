package com.ly.zmn48644.mapping;

import com.ly.zmn48644.type.JdbcType;
import com.ly.zmn48644.type.TypeHandler;

/**
 * 封装上层应用调用接口时传入的参数信息
 */
public class ParameterMapping {
    //参数属性名
    private String property;
    //参数的javaType
    private Class<?> javaType = Object.class;
    //参数的jdbcType
    private JdbcType jdbcType;
    //设置查询参数时,java类型和jdbc类型的转换器
    private TypeHandler<?> typeHandler;

    public ParameterMapping(String property, Class<?> javaType, JdbcType jdbcType, TypeHandler<?> typeHandler) {
        this.property = property;
        this.javaType = javaType;
        this.jdbcType = jdbcType;
        this.typeHandler = typeHandler;
    }

    @Override
    public String toString() {
        return "ParameterMapping{" +
                "property='" + property + '\'' +
                ", javaType=" + javaType +
                ", jdbcType=" + jdbcType +
                ", typeHandler=" + typeHandler +
                '}';
    }
}

package com.ly.zmn48644.type;

import com.ly.zmn48644.type.impl.IntegerTypeHandler;
import com.ly.zmn48644.type.impl.StringTypeHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型转换器注册中心
 */
public final class TypeHandlerRegistry {
    private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<Class<?>, TypeHandler<?>>();

    //注册基本的类型转换器
    public TypeHandlerRegistry() {
        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(String.class, new StringTypeHandler());
    }

    public void register(Class<?> javaType, TypeHandler typeHandler) {
        ALL_TYPE_HANDLERS_MAP.put(javaType, typeHandler);
    }

    /**
     *
     * @param propertyType
     * @return
     */
    public TypeHandler<?> getTypeHandler(Class<?> propertyType) {

        return ALL_TYPE_HANDLERS_MAP.get(propertyType);
    }
}

package com.ly.zmn48644.binding;

import com.ly.zmn48644.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * 映射接口注册中心
 * 主配置文件中定义的 mapper 会注册在此.
 */
public class MapperRegistry {

    /**
     * 缓存接口类
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, MapperProxyFactory<?>>();

    /**
     * 从注册中心获取映射接口实例
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        //首先从缓存中获取
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            //如果不存在的话创建
            mapperProxyFactory = new MapperProxyFactory<>();
        }
        //创建 代理类
        MapperProxy proxy = new MapperProxy(sqlSession, type);
        //定义代理接口
        Class<T>[] classes = new Class[]{type};
        //生成代理对象
        return mapperProxyFactory.newInstance(proxy, classes);
    }

    /**
     * 注册一个映射接口
     * 一般是从主配置文件解析到的mapper
     */
    public <T> void addMapper(Class<T> type) {
        MapperProxyFactory<T> mapperProxyFactory = new MapperProxyFactory<>();
        knownMappers.put(type, mapperProxyFactory);
    }
}

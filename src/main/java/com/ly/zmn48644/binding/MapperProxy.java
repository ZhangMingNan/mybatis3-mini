package com.ly.zmn48644.binding;

import com.ly.zmn48644.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 此类是Mapper接口的具体代理类
 * 所有调用mapper接口的方法都会被此类拦截
 * 此处应用的是动态代理,具体通过JDK提供的动态代理机制实现.
 */
public class MapperProxy implements InvocationHandler {

    private SqlSession sqlSession;

    private Class<?> mapperInterface;

    /**
     * 一个定义的 mapper 接口 对应一个 MapperProxyFactory ,这个对应关系维护在 MapperRegistry类中的 knownMappers 中,
     * 其中key是class类型,value是 一个 MapperProxyFactory
     *
     * mapper中的一个方法 对应 methodCache 中的一个MapperMethod对象
     */
    Map<Method, MapperMethod> methodCache = new HashMap<>();

    public MapperProxy(SqlSession sqlSession,Class<?> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里根据 method 获取对应的 MapperMethod对象,并调用execute方法.
        //这里存在的问题是 methodCache,
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            //如果缓存中不存在则重新创建
            mapperMethod = new MapperMethod(sqlSession.getConfiguration(),this.mapperInterface,method);
            //创建后放入到缓存中
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod.execute(sqlSession,args);
    }
}

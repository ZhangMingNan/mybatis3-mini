
package com.ly.zmn48644.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 默认的反射器工厂类,主要作用其实就是缓存反射器.
 * XMLConfigBuilder
 * DefaultResultSetHandler
 * DefaultMapResultHandler
 * Configuration
 */
public class DefaultReflectorFactory implements ReflectorFactory {
    //是否启用缓存判断字段
    private boolean classCacheEnabled = true;
    //用于存放缓存反射器的map,这里注意使用的是线程安全的map
    private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<Class<?>, Reflector>();

    public DefaultReflectorFactory() {
    }

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
        this.classCacheEnabled = classCacheEnabled;
    }

    @Override
    public Reflector findForClass(Class<?> type) {
        //判断是否开启缓存
        if (classCacheEnabled) {
            //从缓存中获取缓存的反射器
            Reflector cached = reflectorMap.get(type);
            if (cached == null) {
                //缓存中没有取到
                cached = new Reflector(type);
                //放入缓存
                reflectorMap.put(type, cached);
            }
            return cached;
        } else {
            //直接创建反射器
            return new Reflector(type);
        }
    }

}

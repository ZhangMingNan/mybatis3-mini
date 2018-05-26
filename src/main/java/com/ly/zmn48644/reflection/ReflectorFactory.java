package com.ly.zmn48644.reflection;

/**
 * 反射器工厂方法接口
 */
public interface ReflectorFactory {

    //返回判断是否启用缓存
    boolean isClassCacheEnabled();

    //设置是否缓存反射器
    void setClassCacheEnabled(boolean classCacheEnabled);

    //获取类的反射器
    Reflector findForClass(Class<?> type);
}
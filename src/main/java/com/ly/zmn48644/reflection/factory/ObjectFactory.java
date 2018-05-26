
package com.ly.zmn48644.reflection.factory;

import java.util.List;
import java.util.Properties;

/**
 * MyBatis 使用 ObjectFactory 创建所有需要的新对象
 */
public interface ObjectFactory {

    /**
     * 设置配置工厂配置
     *
     * @param properties
     */
    void setProperties(Properties properties);

    /**
     * 使用无参构造方法创建对象
     *
     * @param type Object type
     * @return
     */
    <T> T create(Class<T> type);

    /**
     * 创建对象使用指定的构造方法
     *
     * @param type                Object type
     * @param constructorArgTypes Constructor argument types
     * @param constructorArgs     Constructor argument values
     * @return
     */
    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    /**
     * 判断是集合类
     *
     * @param type Object type
     * @return whether it is a collection or not
     * @since 3.1.0
     */
    <T> boolean isCollection(Class<T> type);

}

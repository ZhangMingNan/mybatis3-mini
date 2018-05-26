
package com.ly.zmn48644.reflection.warpper;


import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * 对对象的包装,抽象了对象的属性信息,定义了一系列查询和设置属性的方法.
 */
public interface ObjectWrapper {

    /**
     * 获取一个属性的值
     *
     * @param prop
     * @return
     */
    Object get(PropertyTokenizer prop);

    /**
     * 设置一个对象中某个属性值
     *
     * @param prop
     * @param value
     */
    void set(PropertyTokenizer prop, Object value);

    /**
     * 查找属性
     *
     * @param name
     * @param useCamelCaseMapping
     * @return
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 获取对象的所有 get 方法名
     *
     * @return
     */
    String[] getGetterNames();

    /**
     * 获取对象的所有 set 方法
     *
     * @return
     */
    String[] getSetterNames();

    /**
     * 获取 对象的 某个属性的 set 方法的参数类型
     * @param name
     * @return
     */
    Class<?> getSetterType(String name);

    /**
     * 获取对象的 get 方法的返回类型
     * @param name
     * @return
     */
    Class<?> getGetterType(String name);

    /**
     * 判断对象是否存在某个 set 方法
     * @param name
     * @return
     */
    boolean hasSetter(String name);

    /**
     * 判断对象是否存在某个 get 方法
     * @param name
     * @return
     */
    boolean hasGetter(String name);

    /**
     * 创建某个属性的值得 MetaObject 对象
     * @param name
     * @param prop
     * @param objectFactory
     * @return
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    /**
     * 判断封装对象是否是 collection 类型
     * @return
     */
    boolean isCollection();

    /**
     * 向 collection 对象中插入一个元素
     * @param element
     */
    void add(Object element);

    /**
     * 向 collection 对象中插入多个元素
     * @param element
     * @param <E>
     */
    <E> void addAll(List<E> element);

}

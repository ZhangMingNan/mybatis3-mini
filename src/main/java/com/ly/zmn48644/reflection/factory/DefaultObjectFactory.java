
package com.ly.zmn48644.reflection.factory;


import com.ly.zmn48644.reflection.ReflectionException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 默认的对象工厂实现
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable {

    private static final long serialVersionUID = -8855120656740914948L;

    /**
     * 根据无参构造方法创建对象的实现
     *
     * @param type Object type
     * @param <T>
     * @return
     */
    @Override
    public <T> T create(Class<T> type) {
        return create(type, null, null);
    }

    /**
     * 指定 class 构造方法参数类型和 构造方法参数创建对象.
     *
     * @param type                Object type 要生产的对象的 calss
     * @param constructorArgTypes Constructor argument types 构造方法参数类型列表
     * @param constructorArgs     Constructor argument values 构造方法参数列表
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        //解析传入 class 如果是接口无法创建,根据指定的规则转换成具体的实现类.
        Class<?> classToCreate = resolveInterface(type);
        //创建对象的实例
        return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
        // no props for default
    }

    <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        try {
            //构造方法对象
            Constructor<T> constructor;
            //如果参数类型列表,和参数列表都是 null 说明调用的是无参构造方法
            if (constructorArgTypes == null || constructorArgs == null) {
                constructor = type.getDeclaredConstructor();
                //设置可访问
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                //调用构造方法创建实例
                return constructor.newInstance();
            }
            //根据参数, 获取有参构造方法.
            constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[constructorArgTypes.size()]));
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            //创建实例
            return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
        } catch (Exception e) {
            //如果抛出异常, 拼装异常数据,封装成反射异常抛出.
            StringBuilder argTypes = new StringBuilder();
            if (constructorArgTypes != null && !constructorArgTypes.isEmpty()) {
                for (Class<?> argType : constructorArgTypes) {
                    argTypes.append(argType.getSimpleName());
                    argTypes.append(",");
                }
                argTypes.deleteCharAt(argTypes.length() - 1); // remove trailing ,
            }
            StringBuilder argValues = new StringBuilder();
            if (constructorArgs != null && !constructorArgs.isEmpty()) {
                for (Object argValue : constructorArgs) {
                    argValues.append(String.valueOf(argValue));
                    argValues.append(",");
                }
                argValues.deleteCharAt(argValues.length() - 1); // remove trailing ,
            }
            throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values (" + argValues + "). Cause: " + e, e);
        }
    }

    /**
     * 解析接口,如果传入的类是接口类型,则需要返回具体的实现类,否则无法创建对象.
     *
     * @param type
     * @return
     */
    protected Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            classToCreate = HashMap.class;
        } else if (type == SortedSet.class) { // issue #510 Collections Support
            classToCreate = TreeSet.class;
        } else if (type == Set.class) {
            classToCreate = HashSet.class;
        } else {
            classToCreate = type;
        }
        return classToCreate;
    }

    /**
     * 判断传入的 类型是否是 集合类型
     * 此方法在多处被使用到,说以即使逻辑很简单,也别封装到此类中,以供整个项目使用.
     * @param type Object type
     * @param <T>
     * @return
     */
    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

}

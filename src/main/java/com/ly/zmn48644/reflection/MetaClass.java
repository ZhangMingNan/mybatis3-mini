
package com.ly.zmn48644.reflection;


import com.ly.zmn48644.reflection.invoker.GetFieldInvoker;
import com.ly.zmn48644.reflection.invoker.Invoker;
import com.ly.zmn48644.reflection.invoker.MethodInvoker;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 类反射模块的封装
 * 谁创建,在什么地方创建,做了什么.
 * VelocitySqlSourceBuilder
 * DefaultResultSetHandler
 * XMLConfigBuilder
 * BeanWrapper
 * SqlSourceBuilder
 * MapperBuilderAssistant
 */
public class MetaClass {

    private final ReflectorFactory reflectorFactory;
    private final Reflector reflector;

    /**
     * 构造方法
     *
     * @param type             反射的原始类型.
     * @param reflectorFactory 反射对象工厂类
     */
    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        //MetClass 的主要功能是对, Reflector 功能的增强,所以大多是直接调用的 reflector中的方法.
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    /**
     * 静态方法创建方式, 和构造方法创建本质是一样的,只是为了调用方便.
     *
     * @param type
     * @param reflectorFactory
     * @return
     */
    public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
        return new MetaClass(type, reflectorFactory);
    }

    /**
     * 获取某个属性的 MetaClass 对象,出现在类结构出现嵌套这种复杂情况下.
     *
     * @param name
     * @return
     */
    public MetaClass metaClassForProperty(String name) {
        Class<?> propType = reflector.getGetterType(name);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    /**
     * 忽略大小写通过属性表达式,来获取准确的属性路径
     *
     * @param name
     * @return
     */
    public String findProperty(String name) {
        StringBuilder prop = buildProperty(name, new StringBuilder());
        return prop.length() > 0 ? prop.toString() : null;
    }

    /**
     * 忽略大小写通过属性表达式,来获取准确的属性路径 是否使用驼峰映射
     *
     * @param name
     * @param useCamelCaseMapping
     * @return
     */
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            name = name.replace("_", "");
        }
        return findProperty(name);
    }

    /**
     * 获取所有get方法列表
     *
     * @return
     */
    public String[] getGetterNames() {
        return reflector.getGetablePropertyNames();
    }

    /**
     * 获取set方法列表
     *
     * @return
     */
    public String[] getSetterNames() {
        return reflector.getSetablePropertyNames();
    }

    /**
     * 根据属性表达式来获取set方法类型
     *
     * @param name
     * @return
     */
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        //使用迭代器模式来处理嵌套属性字段.

        if (prop.hasNext()) {//递归判断条件
            //这里是存在递归调用,每进入一次就深入一级.最后返回最后一级的属性的 set 方法类型.
            MetaClass metaProp = metaClassForProperty(prop.getName());
            //递归调用,获取子属性后传入
            return metaProp.getSetterType(prop.getChildren());
        } else {
            //递归条件不成立返回, 一般就是到最后一级返回.
            return reflector.getSetterType(prop.getName());
        }
    }


    /**
     * 根据属性表达式获取属性get方法返回类型
     *
     * @param name
     * @return
     */
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaClass metaProp = metaClassForProperty(prop);
            return metaProp.getGetterType(prop.getChildren());
        }
        // issue #506. Resolve the type inside a Collection Object
        //这里暂时不深入
        return getGetterType(prop);
    }

    private MetaClass metaClassForProperty(PropertyTokenizer prop) {
        Class<?> propType = getGetterType(prop);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    private Class<?> getGetterType(PropertyTokenizer prop) {
        Class<?> type = reflector.getGetterType(prop.getName());
        if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
            Type returnType = getGenericGetterType(prop.getName());
            if (returnType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    returnType = actualTypeArguments[0];
                    if (returnType instanceof Class) {
                        type = (Class<?>) returnType;
                    } else if (returnType instanceof ParameterizedType) {
                        type = (Class<?>) ((ParameterizedType) returnType).getRawType();
                    }
                }
            }
        }
        return type;
    }

    private Type getGenericGetterType(String propertyName) {
        try {
            Invoker invoker = reflector.getGetInvoker(propertyName);
            if (invoker instanceof MethodInvoker) {
                Field _method = MethodInvoker.class.getDeclaredField("method");
                _method.setAccessible(true);
                Method method = (Method) _method.get(invoker);
                return TypeParameterResolver.resolveReturnType(method, reflector.getType());
            } else if (invoker instanceof GetFieldInvoker) {
                Field _field = GetFieldInvoker.class.getDeclaredField("field");
                _field.setAccessible(true);
                Field field = (Field) _field.get(invoker);
                return TypeParameterResolver.resolveFieldType(field, reflector.getType());
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    /**
     * 判断是否存在 set方法.
     *
     * @param name
     * @return
     */
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        //迭代器模式以及递归的方式,获取最后一级属性判断是否存在set方法.
        if (prop.hasNext()) {
            if (reflector.hasSetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop.getName());
                return metaProp.hasSetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasSetter(prop.getName());
        }
    }

    /**
     * 判断是否存在 get方法
     * @param name
     * @return
     */
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        //迭代器和递归的方式,获取属性表达式最后一级.
        if (prop.hasNext()) {
            if (reflector.hasGetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop);
                return metaProp.hasGetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasGetter(prop.getName());
        }
    }

    public Invoker getGetInvoker(String name) {
        return reflector.getGetInvoker(name);
    }

    public Invoker getSetInvoker(String name) {
        return reflector.getSetInvoker(name);
    }

    private StringBuilder buildProperty(String name, StringBuilder builder) {
        //注意这里是支持属性表达式的.
        PropertyTokenizer prop = new PropertyTokenizer(name);
        //这里使用的是迭代器模式.
        if (prop.hasNext()) {
            String propertyName = reflector.findPropertyName(prop.getName());
            if (propertyName != null) {
                builder.append(propertyName);
                builder.append(".");
                MetaClass metaProp = metaClassForProperty(propertyName);
                metaProp.buildProperty(prop.getChildren(), builder);
            }
        } else {
            String propertyName = reflector.findPropertyName(name);
            if (propertyName != null) {
                builder.append(propertyName);
            }
        }
        return builder;
    }

    public boolean hasDefaultConstructor() {
        return reflector.hasDefaultConstructor();
    }

}


package com.ly.zmn48644.reflection;


import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;
import com.ly.zmn48644.reflection.warpper.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class MetaObject {

    //指向原始对象, 这里有个疑问为什么不使用泛型.
    private final Object originalObject;
    //对象包装对象,简化对原始对象的操作.
    private final ObjectWrapper objectWrapper;
    //对象工厂
    private final ObjectFactory objectFactory;
    //对象包装工厂
    private final ObjectWrapperFactory objectWrapperFactory;
    //反射工厂
    private final ReflectorFactory reflectorFactory;


    /**
     * 构造方法
     *
     * @param object
     * @param objectFactory
     * @param objectWrapperFactory
     * @param reflectorFactory
     */
    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;
        this.reflectorFactory = reflectorFactory;

        if (object instanceof ObjectWrapper) {
            this.objectWrapper = (ObjectWrapper) object;
        } else if (objectWrapperFactory.hasWrapperFor(object)) {
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        } else if (object instanceof Map) {
            //如果对象是 map 类型,则创建 MapWrapper .
            this.objectWrapper = new MapWrapper(this, (Map) object);
        } else if (object instanceof Collection) {
            //如果是 collection 类型,则创建 CollectionWrapper
            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        } else {
            //上面都不是 则属于普通自定义类,创建 BeanWrapper
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    /**
     * 静态方法创建 MetaObject
     *
     * @param object
     * @param objectFactory
     * @param objectWrapperFactory
     * @param reflectorFactory
     * @return
     */
    public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        if (object == null) {
            return SystemMetaObject.NULL_META_OBJECT;
        } else {
            return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
        }
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public String findProperty(String propName, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(propName, useCamelCaseMapping);
    }

    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }


    /**
     * 通过传入的 属性表达式,返回属性值
     *
     * @param name
     * @return
     */
    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return null;
            } else {
                return metaValue.getValue(prop.getChildren());
            }
        } else {
            return objectWrapper.get(prop);
        }
    }

    /**
     * 通过传入的属性表达式,设置属性值
     *
     * @param name
     * @param value
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        //迭代器模式
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null && prop.getChildren() != null) {
                    // don't instantiate child path if value is null
                    return;
                } else {
                    metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
                }
            }
            //递归调用
            metaValue.setValue(prop.getChildren(), value);
        } else {
            //调用 objectWrapper 设置属性值
            objectWrapper.set(prop, value);
        }
    }

    public MetaObject metaObjectForProperty(String name) {
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    public boolean isCollection() {
        return objectWrapper.isCollection();
    }

    public void add(Object element) {
        objectWrapper.add(element);
    }

    public <E> void addAll(List<E> list) {
        objectWrapper.addAll(list);
    }

}

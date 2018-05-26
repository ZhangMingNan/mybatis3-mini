
package com.ly.zmn48644.reflection.warpper;


import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;

import java.util.Collection;
import java.util.List;


public class CollectionWrapper implements ObjectWrapper {

    //指向原始的集合对象
    private final Collection<Object> object;

    public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
        this.object = object;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        //集合对象没有 get 操作
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        //集合对象没有 set 操作
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void add(Object element) {
        //向集合中插入元素
        object.add(element);
    }

    @Override
    public <E> void addAll(List<E> element) {
        //向集合中插入所有元素
        object.addAll(element);
    }

}


package com.ly.zmn48644.reflection.warpper;




import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.ReflectionException;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;

import java.util.List;
import java.util.Map;


/**
 * 抽象类 实现了 ObjectWrapper 接口
 * 存在下面的两个子类,为子类提供了 三个方法.
 * MapWrapper
 * BeanWrapper
 */
public abstract class BaseWrapper implements ObjectWrapper {

    protected static final Object[] NO_ARGUMENTS = new Object[0];

    //封装了 metaObject 对象
    protected final MetaObject metaObject;

    /**
     * 构造方法 传入 metaObject 对象
     *
     * @param metaObject
     */
    protected BaseWrapper(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    /**
     * 解析属性表达式,获取属性
     *
     * @param prop
     * @param object
     * @return
     */
    protected Object resolveCollection(PropertyTokenizer prop, Object object) {
        if ("".equals(prop.getName())) {
            return object;
        } else {
            return metaObject.getValue(prop.getName());
        }
    }

    /**
     * 解析属性表达式, 获取 属性值
     *
     * @param prop
     * @param collection
     * @return
     */
    protected Object getCollectionValue(PropertyTokenizer prop, Object collection) {
        if (collection instanceof Map) {
            return ((Map) collection).get(prop.getIndex());
        } else {
            int i = Integer.parseInt(prop.getIndex());
            if (collection instanceof List) {
                return ((List) collection).get(i);
            } else if (collection instanceof Object[]) {
                return ((Object[]) collection)[i];
            } else if (collection instanceof char[]) {
                return ((char[]) collection)[i];
            } else if (collection instanceof boolean[]) {
                return ((boolean[]) collection)[i];
            } else if (collection instanceof byte[]) {
                return ((byte[]) collection)[i];
            } else if (collection instanceof double[]) {
                return ((double[]) collection)[i];
            } else if (collection instanceof float[]) {
                return ((float[]) collection)[i];
            } else if (collection instanceof int[]) {
                return ((int[]) collection)[i];
            } else if (collection instanceof long[]) {
                return ((long[]) collection)[i];
            } else if (collection instanceof short[]) {
                return ((short[]) collection)[i];
            } else {
                throw new ReflectionException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
            }
        }
    }

    /**
     * 解析属性表达式, 设置 属性值
     *
     * @param prop
     * @param collection
     * @param value
     */
    protected void setCollectionValue(PropertyTokenizer prop, Object collection, Object value) {
        if (collection instanceof Map) {
            ((Map) collection).put(prop.getIndex(), value);
        } else {
            int i = Integer.parseInt(prop.getIndex());
            if (collection instanceof List) {
                ((List) collection).set(i, value);
            } else if (collection instanceof Object[]) {
                ((Object[]) collection)[i] = value;
            } else if (collection instanceof char[]) {
                ((char[]) collection)[i] = (Character) value;
            } else if (collection instanceof boolean[]) {
                ((boolean[]) collection)[i] = (Boolean) value;
            } else if (collection instanceof byte[]) {
                ((byte[]) collection)[i] = (Byte) value;
            } else if (collection instanceof double[]) {
                ((double[]) collection)[i] = (Double) value;
            } else if (collection instanceof float[]) {
                ((float[]) collection)[i] = (Float) value;
            } else if (collection instanceof int[]) {
                ((int[]) collection)[i] = (Integer) value;
            } else if (collection instanceof long[]) {
                ((long[]) collection)[i] = (Long) value;
            } else if (collection instanceof short[]) {
                ((short[]) collection)[i] = (Short) value;
            } else {
                throw new ReflectionException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
            }
        }
    }

}
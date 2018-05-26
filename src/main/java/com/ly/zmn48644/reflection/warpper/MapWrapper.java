
package com.ly.zmn48644.reflection.warpper;


import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.SystemMetaObject;
import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * map类型对象的包装类
 */
public class MapWrapper extends BaseWrapper {

    //指向原始的 map 对象
    private final Map<String, Object> map;


    /**
     * 构造方法
     *
     * @param metaObject
     * @param map
     */
    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.map = map;
    }


    /**
     * 实现接口 get 方法
     *
     * @param prop
     * @return
     */
    @Override
    public Object get(PropertyTokenizer prop) {
        //判断 index 是否存在
        if (prop.getIndex() != null) {
            //如果存在说明是集合类型
            Object collection = resolveCollection(prop, map);
            //调用获取集合类型值得方法
            return getCollectionValue(prop, collection);
        } else {
            //否则使用 Map 得方式获取
            return map.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            setCollectionValue(prop, collection, value);
        } else {
            map.put(prop.getName(), value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (map.containsKey(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(prop.getName());
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        set(prop, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }

}

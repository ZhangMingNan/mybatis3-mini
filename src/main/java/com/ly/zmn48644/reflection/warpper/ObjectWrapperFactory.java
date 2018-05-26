
package com.ly.zmn48644.reflection.warpper;


import com.ly.zmn48644.reflection.MetaObject;

/**
 * 用于创建对象的 ObjectWrapper
 */
public interface ObjectWrapperFactory {

    /**
     * 始终返回 false
     * @param object
     * @return
     */
    boolean hasWrapperFor(Object object);

    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}

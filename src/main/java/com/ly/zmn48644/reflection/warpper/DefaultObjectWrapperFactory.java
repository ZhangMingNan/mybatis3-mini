
package com.ly.zmn48644.reflection.warpper;


import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.ReflectionException;

/**
 * ObjectWrapperFactory 默认的实现
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        //永远永远 false
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        //永远返回异常
        throw new ReflectionException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }

}

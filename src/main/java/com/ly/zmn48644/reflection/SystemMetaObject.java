
package com.ly.zmn48644.reflection;


import com.ly.zmn48644.reflection.factory.DefaultObjectFactory;
import com.ly.zmn48644.reflection.factory.ObjectFactory;
import com.ly.zmn48644.reflection.warpper.DefaultObjectWrapperFactory;
import com.ly.zmn48644.reflection.warpper.ObjectWrapperFactory;


public final class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    private SystemMetaObject() {

    }

    private static class NullObject {
    }

    /**
     * 在 UnpooledDataSourceFactory.setProperties 方法中被调用,用户获取数据源的 MetaObject.
     *
     * @param object
     * @return
     */
    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }

}

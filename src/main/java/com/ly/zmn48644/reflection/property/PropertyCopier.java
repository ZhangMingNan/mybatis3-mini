package com.ly.zmn48644.reflection.property;

import java.lang.reflect.Field;

/**
 * 对象之间的拷贝
 * 类似 beanutis.copy
 */
public final class PropertyCopier {

    private PropertyCopier() {
        // Prevent Instantiation of Static Class
    }

    public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
        Class<?> parent = type;
        //从子类向父类迭代拷贝,这种模式重复出现多次.很重要.
        while (parent != null) {
            final Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(destinationBean, field.get(sourceBean));
                } catch (Exception e) {
                    // Nothing useful to do, will only fail on final fields, which will be ignored.
                }
            }
            parent = parent.getSuperclass();
        }
    }

}

package com.ly.zmn48644.reflection.property;




import com.ly.zmn48644.reflection.ReflectionException;

import java.util.Locale;

public final class PropertyNamer {

    /**
     * 构造方法私有,不允许创建对象
     */
    private PropertyNamer() {

    }

    /**
     * 去掉 get/set/is 前缀的方法名,返回属性名
     *
     * @param name
     * @return
     */
    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    /**
     * 判断是否是属性方法
     *
     * @param name
     * @return
     */
    public static boolean isProperty(String name) {
        return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
    }

    /**
     * 是否是 get 方法
     *
     * @param name
     * @return
     */
    public static boolean isGetter(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    /**
     * 是否是 set方法
     *
     * @param name
     * @return
     */
    public static boolean isSetter(String name) {
        return name.startsWith("set");
    }

}

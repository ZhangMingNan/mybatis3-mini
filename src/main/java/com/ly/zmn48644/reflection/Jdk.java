
package com.ly.zmn48644.reflection;


import com.ly.zmn48644.io.Resources;

/**
 * To check the existence of version dependent classes.
 */
public class Jdk {

    /**
     * <code>true</code> if <code>java.lang.reflect.Parameter</code> is available.
     */
    public static final boolean parameterExists;

    static {
        boolean available = false;
        try {
            //java 8 反射模块提供了 方法参数(Parameter) 这个类,可以使用此类获取方法参数名
            Resources.classForName("java.lang.reflect.Parameter");
            available = true;
        } catch (ClassNotFoundException e) {
            // ignore
        }
        parameterExists = available;
    }

    public static final boolean dateAndTimeApiExists;

    static {
        boolean available = false;
        try {
            Resources.classForName("java.time.Clock");
            available = true;
        } catch (ClassNotFoundException e) {
            // ignore
        }
        dateAndTimeApiExists = available;
    }

    private Jdk() {
        super();
    }
}

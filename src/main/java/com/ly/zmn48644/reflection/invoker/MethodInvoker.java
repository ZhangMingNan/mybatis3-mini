
package com.ly.zmn48644.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法调用器,这里封装了,方法反射对象,以及返回值或者参数类型反射对象.
 */
public class MethodInvoker implements Invoker {

    private final Class<?> type;
    private final Method method;

    public MethodInvoker(Method method) {
        this.method = method;

        if (method.getParameterTypes().length == 1) {
            //获取方法参数类型列表
            type = method.getParameterTypes()[0];
        } else {
            type = method.getReturnType();
        }
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(target, args);
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}

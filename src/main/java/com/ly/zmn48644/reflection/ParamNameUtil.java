

package com.ly.zmn48644.reflection;




import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


public class ParamNameUtil {
    public static List<String> getParamNames(Method method) {
        return getParameterNames(method);
    }

    public static List<String> getParamNames(Constructor<?> constructor) {
        return getParameterNames(constructor);
    }

    //通过反射的方式获取方法参数的真实名字
    private static List<String> getParameterNames(Executable executable) {
        final List<String> names = new ArrayList<String>();
        final Parameter[] params = executable.getParameters();
        for (Parameter param : params) {
            names.add(param.getName());
        }
        return names;
    }

    //这里把构造方法私有了,但是并不是单例模式
    //其他方法全部是静态方法.
    //仅仅是避免创建此类的对象
    private ParamNameUtil() {
        super();
    }
}

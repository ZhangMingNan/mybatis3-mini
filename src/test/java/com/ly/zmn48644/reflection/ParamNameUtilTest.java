package com.ly.zmn48644.reflection;

import com.google.common.base.Joiner;
import com.ly.zmn48644.model.Author;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class ParamNameUtilTest {

    /**
     * 获取普通方法的参数名列表
     *
     * @throws NoSuchMethodException
     */
    @Test
    public void methodParamName() throws NoSuchMethodException {
        Method method = Author.class.getDeclaredMethod("setId", Integer.class);
        //获取方法的参数名
        List<String> paramNames = ParamNameUtil.getParamNames(method);
        for (String paramName : paramNames) {
            System.out.println(paramName);
        }
    }

    /**
     * 获取构造方法的的参数名列表
     */
    @Test
    public void constructorParamName() {
        for (Constructor<?> constructor : Author.class.getConstructors()) {
            List<String> paramNames = ParamNameUtil.getParamNames(constructor);
            System.out.println(Joiner.on(",").join(paramNames));
        }
    }

}

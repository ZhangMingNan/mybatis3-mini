

package com.ly.zmn48644.reflection;


import com.ly.zmn48644.annotations.Param;
import com.ly.zmn48644.binding.ParamMap;
import com.ly.zmn48644.session.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 方法参数名解析器
 *
 */
public class ParamNameResolver {

    //GENERIC NAME PREFIX
    //通用名称前缀
    private static final String GENERIC_NAME_PREFIX = "param";

    private final SortedMap<Integer, String> names;

    private boolean hasParamAnnotation;

    public ParamNameResolver(Configuration config, Method method) {
        //获取方法参数上的所有注解
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<Integer, String>();
        //使用注解标注的参数的数量
        int paramCount = paramAnnotations.length;
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            String name = null;
            //由于 方法参数上的注解可以添加多个 因此 这里要使用 循环, 查找出参数上的所有注解
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                //如果注解类型是 Param
                if (annotation instanceof Param) {
                    //标记设置为true
                    hasParamAnnotation = true;
                    //获取注解的值, 用作参数名
                    name = ((Param) annotation).value();
                    break;
                }
            }
            //如果通过注解没有获取到
            if (name == null) {
                //如果配置中设置了使用真实的 方法参数名.
                if (config.isUseActualParamName()) {
                    //获取方法参数 的真实名字, 此方法需要 java 8 支持
                    name = getActualParamName(method, paramIndex);
                }
                if (name == null) {
                    //如果依然无法获取到方法参数的真实名称
                    //使用参数在方法上的位置
                    name = String.valueOf(map.size());
                }
            }
            //将解析出来的参数名, 以及参数索引, 放入 map 中
            map.put(paramIndex, name);
        }
        //包装成不可变有序集合.
        names = Collections.unmodifiableSortedMap(map);
    }

    /**
     * 获取方法真实的参数名称
     *
     * @param method
     * @param paramIndex
     * @return
     */
    private String getActualParamName(Method method, int paramIndex) {
        if (Jdk.parameterExists) {
            return ParamNameUtil.getParamNames(method).get(paramIndex);
        }
        return null;
    }


    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    /**
     * 通过反射拿到的方法调用的参数 是 Object[] args .
     * 需要分情况将 args 中的 参数 转换为 map,或者单个对象.
     * @param args
     * @return
     */
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            //接口方法无参数的情况
            return null;
        } else if (!hasParamAnnotation && paramCount == 1) {
            //接口方法只有一个参数的情况, 直接返回 参数值本身
            return args[names.firstKey()];
        } else {
            //接口方法有多个参数的情况下,使用 param 来保存
            //注意这里使用的是一个自定义的 ParamMap 类型
            final Map<String, Object> param = new ParamMap<>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                //将 参数名 和 参数值 放入 map中去.
                param.put(entry.getValue(), args[entry.getKey()]);
                //添加通用参数名到这个map 中.(param1, param2, ...)
                final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
                // ensure not to overwrite parameter named with @Param
                //这里要判断一下, 防止 覆盖掉注解中的 参数名, 有可能也叫(param1, param2, ...)
                if (!names.containsValue(genericParamName)) {
                    param.put(genericParamName, args[entry.getKey()]);
                }
                i++;
            }
            return param;
        }
    }
}

package com.ly.zmn48644.type;


import com.ly.zmn48644.io.ResolverUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 类型别名注册中心
 */
public class TypeAliasRegistry {

    Map<String, Class<?>> TYPE_ALIAS = new HashMap<>();


    public TypeAliasRegistry() {
        //注册一些默认的别名

    }

    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new RuntimeException("别名不能为空!");
        }
        //判断添加的别名是否已经注册过,防止后注册的别名覆盖系统默认的别名
        if (TYPE_ALIAS.containsKey(alias)) {
            //如果已经存在
            throw new RuntimeException("此别名已经被注册过了!");
        }
        //将别名转换成小写
        String key = alias.toLowerCase(Locale.ENGLISH);
        TYPE_ALIAS.put(key, value);
    }


    /**
     * 传入别名返回,别名对应的类型
     *
     * @param alias
     * @param <T>
     * @return
     */
    public <T> Class<T> resolveAlias(String alias) {
        if (alias == null) {
            return null;
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        Class<T> value = null;
        if (TYPE_ALIAS.containsKey(key)) {
            value = (Class<T>) TYPE_ALIAS.get(key);
        }
        return value;
    }

    /**
     * 指定包名
     * @param packageName
     */
    public void registerAliases(String packageName) {
        registerAliases(packageName, Object.class);
    }

    /**
     * 指定包名 和 限定父类
     *
     * @param packageName
     */
    public void registerAliases(String packageName, Class<?> superType) {
        //调用基础工具类
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        for (Class<?> type : typeSet) {
            if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                registerAlias(type.getSimpleName(), type);
            }
        }
    }
}

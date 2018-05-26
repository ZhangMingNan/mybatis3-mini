package com.ly.zmn48644.binding;


import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {


    /**
     * 在这里创建 MapperProxy 实例并不需要缓存,原因在于构建 class 和 MapperProxyFactory的映射是在工程启动解析
     * xml配置文件的时候进行的,真正项目运行的时候是不会调用的.
     *
     * @param proxy
     * @param mapperInterface
     * @param <T>
     * @return
     */
    public <T> T newInstance(MapperProxy proxy, Class<T>[] mapperInterface) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), mapperInterface, proxy);
    }
}

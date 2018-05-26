
package com.ly.zmn48644.io;

import java.io.InputStream;
import java.net.URL;

/**
 * 一个类中包装多个类加载器让他们一起工作
 */
public class ClassLoaderWrapper {

    ClassLoader defaultClassLoader;
    ClassLoader systemClassLoader;

    ClassLoaderWrapper() {
        try {
            //构造方法中初始化系统类加载器
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
            // AccessControlException on Google App Engine
        }
    }

    /*
     * 获取资源的URL根据资源名
     *
     * @param resource - 资源位置
     * @return 返回资源或者null
     */
    public URL getResourceAsURL(String resource) {
        return getResourceAsURL(resource, getClassLoaders(null));
    }

    /*
     * 获取资源的URL根据资源名和指定的类加载器
     *
     * @param resource    - 资源位置
     * @param classLoader - 第一个尝试查找的类加载器.
     * @return 返回资源或者null
     */
    public URL getResourceAsURL(String resource, ClassLoader classLoader) {
        return getResourceAsURL(resource, getClassLoaders(classLoader));
    }

    /*
     * 获取资源的输入流
     *
     * @param resource - 查找的资源
     * @return 返回流或者null
     */
    public InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    /*
     * 使用指定的类加载器加载资源返回资源的输入流
     *
     * @param resource    - 查找的资源
     * @param classLoader - 第一个尝试查找的类加载器
     * @return 返回流或null
     */
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /*
     * 在当前类路径下查找类
     *
     * @param name - 要查找的类名称
     * @return - 找到的类
     * @throws ClassNotFoundException 废话.
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    /*
     * 查找类从当前类路径
     * @param name 类的名称
     * @param classLoader 指定的类加载器
     * @return - the class 加载到的类
     * @throws ClassNotFoundException Duh.
     */
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    /*
     * 试着从一组类加载器中获取获取资源,返回输入流.
     *
     * @param 需要获取的资源
     * @param 一组类加载器
     * @return 返回找到的资源或者null.
     */
    InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {

                // 试着获取这个资源.
                InputStream returnValue = cl.getResourceAsStream(resource);

                // 这里有些类加载器需要加上"/"前缀.
                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }
                //如果加载的资源
                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /*
     * 试着从一组类加载器中获取获取资源,返回URL
     *
     * @param resource    - 资源的位置也就是URL
     * @param classLoader - 一组类加载器
     * @return 返回这个资源或者null
     */
    URL getResourceAsURL(String resource, ClassLoader[] classLoader) {
        URL url;
        for (ClassLoader cl : classLoader) {

            if (null != cl) {
                url = cl.getResource(resource);
                if (null == url) {
                    url = cl.getResource("/" + resource);
                }
                if (null != url) {
                    return url;
                }
            }
        }
        //没有找到任何资源
        return null;

    }

    /*
     * 试图通过一组类加载器加载一个类
     *
     * @param name        类的名字
     * @param classLoader 一组类加载器
     * @return 加载到的class对象
     * @throws ClassNotFoundException - Remember the wisdom of Judge Smails: Well, the world needs ditch diggers, too.
     */
    Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    Class<?> c = Class.forName(name, true, cl);
                    if (null != c) {
                        return c;
                    }
                } catch (ClassNotFoundException e) {
                    //这里忽略某个类加载器的异常,最后统一抛出类加载异常.
                }
            }
        }
        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    /**
     * 一组类加载器
     *
     * @param classLoader
     * @return
     */
    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,//传入的自定义的类加载器(可能为空)
                defaultClassLoader,//系统默认的类加载器(可能为空)
                Thread.currentThread().getContextClassLoader(),//当前线程的类加载器
                getClass().getClassLoader(),//当前类的类加载器
                systemClassLoader};//系统类加载器
    }

}

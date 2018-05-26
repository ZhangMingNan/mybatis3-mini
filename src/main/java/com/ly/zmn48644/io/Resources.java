
package com.ly.zmn48644.io;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 通过classloader简化资源访问的一个工具类
 * @author Clinton Begin
 */
public class Resources {

    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    /*
     * 调用 getResourceAsReader 时使用的字符集,如果为null 意味着使用系统默认的.
     */
    private static Charset charset;

    Resources() {
    }

    /*
     * 返回默认的类加载器,有可能为null
     * 
     * @return 默认的类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.defaultClassLoader;
    }

    /*
     * 设置默认的类加载器
     * @param defaultClassLoader - the new default ClassLoader
     */
    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.defaultClassLoader = defaultClassLoader;
    }

    /*
     * 返回资源在类路径下的URL
     * 
     * @param 需要查找的资源名称
     * @return 返回找到的资源
     * @throws java.io.IOException 如果资源没有找到或者不可读,抛出异常!
     */
    public static URL getResourceURL(String resource) throws IOException {
        // issue #625
        return getResourceURL(null, resource);
    }

    /*
     * 返回资源在类路径下的URL
     *
     * @param 使用传入的loader获取这个资源
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException 如果资源没有找到或者不可读,抛出异常!
     */
    public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /*
     * 返回资源在类路径下的流对象
     *
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException  如果资源没有找到或者不可读,抛出异常!
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    /*
     * 返回资源在类路径下的流对象
     *
     * @param  使用传入的loader获取这个资源
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException (如果资源没有找到或者不可读,抛出异常!
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /*
     * 返回资源在类路径下的Properties对象
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException 如果资源没有找到或者不可读,抛出异常!
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * 返回资源在类路径下的Properties对象
     *
     * @param  使用指定类加载器加载资源
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException 
     */
    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(loader, resource);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * 返回资源在类路径下的Reader对象
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException 
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(resource), charset);
        }
        return reader;
    }

    /*
     * 返回资源在类路径下的Reader对象
     *
     * @param  使用指定的类加载器加载资源
     * @param 需要查找的资源名称
     * @return 通过类加载器找到的资源
     * @throws java.io.IOException 
     */
    public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(loader, resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
        }
        return reader;
    }

    /*
     * 返回类路径下的一个资源为一个File对象
     *
     * @param 需要查找的资源名称
     * @return 返回这个资源
     * @throws java.io.IOException 如果没有找到或者不可读则抛出异常
     */
    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    /*
     * 返回类路径下的一个资源为一个File对象
     *
     * @param 指定一个类加载器
     * @param resource - 需要查找的资源名称
     * @return 类路径下找到的资源
     * @throws java.io.IOException 如果没有找到或者不可读则抛出异常
     */
    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceURL(loader, resource).getFile());
    }

    /*
     * 获取的一个URL所指向的输入流
     *
     * @param 指定的URL
     * @return 从指定URL打开的输入流
     * @throws java.io.IOException 抛出的IO异常
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    /*
     * 获取的一个URL所指向的reader对象
     *
     * @param 指定的URL
     * @return 从指定URL打开的输入流
     * @throws java.io.IOException 抛出的IO异常
     */
    public static Reader getUrlAsReader(String urlString) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }
        return reader;
    }

    /*
     * 获取URL所指向的Properties对象
     *
     * @param 指定的URL
     * @return 一个URL所指向Properties
     * @throws java.io.IOException 抛出的IO异常
     */
    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        InputStream in = getUrlAsStream(urlString);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * 加载一个类文件
     *
     * 此方法在 XMLConfigBuilder.loadCustomVfs 中被调用,用于 加载 自定义虚拟文件系统实现类.
     * 此方法在 XMLConfigBuilder.typeAliasesElement  中被调用,用于加载 别名配置中的类.
     *
     * @param 要加载的类名
     * @return 加载到的类对象
     * @throws ClassNotFoundException 如果类没有找到抛出没有找到类异常
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }

    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }

}

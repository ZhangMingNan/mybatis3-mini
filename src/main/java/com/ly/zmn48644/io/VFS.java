package com.ly.zmn48644.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 提供一个简化的API用来访问应用服务器资源.
 * Virtual File System(虚拟文件系统)
 * Apache 有个 http://commons.apache.org/proper/commons-vfs/  是功能更加强大的封装.
 */
public abstract class VFS {


    /**
     * The built-in(内置) implementations.
     * 在抽象类中内置一些具体实现,这里内置了两个内置实现.
     */
    public static final Class<?>[] IMPLEMENTATIONS = {JBoss6VFS.class, DefaultVFS.class};

    /**
     * The list to which implementations are added by {@link #addImplClass(Class)}.
     * 除了内置的实现,可以以添加自定义的实现到此集合中.
     */
    public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<Class<? extends VFS>>();

    /**
     * Singleton instance holder.
     * 这里使用的是静态内部类的方式
     */
    private static class VFSHolder {
        //指向单例对象`
        static final VFS INSTANCE = createVFS();

        @SuppressWarnings("unchecked")
        static VFS createVFS() {
            // Try the user implementations first, then the built-ins
            // 这里将自定义实现集合加在最前面,首先尝试自定义实现,其次是内置实现.
            List<Class<? extends VFS>> impls = new ArrayList<Class<? extends VFS>>();
            //添加自定义实现
            impls.addAll(USER_IMPLEMENTATIONS);
            //添加内置实现
            impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));

            // Try each implementation class until a valid one is found
            // 尝试每一个实现类一直到有一个可用的实现被找到.
            VFS vfs = null;
            // 不可用 继续循环
            for (int i = 0; vfs == null || !vfs.isValid(); i++) {

                Class<? extends VFS> impl = impls.get(i);
                try {
                    //创建实现对象
                    vfs = impl.newInstance();
                    if (vfs == null || !vfs.isValid()) {
                        //如果当前循环中的实现不可用,打印debug级别的日志
//                        if (log.isDebugEnabled()) {
//                            log.debug("VFS implementation " + impl.getName() +
//                                    " is not valid in this environment.");
//                        }
                    }
                } catch (InstantiationException e) {
                    //创建实现对象报错打印error日志
//                    log.error("Failed to instantiate " + impl, e);
                    //这里并没有抛出异常,只是返回了 null
                    return null;
                } catch (IllegalAccessException e) {
                    //创建实现对象报错打印error日志
                    //log.error("Failed to instantiate " + impl, e);
                    return null;
                }
            }

//            if (log.isDebugEnabled()) {
//                //正常找到可用的打印debug模式日志
//                log.debug("Using VFS adapter " + vfs.getClass().getName());
//            }

            return vfs;
        }
    }

    /**
     * Get the singleton {@link VFS} instance. If no {@link VFS} implementation can be found for the
     * current environment, then this method returns null.
     * 返回单例实现如果被找到从当前环境中, 如果没找到返回 null.
     */
    public static VFS getInstance() {
        return VFSHolder.INSTANCE;
    }

    /**
     * Adds the specified class to the list of {@link VFS} implementations. Classes added in this
     * manner are tried in the order they are added and before any of the built-in implementations.
     * <p>
     * 添加指定类到vfs实现集合中,这里添加进去的实现优先级会在内置实现之前
     *
     * @param clazz The {@link VFS} implementation class to add.
     */
    public static void addImplClass(Class<? extends VFS> clazz) {
        if (clazz != null) {
            USER_IMPLEMENTATIONS.add(clazz);
        }
    }

    /**
     * Get a class by name. If the class is not found then return null.
     * 传入一个类名,如果根据类名能找到则返回否则返回null.
     */
    protected static Class<?> getClass(String className) {
        try {
            //使用当前线程的类加载器加载
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            //这里的长全部, 被用 debug级别 日志输出
//            if (log.isDebugEnabled()) {
//                log.debug("Class not found: " + className);
//            }
            return null;
        }
    }

    /**
     * Get a method by name and parameter types. If the method is not found then return null.
     * <p>
     * 获取一个方法对象根据名称和参数, 如果没找到返回null
     *
     * @param clazz          The class to which the method belongs.
     * @param methodName     The name of the method.
     * @param parameterTypes The types of the parameters accepted by the method.
     */
    protected static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            //log.error("Security exception looking for method " + clazz.getName() + "." + methodName + ".  Cause: " + e);
            return null;
        } catch (NoSuchMethodException e) {
            //log.error("Method not found " + clazz.getName() + "." + methodName + "." + methodName + ".  Cause: " + e);
            return null;
        }
    }

    /**
     * Invoke a method on an object and return whatever it returns.
     * <p>
     * 通过反射的方式调用方法
     *
     * @param method     The method to invoke.
     * @param object     The instance or class (for static methods) on which to invoke the method.
     * @param parameters The parameters to pass to the method.
     * @return Whatever the method returns.
     * @throws IOException      If I/O errors occur
     * @throws RuntimeException If anything else goes wrong
     */
    @SuppressWarnings("unchecked")
    protected static <T> T invoke(Method method, Object object, Object... parameters)
            throws IOException, RuntimeException {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalArgumentException e) {
            //抛出 运行时异常
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            //抛出 运行时异常
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收,InvocationTargetException.
            //如果反射调用内部抛出的异常时 IO异常则,抛出被调用者抛出的异常.
            if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            } else {
                //如果反射调用内部,抛出的不是IO异常则,抛出运行时异常.
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 使用当前线程加载器,返回资源列表
     *
     * @param path The resource path.
     * @return A list of {@link URL}s, as returned by {@link ClassLoader#getResources(String)}.
     * @throws IOException If I/O errors occur
     */
    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    /**
     * Return true if the {@link VFS} implementation is valid for the current environment.
     * <p>
     * 抽象方法,交给子类来实现, 判断当前环境子类是否可用.
     */
    public abstract boolean isValid();

    /**
     * Recursively list the full resource path of all the resources that are children of the
     * resource identified by a URL.
     * <p>
     * 抽象方法, 负责查找指定的资源名称列表,这里会使用递归的方式列出所有的资源列表.
     *
     * @param url     The URL that identifies the resource to list.
     * @param forPath The path to the resource that is identified by the URL. Generally, this is the
     *                value passed to {@link #getResources(String)} to get the resource URL.
     * @return A list containing the names of the child resources.
     * @throws IOException If I/O errors occur
     */
    protected abstract List<String> list(URL url, String forPath) throws IOException;

    /**
     * Recursively list the full resource path of all the resources that are children of all the
     * resources found at the specified path.
     * <p>
     * 递归的方式列出指定path下所有的资源列表
     *
     * @param path The path of the resource(s) to list.
     * @return A list containing the names of the child resources.
     * @throws IOException If I/O errors occur
     */
    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }
}

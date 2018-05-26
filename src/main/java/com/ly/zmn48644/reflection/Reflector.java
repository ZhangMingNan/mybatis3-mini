package com.ly.zmn48644.reflection;

import com.ly.zmn48644.reflection.invoker.GetFieldInvoker;
import com.ly.zmn48644.reflection.invoker.Invoker;
import com.ly.zmn48644.reflection.invoker.MethodInvoker;
import com.ly.zmn48644.reflection.invoker.SetFieldInvoker;
import com.ly.zmn48644.reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 某一个类的反射器,mybatis反射工具包的核心类
 */
public class Reflector {

    //这个类的class对象
    private final Class<?> type;
    //读属性名列表
    private final String[] readablePropertyNames;
    //写属性名列表
    private final String[] writeablePropertyNames;
    //字段名 和 set方法调用器 之间的映射
    private final Map<String, Invoker> setMethods = new HashMap<String, Invoker>();
    //字段名 和 get方法调用器 之间的映射
    private final Map<String, Invoker> getMethods = new HashMap<String, Invoker>();
    //字段名 和 set方法参数的映射
    private final Map<String, Class<?>> setTypes = new HashMap<String, Class<?>>();
    //字段名 和 get方法参数的映射
    private final Map<String, Class<?>> getTypes = new HashMap<String, Class<?>>();
    //这个类的构造方法实例
    private Constructor<?> defaultConstructor;

    //不区分大小写字段 和 属性的映射
    private Map<String, String> caseInsensitivePropertyMap = new HashMap<String, String>();

    /**
     * 反射器构造方法
     *
     * @param clazz
     */
    public Reflector(Class<?> clazz) {
        //赋值 type 属性 为 传入的class对象
        type = clazz;
        //获取并赋值默认无参构造方法
        addDefaultConstructor(clazz);
        //获取并赋值所有get方法的 字段 和 调用器映射
        addGetMethods(clazz);
        //获取并赋值所有set方法的 字段 和 调用器映射
        addSetMethods(clazz);
        //setMethods,getMethods和setTypes,getTypes 的获取和赋值
        addFields(clazz);
        //获取get方法属性 字段名列表
        readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
        //获取set方法属性 字段名列表
        writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);

        //向caseInsensitivePropertyMap中加入 以忽略大小写属性名为key,真实属性名为value的键值对.
        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writeablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    /**
     * 获取类对象的默认无参构造方法赋值给defaultConstructor.
     *
     * @param clazz
     */
    private void addDefaultConstructor(Class<?> clazz) {
        //获取所有的公开构造方法
        Constructor<?>[] consts = clazz.getDeclaredConstructors();
        //遍历所有的构造方法
        for (Constructor<?> constructor : consts) {
            //如果构造方法参数数量为0
            if (constructor.getParameterTypes().length == 0) {
                //判断系统权限判断是否能够访问私有方法
                if (canAccessPrivateMethods()) {
                    try {
                        //设置不进行访问检查,调用效率会提高
                        constructor.setAccessible(true);
                    } catch (Exception e) {
                        // Ignored. This is only a final precaution, nothing we can do.
                    }
                }
                //判断是否能访问
                if (constructor.isAccessible()) {
                    //找到的无参构造方法 赋值给 defaultConstructor.
                    this.defaultConstructor = constructor;
                }
            }
        }
    }

    /**
     * 获取和赋值 get方法 字段和调用器之间的映射关系
     *
     * @param cls
     */
    private void addGetMethods(Class<?> cls) {
        //冲突的方法列表
        Map<String, List<Method>> conflictingGetters = new HashMap<String, List<Method>>();

        //获取cls中所有的方法, 包含其父类中的私有非私有方法.
        Method[] methods = getClassMethods(cls);
        for (Method method : methods) {
            //如果参数大于0 说明不可能是get方法,跳出循环.
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            //获取方法名
            String name = method.getName();
            //判断 是 get前缀还是 is前缀
            if ((name.startsWith("get") && name.length() > 3)
                    || (name.startsWith("is") && name.length() > 2)) {
                //将方法名处理为字段名
                name = PropertyNamer.methodToProperty(name);
                //处理冲突方法,?什么样方法算是冲突方法.
                addMethodConflict(conflictingGetters, name, method);
            }
        }
        resolveGetterConflicts(conflictingGetters);
    }

    /**
     * 解决get冲突方法
     *
     * @param conflictingGetters
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
            //优先方法
            Method winner = null;
            //属性名称
            String propName = entry.getKey();
            //候选者candidate
            for (Method candidate : entry.getValue()) {
                //第一次进入当选者为null
                if (winner == null) {
                    //将第一个候选者赋值给当选者引用
                    winner = candidate;
                    //跳出循环
                    continue;
                }
                //获取当选者 返回值类型
                Class<?> winnerType = winner.getReturnType();
                //获取候选者 返回值类型
                Class<?> candidateType = candidate.getReturnType();
                //判断 当选者 和候选者 返回类型是否相同
                if (candidateType.equals(winnerType)) {
                    //如果相同
                    if (!boolean.class.equals(candidateType)) {
                        throw new ReflectionException(
                                "Illegal overloaded getter method with ambiguous type for property "
                                        + propName + " in class " + winner.getDeclaringClass()
                                        + ". This breaks the JavaBeans specification and can cause unpredictable results.");
                    } else if (candidate.getName().startsWith("is")) {
                        winner = candidate;
                    }
                } else if (candidateType.isAssignableFrom(winnerType)) {
                    // 保留当前 当选者方法, winnerType 是 candidateType的父类
                } else if (winnerType.isAssignableFrom(candidateType)) {
                    // 替换当选者方法, candidateType 是 winnerType 的父类
                    winner = candidate;
                } else {
                    //否则抛出异常
                    throw new ReflectionException(
                            "Illegal overloaded getter method with ambiguous type for property "
                                    + propName + " in class " + winner.getDeclaringClass()
                                    + ". This breaks the JavaBeans specification and can cause unpredictable results.");
                }
            }
            //将传入的方法 放到 getMethods 和 getTypes中.
            addGetMethod(propName, winner);
        }
    }

    /**
     * 将传入的方法 放到 getMethods 和 getTypes中.
     *
     * @param name
     * @param method
     */
    private void addGetMethod(String name, Method method) {
        //排除一些非 属性字段
        if (isValidPropertyName(name)) {
            getMethods.put(name, new MethodInvoker(method));
            //解析出返回值类型
            Type returnType = TypeParameterResolver.resolveReturnType(method, type);
            //将返回值类型转换成 class 放入getTypes数组中
            getTypes.put(name, typeToClass(returnType));
        }
    }

    /**
     * 添加set方法 属性名和方法的映射
     *
     * @param cls
     */
    private void addSetMethods(Class<?> cls) {
        Map<String, List<Method>> conflictingSetters = new HashMap<String, List<Method>>();
        Method[] methods = getClassMethods(cls);
        for (Method method : methods) {
            String name = method.getName();
            //开头必须是set
            if (name.startsWith("set") && name.length() > 3) {
                //必须是只有一个参数
                if (method.getParameterTypes().length == 1) {
                    //方法名转属性名
                    name = PropertyNamer.methodToProperty(name);
                    //
                    addMethodConflict(conflictingSetters, name, method);
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);
    }

    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        List<Method> list = conflictingMethods.get(name);
        if (list == null) {
            list = new ArrayList<Method>();
            conflictingMethods.put(name, list);
        }
        list.add(method);
    }

    /**
     * 解决set方法冲突
     *
     * @param conflictingSetters
     */
    private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (String propName : conflictingSetters.keySet()) {
            List<Method> setters = conflictingSetters.get(propName);

            Class<?> getterType = getTypes.get(propName);
            Method match = null;
            ReflectionException exception = null;
            for (Method setter : setters) {
                //获取set方法第一个参数
                Class<?> paramType = setter.getParameterTypes()[0];
                if (paramType.equals(getterType)) {
                    //如果 set方法 和 get方法的参数类型相同
                    //找到匹配的setter方法
                    match = setter;
                    break;
                }
                if (exception == null) {
                    try {
                        match = pickBetterSetter(match, setter, propName);
                    } catch (ReflectionException e) {
                        // there could still be the 'best match'
                        match = null;
                        exception = e;
                    }
                }
            }
            if (match == null) {
                throw exception;
            } else {
                addSetMethod(propName, match);
            }
        }
    }

    private Method pickBetterSetter(Method setter1, Method setter2, String property) {
        if (setter1 == null) {
            return setter2;
        }
        Class<?> paramType1 = setter1.getParameterTypes()[0];
        Class<?> paramType2 = setter2.getParameterTypes()[0];
        if (paramType1.isAssignableFrom(paramType2)) {
            return setter2;
        } else if (paramType2.isAssignableFrom(paramType1)) {
            return setter1;
        }
        throw new ReflectionException("Ambiguous setters defined for property '" + property + "' in class '"
                + setter2.getDeclaringClass() + "' with types '" + paramType1.getName() + "' and '"
                + paramType2.getName() + "'.");
    }

    private void addSetMethod(String name, Method method) {
        if (isValidPropertyName(name)) {
            setMethods.put(name, new MethodInvoker(method));
            Type[] paramTypes = TypeParameterResolver.resolveParamTypes(method, type);
            setTypes.put(name, typeToClass(paramTypes[0]));
        }
    }

    /**
     * 将 type 转换成 Class 返回
     *
     * @param src
     * @return
     */
    private Class<?> typeToClass(Type src) {
        Class<?> result = null;
        if (src instanceof Class) {
            result = (Class<?>) src;
        } else if (src instanceof ParameterizedType) {
            result = (Class<?>) ((ParameterizedType) src).getRawType();
        } else if (src instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) src).getGenericComponentType();
            if (componentType instanceof Class) {
                result = Array.newInstance((Class<?>) componentType, 0).getClass();
            } else {
                Class<?> componentClass = typeToClass(componentType);
                result = Array.newInstance((Class<?>) componentClass, 0).getClass();
            }
        }
        if (result == null) {
            result = Object.class;
        }
        return result;
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //首先要判断系统的检查权限.
            if (canAccessPrivateMethods()) {
                try {
                    //设置访问安全检查,可以提高反射调用的性能.
                    field.setAccessible(true);
                } catch (Exception e) {
                    //忽略此处的异常处理, 这是最后的防御,我无能为力.
                    // Ignored. This is only a final precaution, nothing we can do.
                }
            }
            if (field.isAccessible()) {
                if (!setMethods.containsKey(field.getName())) {
                    // issue #379 - removed the check for final because JDK 1.5 allows
                    // modification of final fields through reflection (JSR-133). (JGB)
                    // pr #16 - final static can only be set by the classloader
                    int modifiers = field.getModifiers();
                    if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                        addSetField(field);
                    }
                }
                if (!getMethods.containsKey(field.getName())) {
                    addGetField(field);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMethods.put(field.getName(), new SetFieldInvoker(field));
            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            setTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMethods.put(field.getName(), new GetFieldInvoker(field));
            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            getTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    /**
     * 判断是否是合法,排除一些特殊的方法
     *
     * @param name
     * @return
     */
    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    /*
     * 此方法返回包含所有方法的数组,从class中以及其父类中.
     * 不适用 Class.getMethods()的原因是想获取私有方法.
     * @param cls The class
     * @return 包含所有方法的数组
     */
    private Method[] getClassMethods(Class<?> cls) {
        //key为方法签名
        Map<String, Method> uniqueMethods = new HashMap<String, Method>();
        //循环中当前class
        Class<?> currentClass = cls;
        while (currentClass != null) {
            //获取当前类所有的方法,添加到  uniqueMethods 中去.
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
            //如果是抽象类的话需要查看其接口
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            //将当前类 指向次循环中 currentClass的父类,实现类似递归的作用
            currentClass = currentClass.getSuperclass();
        }
        //从map中取到所有方法集合
        Collection<Method> methods = uniqueMethods.values();
        //转换成数组返回
        return methods.toArray(new Method[methods.size()]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            //判断是否是桥接方法,如果不是桥接方法进入
            if (!currentMethod.isBridge()) {
                //获取方法唯一签名
                String signature = getSignature(currentMethod);
                //检查这个方法是否已经存在.
                //如果已经存在,覆盖此方法.
                if (!uniqueMethods.containsKey(signature)) {
                    if (canAccessPrivateMethods()) {
                        try {
                            //设置此方法为可访问
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                            // Ignored. This is only a final precaution, nothing we can do.
                        }
                    }
                    //key为方法签名
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    /**
     * 获取方法签名
     *
     * @param method
     * @return
     */
    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            sb.append(returnType.getName()).append('#');
        }
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    /**
     * 检查操作系统是否允许访问私有方法
     *
     * @return
     */
    private static boolean canAccessPrivateMethods() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    /*
     * Gets the name of the class the instance provides information for
     *
     * @return The class name
     */
    public Class<?> getType() {
        return type;
    }

    public Constructor<?> getDefaultConstructor() {
        if (defaultConstructor != null) {
            return defaultConstructor;
        } else {
            throw new ReflectionException("There is no default constructor for " + type);
        }
    }

    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = setMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = getMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    /*
     * Gets the type for a property setter
     *
     * @param propertyName - the name of the property
     * @return The Class of the propery setter
     */
    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /*
     * Gets the type for a property getter
     *
     * @param propertyName - the name of the property
     * @return The Class of the propery getter
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> clazz = getTypes.get(propertyName);
        if (clazz == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /*
     * Gets an array of the readable properties for an object
     *
     * @return The array
     */
    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    /*
     * Gets an array of the writeable properties for an object
     *
     * @return The array
     */
    public String[] getSetablePropertyNames() {
        return writeablePropertyNames;
    }

    /*
     * Check to see if a class has a writeable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a writeable property by the name
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.keySet().contains(propertyName);
    }

    /*
     * Check to see if a class has a readable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a readable property by the name
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.keySet().contains(propertyName);
    }

    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }
}

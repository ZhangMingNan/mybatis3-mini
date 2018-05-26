
package com.ly.zmn48644.datasource.unpooled;


import com.ly.zmn48644.datasource.DataSourceFactory;
import com.ly.zmn48644.reflection.MetaObject;
import com.ly.zmn48644.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 非池化数据源工厂类
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

    //驱动前缀
    private static final String DRIVER_PROPERTY_PREFIX = "driver.";
    //驱动前缀长度
    private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

    //工厂类持有的数据源对象
    protected DataSource dataSource;


    /**
     * 工厂类构造方法
     */
    public UnpooledDataSourceFactory() {

        //工厂类的构造方法就会初始化一个数据源
        this.dataSource = new UnpooledDataSource();
    }


    /**
     * 设置相关数据源配置
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

        Properties driverProperties = new Properties();

        //获取dataSource对象的MetaObject对象.
        MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);

        //遍历所有配置项
        for (Object key : properties.keySet()) {
            //获取当前循环到的配置项的 key
            String propertyName = (String) key;

            //如果是 驱动 配置,以是以 driver. 为前缀进行判断,则放入 driverProperties 中
            //比如下面的配置,下面的三个以前是写在url 后面的,既然提供了这种方式,就要按照这种官方建议的方式配置
//          <dataSource type="UNPOOLED">
            //.....
//                <property name="driver.allowMultiQueries" value="true"/>
//                <property name="driver.useUnicode" value="true"/>
//                <property name="driver.characterEncoding" value="UTF-8"/>
//            </dataSource>
            if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
                //获取配置值
                String value = properties.getProperty(propertyName);

                driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
            } else if (metaDataSource.hasSetter(propertyName)) {
                //如果数据源实现对象有存在配置中的 key 的set方法.则 通过反射方式设置 数据源的 配置.
                //在这里将配置中的数据 设置到真正的数据源对象中去.
                String value = (String) properties.get(propertyName);
                //转换配置的数据类型
                Object convertedValue = convertValue(metaDataSource, propertyName, value);
                //通过反射的方式把配置值设置给 数据源对象.
                metaDataSource.setValue(propertyName, convertedValue);
            } else {
                //如果配置了 数据源实现类中没有的属性,也就是没有 set 方法则抛出异常.
                throw new RuntimeException("Unknown DataSource property: " + propertyName);
            }
        }

        //如果 driverProperties  存在配置项
        if (driverProperties.size() > 0) {
            //设置 数据源对象的  driverProperties 属性
            metaDataSource.setValue("driverProperties", driverProperties);
        }
    }


    /**
     * 外部调用获取构造好的数据源
     *
     * @return
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }


    /**
     * 使用 MetaObject 转换,配置中的value类型
     *
     * @param metaDataSource
     * @param propertyName
     * @param value
     * @return
     */
    private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaDataSource.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }
        return convertedValue;
    }

}

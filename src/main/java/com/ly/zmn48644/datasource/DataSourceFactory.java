
package com.ly.zmn48644.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源工厂接口
 */
public interface DataSourceFactory {

    //设置配置,一般紧跟在初始化完成之后
    void setProperties(Properties props);

    //获取数据源对象
    DataSource getDataSource();

}

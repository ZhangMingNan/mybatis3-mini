package com.ly.zmn48644.session;


import com.ly.zmn48644.build.XMLConfigBuilder;
import com.ly.zmn48644.session.defaults.DefaultSqlSessionFactory;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {


    public static SqlSessionFactory build(InputStream inputStream) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(inputStream);
        return build(xmlConfigBuilder.parse());
    }

    public static SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}

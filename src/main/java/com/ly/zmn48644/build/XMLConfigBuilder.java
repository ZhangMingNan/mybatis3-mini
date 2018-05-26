package com.ly.zmn48644.build;

import com.ly.zmn48644.build.xml.XMLMapperBuilder;
import com.ly.zmn48644.datasource.DataSourceFactory;
import com.ly.zmn48644.io.Resources;
import com.ly.zmn48644.mapping.Environment;
import com.ly.zmn48644.parsing.XNode;
import com.ly.zmn48644.parsing.XPathParser;
import com.ly.zmn48644.reflection.DefaultReflectorFactory;
import com.ly.zmn48644.reflection.MetaClass;
import com.ly.zmn48644.reflection.ReflectorFactory;
import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.transaction.Transaction;
import com.ly.zmn48644.transaction.TransactionFactory;
import com.ly.zmn48644.type.TypeAliasRegistry;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder extends BaseBuilder {

    private XPathParser parser;

    //配置文件中的环境Id
    private String environment;

    private ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();


    public XMLConfigBuilder(InputStream inputStream) {
        //在这里创建全局配置对象
        super(new Configuration());
        //创建XPathParser对象用于解析XML
        parser = new XPathParser(inputStream);

    }

    public Configuration parse() {

        //解析mappers节点
        parseConfiguration(parser.evalNode("/configuration"));

        return this.configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            //解析settings节点数据,生成为
            Properties settings = settingsAsProperties(root.evalNode("settings"));

            settingsElement(settings);

            //解析environments节点
            environmentsElement(root.evalNode("environments"));

            //解析 typeAliases 配置
            typeAliasesElement(root.evalNode("typeAliases"));

            //解析映射配置
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("配置解析异常!");
        }
    }

    /**
     * 将从配置文件中读取到的配置数据
     *
     * @param settings
     */
    private void settingsElement(Properties settings) {
        //下划线转驼峰的配置
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(settings.getProperty("mapUnderscoreToCamelCase"), true));


    }

    private Properties settingsAsProperties(XNode settings) {
        //兼容没有配置的情况
        if (settings == null) {
            //返回空的Properties
            return new Properties();
        }
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        //读取配置完毕
        Properties props = settings.getChildrenAsProperties();
        //检查 Configuration 中有没有对应配置的 set方法.
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new RuntimeException("Configuration 没有 " + key + "对应的set方法!");
            }
        }
        return props;
    }

    /**
     * 解析类型别名配置
     *
     * @param typeAliases
     */
    private void typeAliasesElement(XNode typeAliases) {
        if (typeAliases != null) {
            List<XNode> children = typeAliases.getChildren();
            for (XNode child : children) {
                if ("package".equals(child.getName())) {
                    String packageName = child.getStringAttribute("name");
                    //指定包名,注册此包下面所有类的别名
                    configuration.getTypeAliasRegistry().registerAliases(packageName);
                }
            }
        }
    }

    /**
     * 解析环境配置
     *
     * @param environments
     */
    private void environmentsElement(XNode environments) throws Exception {

        if (this.environment == null) {
            this.environment = environments.getStringAttribute("default");
        }

        for (XNode children : environments.getChildren()) {
            String id = children.getStringAttribute("id");
            if (isSpecifiedEnvironment(id)) {
                TransactionFactory transactionFactory = transactionManagerElement(children.evalNode("transactionManager"));
                DataSourceFactory dataSourceFactory = dataSourceFactoryElement(children.evalNode("dataSource"));

                DataSource dataSource = dataSourceFactory.getDataSource();
                //创建 环境配置对象
                Environment environment = new Environment(id, transactionFactory, dataSource);
                //将环境配置对象设置给全局配置对象
                this.configuration.setEnvironment(environment);
            }
        }

    }

    private DataSourceFactory dataSourceFactoryElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties properties = context.getChildrenAsProperties();
            DataSourceFactory dataSourceFactory = (DataSourceFactory) resolveClass(type).newInstance();
            dataSourceFactory.setProperties(properties);
            return dataSourceFactory;
        }
        throw new RuntimeException("数据源配置解析失败!");
    }

    /**
     * 解析事务管理器
     *
     * @param context
     * @return
     * @throws Exception
     */
    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            TransactionFactory transactionFactory = (TransactionFactory) resolveClass(type).newInstance();
            return transactionFactory;
        }
        throw new RuntimeException("事务管理器配置解析失败!");
    }


    private boolean isSpecifiedEnvironment(String id) {
        if (id == null) {
            throw new RuntimeException("必须设置环境ID!");
        }
        if (this.environment == null) {
            throw new RuntimeException("必须设置默认的环境ID!");
        }
        if (this.environment.equals(id)) {
            return true;
        }
        return false;
    }

    /**
     * 解析mappers节点的配置
     *
     * @param parent 对应 配置文件中的mappers节点
     */
    private void mapperElement(XNode parent) {
        for (XNode child : parent.getChildren()) {
            String resource = child.getStringAttribute("resource");
            try {
                InputStream inputStream = Resources.getResourceAsStream(resource);
                //通过 XMLMapperBuilder 调用 parse 方法对xml进行处理解析映射配置文件
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource, inputStream, configuration);
                xmlMapperBuilder.parse();

            } catch (IOException e) {
                throw new RuntimeException("resource 指向的资源不存在或者未指定!");
            }

        }

    }
}

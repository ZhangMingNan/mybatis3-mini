package com.ly.zmn48644.build.xml;

import com.ly.zmn48644.build.BaseBuilder;
import com.ly.zmn48644.build.MapperBuilderAssistant;
import com.ly.zmn48644.parsing.XNode;
import com.ly.zmn48644.parsing.XPathParser;
import com.ly.zmn48644.session.Configuration;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder extends BaseBuilder {
    private String resource;

    private XPathParser parser;

    private MapperBuilderAssistant builderAssistant;


    public XMLMapperBuilder(String resource, InputStream inputStream, Configuration configuration) {
        super(configuration);
        this.resource = resource;
        this.parser = new XPathParser(inputStream);
        this.builderAssistant = new MapperBuilderAssistant(configuration);
    }

    /**
     * 处理mapper元素配置
     */
    public void parse() {

        configurationElement(parser.evalNode("/mapper"));

    }

    private void configurationElement(XNode context) {
        //设置命名空间
        String namespace = context.getStringAttribute("namespace");
        if (namespace == null || "".equals(namespace)) {
            throw new RuntimeException("映射配置文件的命名空间不能为空!");
        }
        this.builderAssistant.setCurrentNamespace(namespace);

        //目前这里只解析 select|update|delete|insert 配置
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    }

    /**
     * 解析映射配置文件中的 select,insert,update,delete元素,生成对应的
     *
     * @param context
     */
    private void buildStatementFromContext(List<XNode> context) {

        for (XNode child : context) {
            XMLStatementBuilder xmlStatementBuilder = new XMLStatementBuilder(configuration, builderAssistant,child);
            xmlStatementBuilder.parseStatementNode();
        }

    }
}

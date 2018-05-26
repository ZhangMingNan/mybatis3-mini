package com.ly.zmn48644.build;

import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.mapping.ResultMap;
import com.ly.zmn48644.mapping.SqlCommandType;
import com.ly.zmn48644.scripting.xmltags.SqlSource;
import com.ly.zmn48644.session.Configuration;

import java.util.Locale;

public class MapperBuilderAssistant extends BaseBuilder {
    private String currentNamespace;

    public MapperBuilderAssistant(Configuration configuration) {
        super(configuration);
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    public void addMappedStatement(String id, String nodeName, String resultType, SqlSource sqlSource) {
        //builderAssistant.addMappedStatement();
        MappedStatement ms = new MappedStatement();
        //命名空间 + 方法名
        ms.setId(this.getCurrentNamespace() + "." + id);
        ms.setSqlSource(sqlSource);

        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        ms.setSqlCommandType(sqlCommandType, configuration);

        ResultMap resultMap = new ResultMap();
        //将配置的resultType指定的类加载为class
        Class<?> resultTypeClass = resolveClass(resultType);
        resultMap.setType(resultTypeClass);
        ms.setResultMap(resultMap);

        this.configuration.addMappedStatement(ms);
    }
}

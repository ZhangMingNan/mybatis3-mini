package com.ly.zmn48644.scripting.xmltags;

import com.ly.zmn48644.mapping.BoundSql;

public class DynamicSource implements SqlSource {

    private SqlNode node;

    public DynamicSource(SqlNode node) {
        this.node = node;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        //处理动态标签和${}变量
        DynamicContext context = new DynamicContext(parameterObject);

        //处理#{}变量

        return null;
    }
}

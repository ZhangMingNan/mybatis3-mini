package com.ly.zmn48644.scripting.xmltags;

import com.ly.zmn48644.mapping.BoundSql;

public class RawSqlSource implements SqlSource {
    private SqlNode node;

    public RawSqlSource(SqlNode node) {
        this.node = node;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return null;
    }
}

package com.ly.zmn48644.build;

import com.ly.zmn48644.mapping.BoundSql;
import com.ly.zmn48644.scripting.xmltags.SqlSource;

public class StaticSqlSource implements SqlSource {
    private String sql;

    public StaticSqlSource(String sql) {
        this.sql = sql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(sql);
    }
}

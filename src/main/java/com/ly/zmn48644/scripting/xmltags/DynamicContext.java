package com.ly.zmn48644.scripting.xmltags;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态上下文
 */
public class DynamicContext {
    //保存上下文中的变量
    private Map<String, Object> bindings = new HashMap<>();
    //保存SQL
    protected StringBuilder sqlBuilder = new StringBuilder();

    public DynamicContext(Object parameter) {


    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    public void bind(String name, Object value) {
        bindings.put(name, value);
    }

    public void appendSql(String sql) {
        sqlBuilder.append(sql);
        sqlBuilder.append(" ");
    }

    public String getSql() {
        return sqlBuilder.toString().trim();
    }
}

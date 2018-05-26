package com.ly.zmn48644.scripting.xmltags;

public class StaticTextSqlNode implements SqlNode {
    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(text);
    }
}

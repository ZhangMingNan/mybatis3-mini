package com.ly.zmn48644.scripting.xmltags;

public class TextSqlNode implements SqlNode {

    private String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(parseGenericToken(text, context));
    }

    //临时简单处理
    private String parseGenericToken(String text, DynamicContext context) {
        //替换
        for (String key : context.getBindings().keySet()) {
            text = text.replace("${" + key + "}", context.getBindings().get(key).toString());
        }
        return text;
    }

    //判断是否是动态SQL , 这里临时简单处理
    public boolean isDynamic() {

        return text.contains("${");
    }

}

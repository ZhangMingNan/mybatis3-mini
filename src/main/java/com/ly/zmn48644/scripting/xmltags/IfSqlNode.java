package com.ly.zmn48644.scripting.xmltags;

public class IfSqlNode implements SqlNode {

    //这是一个表达式,需要需要运行时判断表达式是否成立
    private String test;
    //if标签
    private SqlNode content;
    //表达式计算器
    private final ExpressionEvaluator evaluator;

    public IfSqlNode(String test, SqlNode content) {
        this.test = test;
        this.content = content;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public void apply(DynamicContext context) {
        if (evaluator.evaluateBoolean(test, context.getBindings())) {
            content.apply(context);
        }
    }
}

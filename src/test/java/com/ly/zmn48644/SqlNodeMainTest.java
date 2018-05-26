package com.ly.zmn48644;

import com.ly.zmn48644.scripting.xmltags.*;

import java.util.ArrayList;
import java.util.List;


public class SqlNodeMainTest {

    public static void main(String[] args) {
        testWhereAndSet();

        //testWhereAndSet();
        // testSet();

    }

    private static void testSet() {
        DynamicContext context = new DynamicContext();
        context.bind("name", "张明楠");
        context.bind("age", "1");

        List<SqlNode> contents = new ArrayList<>();

        StaticTextSqlNode textSqlNode = new StaticTextSqlNode("update t_user ");

        StaticTextSqlNode staticTextSqlNode = new StaticTextSqlNode(" name = 1,");

        SetSqlNode setSqlNode = new SetSqlNode(staticTextSqlNode);

        contents.add(textSqlNode);
        contents.add(setSqlNode);

        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        mixedSqlNode.apply(context);
        System.out.println(context.getSql());
    }


    private static void testWhereAndSet() {
        DynamicContext context = new DynamicContext();
        context.bind("name", "张明楠");
        context.bind("age", "1");

        List<SqlNode> contents = new ArrayList<>();
        StaticTextSqlNode staticTextSqlNode = new StaticTextSqlNode("select name,age from t_user ");
        TextSqlNode textSqlNode = new TextSqlNode("and name=${name}");
        WhereSqlNode whereSqlNode = new WhereSqlNode(textSqlNode);
        contents.add(staticTextSqlNode);
        contents.add(whereSqlNode);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        mixedSqlNode.apply(context);
        System.out.println(context.getSql());
    }

    private static void testIf() {
        DynamicContext context = new DynamicContext();
        context.bind("name", "张明楠");
        context.bind("age", "");

        List<SqlNode> contents = new ArrayList<>();
        StaticTextSqlNode staticTextSqlNode = new StaticTextSqlNode("select name,age from t_user ");
        TextSqlNode textSqlNode = new TextSqlNode("where name=${name}");
        contents.add(staticTextSqlNode);
        contents.add(textSqlNode);

        TextSqlNode textSqlNode1 = new TextSqlNode("and age = ${age}");
        IfSqlNode ifSqlNode = new IfSqlNode("age != null && age != ''", textSqlNode1);
        contents.add(ifSqlNode);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        mixedSqlNode.apply(context);
        System.out.println(context.getSql());
    }
}

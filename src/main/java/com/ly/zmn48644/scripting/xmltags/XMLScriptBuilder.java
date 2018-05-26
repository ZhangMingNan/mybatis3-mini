package com.ly.zmn48644.scripting.xmltags;

import com.ly.zmn48644.build.BaseBuilder;
import com.ly.zmn48644.parsing.XNode;
import com.ly.zmn48644.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangmingnan on 2018/5/13.
 */
public class XMLScriptBuilder extends BaseBuilder {

    private final XNode context;

    private boolean isDynamic;

    private Map<String, NodeHandler> nodeHandlerMap;

    public XMLScriptBuilder(Configuration configuration, XNode context) {
        super(configuration);
        this.context = context;
        initNodeHandlerMap();
    }

    private void initNodeHandlerMap() {
        nodeHandlerMap = new HashMap<>();
        nodeHandlerMap.put("where", new WhereNodeHandler());
        nodeHandlerMap.put("set", new SetNodeHandler());
        nodeHandlerMap.put("if", new IfNodeHandler());
    }


    public SqlSource parseScriptNode() {
        //解析动态标签
        MixedSqlNode rootSqlNode = parseDynamicTags(context);
        SqlSource sqlSource;
        if (isDynamic){
            sqlSource = new DynamicSource(rootSqlNode);
        }else{
            sqlSource = new RawSqlSource(rootSqlNode);
        }
        return sqlSource;
    }

    private MixedSqlNode parseDynamicTags(XNode node) {
        NodeList children = node.getNode().getChildNodes();
        List<SqlNode> contents = new ArrayList<SqlNode>();
        for (int i = 0; i < children.getLength(); i++) {
            Node item = children.item(i);
            XNode child = node.newXNode(item);
            if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {

                String data = child.getStringBody("");
                TextSqlNode textSqlNode = new TextSqlNode(data);
                if (textSqlNode.isDynamic()) {
                    isDynamic = true;
                    contents.add(textSqlNode);
                } else {
                    contents.add(new StaticTextSqlNode(data));
                }
            } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) {

                String nodeName = child.getNode().getNodeName();
                NodeHandler handler = nodeHandlerMap.get(nodeName);
                handler.handleNode(child, contents);
            }
        }
        return new MixedSqlNode(contents);
    }

    private interface NodeHandler {
        void handleNode(XNode node, List<SqlNode> contents);
    }

    private class WhereNodeHandler implements NodeHandler {
        @Override
        public void handleNode(XNode node, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(node);
            WhereSqlNode whereSqlNode = new WhereSqlNode(mixedSqlNode);
            contents.add(whereSqlNode);
        }
    }

    private class SetNodeHandler implements NodeHandler {
        @Override
        public void handleNode(XNode node, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(node);
            SetSqlNode setSqlNode = new SetSqlNode(mixedSqlNode);
            contents.add(setSqlNode);
        }
    }

    private class IfNodeHandler implements NodeHandler {
        @Override
        public void handleNode(XNode node, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(node);
            String text = node.getStringAttribute("test");
            IfSqlNode ifSqlNode = new IfSqlNode(text, mixedSqlNode);
            contents.add(ifSqlNode);
        }
    }

}












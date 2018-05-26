package com.ly.zmn48644.parsing;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 用于封装jdk自带的Node对象.
 */
public class XNode {

    //原生Node对象
    private final Node node;
    //构造方法中 获取到的当前封装节点的名称
    private final String name;
    //构造方法中 解析到的 节点的 body文本.
    private final String body;
    //构造方法中 解析到的 当前节点的 属性名 以及 值 组成的 Properties.
    private final Properties attributes;
    //构造方法传入的外部的 站位变量.
    private final Properties variables;
    //构造方法出入的外部的处理器对象.
    private final XPathParser xpathParser;

    /**
     * 构造方法
     *
     * @param xpathParser 处理器对象
     * @param node        原生节点
     * @param variables   节点中需要替换的变量
     */
    public XNode(XPathParser xpathParser, Node node, Properties variables) {
        this.xpathParser = xpathParser;
        this.node = node;
        this.name = node.getNodeName();
        this.variables = variables;
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
    }

    public XNode newXNode(Node node) {
        return new XNode(xpathParser, node, variables);
    }

    /**
     * 获取父节点
     *
     * @return
     */
    public XNode getParent() {
        Node parent = node.getParentNode();
        if (parent == null || !(parent instanceof Element)) {
            return null;
        } else {
            return new XNode(xpathParser, parent, variables);
        }
    }

    /**
     * 获取当前节点对象的path,使用while循环而非递归的方式获取 一个子节点的path(从子节点到跟节点的路径)
     *
     * @return
     */
    public String getPath() {
        StringBuilder builder = new StringBuilder();
        Node current = node;
        //循环条件 当前节点不等于null 并且 当前节点是 Element 对象实例.
        while (current != null && current instanceof Element) {
            //current指向的不是当前节点 则 在拼接的path前面 添加一个"/"
            if (current != node) {
                builder.insert(0, "/");
            }
            //获取current指向节点的名称
            builder.insert(0, current.getNodeName());
            //将 current 指向 current现在指向的节点的父节点.
            current = current.getParentNode();
        }
        return builder.toString();
    }

    /**
     * 获取元素的唯一ID,比如 employee[${id_var}]_height_t
     * 最前面是ID值 然后每个节点使用 _ 下划线连接.
     *
     * @return
     */
    public String getValueBasedIdentifier() {
        StringBuilder builder = new StringBuilder();
        //current 指向 this.
        XNode current = this;
        while (current != null) {
            if (current != this) {
                builder.insert(0, "_");
            }
            //获取 id属性 以及 属性值
            String value = current.getStringAttribute("id",
                    current.getStringAttribute("value",
                            current.getStringAttribute("property", null)));
            if (value != null) {
                value = value.replace('.', '_');
                builder.insert(0, "]");
                builder.insert(0,
                        value);
                builder.insert(0, "[");
            }
            builder.insert(0, current.getName());
            current = current.getParent();
        }
        return builder.toString();
    }

    /**
     * 解析当前节点中的一个字符串根据传入的表达式.
     *
     * @param expression
     * @return
     */
    public String evalString(String expression) {
        return xpathParser.evalString(node, expression);
    }

    /**
     * 解析当前节点中的一个布尔值根据传入的表达式.
     *
     * @param expression
     * @return
     */
    public Boolean evalBoolean(String expression) {
        return xpathParser.evalBoolean(node, expression);
    }

    public Double evalDouble(String expression) {
        return xpathParser.evalDouble(node, expression);
    }

    /**
     * 解析当前节点的一组节点根据出入的表达式
     *
     * @param expression
     * @return
     */
    public List<XNode> evalNodes(String expression) {
        return xpathParser.evalNodes(node, expression);
    }

    /**
     * 解析当前节点的一个节点根据出入的表达式
     *
     * @param expression
     * @return
     */
    public XNode evalNode(String expression) {
        return xpathParser.evalNode(node, expression);
    }

    public Node getNode() {
        return node;
    }

    /**
     * 获取当前节点的名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 获取当前节点body内容以字符串形式返回
     *
     * @return
     */
    public String getStringBody() {
        return getStringBody(null);
    }

    public String getStringBody(String def) {
        if (body == null) {
            return def;
        } else {
            return body;
        }
    }

    /**
     * 获取当前节点body内容以布尔类型返回
     *
     * @return
     */
    public Boolean getBooleanBody() {
        return getBooleanBody(null);
    }

    public Boolean getBooleanBody(Boolean def) {
        if (body == null) {
            return def;
        } else {
            return Boolean.valueOf(body);
        }
    }

    /**
     * 获取当前节点body内容以int类型返回
     *
     * @return
     */
    public Integer getIntBody() {
        return getIntBody(null);
    }

    public Integer getIntBody(Integer def) {
        if (body == null) {
            return def;
        } else {
            return Integer.parseInt(body);
        }
    }

    /**
     * 获取当前节点body内容以long类型返回
     *
     * @return
     */
    public Long getLongBody() {
        return getLongBody(null);
    }

    public Long getLongBody(Long def) {
        if (body == null) {
            return def;
        } else {
            return Long.parseLong(body);
        }
    }

    /**
     * 获取当前节点body内容以Double类型返回
     *
     * @return
     */
    public Double getDoubleBody() {
        return getDoubleBody(null);
    }

    public Double getDoubleBody(Double def) {
        if (body == null) {
            return def;
        } else {
            return Double.parseDouble(body);
        }
    }

    /**
     * 获取当前节点body内容以Float类型返回
     *
     * @return
     */
    public Float getFloatBody() {
        return getFloatBody(null);
    }

    public Float getFloatBody(Float def) {
        if (body == null) {
            return def;
        } else {
            return Float.parseFloat(body);
        }
    }

    /**
     * 获取 attributes 中 指定名称 的值的枚举类型
     *
     * @return
     */
    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name) {
        return getEnumAttribute(enumType, name, null);
    }

    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name, T def) {
        String value = getStringAttribute(name);
        if (value == null) {
            return def;
        } else {
            return Enum.valueOf(enumType, value);
        }
    }

    /**
     * 获取attributes中指定名称 的值的字符串类型
     *
     * @return
     */
    public String getStringAttribute(String name) {
        //给定属性名,获取当前节点的属性值,设置默认值为null,没有解析到返回null.
        return getStringAttribute(name, null);
    }

    public String getStringAttribute(String name, String def) {
        //从 attributes 获取配置,这个配置在节点创建的时候已经初始化完成.
        String value = attributes.getProperty(name);
        //如果没有获取到,返回传入的默认值.
        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    /**
     * 获取attributes中指定名称 的值的布尔类型
     *
     * @return
     */
    public Boolean getBooleanAttribute(String name) {
        return getBooleanAttribute(name, null);
    }

    public Boolean getBooleanAttribute(String name, Boolean def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Boolean.valueOf(value);
        }
    }

    /**
     * 获取attributes中指定名称 的值的int类型
     *
     * @return
     */
    public Integer getIntAttribute(String name) {
        return getIntAttribute(name, null);
    }

    public Integer getIntAttribute(String name, Integer def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取attributes中指定名称 的值的lang类型
     *
     * @return
     */
    public Long getLongAttribute(String name) {
        return getLongAttribute(name, null);
    }

    public Long getLongAttribute(String name, Long def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * 获取attributes中指定名称 的值的double类型
     *
     * @return
     */
    public Double getDoubleAttribute(String name) {
        return getDoubleAttribute(name, null);
    }

    public Double getDoubleAttribute(String name, Double def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Double.parseDouble(value);
        }
    }

    /**
     * 获取attributes中指定名称 的值的float类型
     *
     * @return
     */
    public Float getFloatAttribute(String name) {
        return getFloatAttribute(name, null);
    }

    public Float getFloatAttribute(String name, Float def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Float.parseFloat(value);
        }
    }

    /**
     * 获取当前节点的子节点
     *
     * @return
     */
    public List<XNode> getChildren() {
        List<XNode> children = new ArrayList<XNode>();
        //获取原生子节点
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0, n = nodeList.getLength(); i < n; i++) {
                //拿到每一个原生子节点
                Node node = nodeList.item(i);
                //如果类型是节点类型
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    //放入子节点列表
                    children.add(new XNode(xpathParser, node, variables));
                }
            }
        }
        //返回找到的子节点列表
        return children;
    }

    /**
     * 获取 当前节点的 attributes 中 name 和 value 返回 Properties
     * <p>
     * 此方法 在 XMLConfigBuilder.settingsAsProperties 方法中调用, 用于解析配置文件中的 settings 元素节点数据.
     *
     * @return
     */
    public Properties getChildrenAsProperties() {
        Properties properties = new Properties();
        for (XNode child : getChildren()) {
            //获取配置key
            String name = child.getStringAttribute("name");
            //获取配置key的值.
            String value = child.getStringAttribute("value");
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    /**
     * 节点的toString方法
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        builder.append(name);
        for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
            builder.append(" ");
            builder.append(entry.getKey());
            builder.append("=\"");
            builder.append(entry.getValue());
            builder.append("\"");
        }
        List<XNode> children = getChildren();
        if (!children.isEmpty()) {
            builder.append(">\n");
            for (XNode node : children) {
                builder.append(node.toString());
            }
            builder.append("</");
            builder.append(name);
            builder.append(">");
        } else if (body != null) {
            builder.append(">");
            builder.append(body);
            builder.append("</");
            builder.append(name);
            builder.append(">");
        } else {
            builder.append("/>");
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * 初始化构造方法中调用,初始化 节点的 attributes属性
     *
     * @param n
     * @return
     */
    private Properties parseAttributes(Node n) {
        //创建一个空的Properties对象
        Properties attributes = new Properties();
        //获取节点的所有属性
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                //通过PropertyParser处理节点属性值中的占位变量
                String value = PropertyParser.parse(attribute.getNodeValue(), variables);
                //将解析到的 节点的属性 放入 Properties 对象中.
                attributes.put(attribute.getNodeName(), value);
            }
        }
        //返回
        return attributes;
    }

    /**
     * 获取节点的body内容
     *
     * @param node
     * @return
     */
    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE
                || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) child).getData();
            data = PropertyParser.parse(data, variables);
            return data;
        }
        return null;
    }

}
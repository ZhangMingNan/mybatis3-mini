package com.ly.zmn48644.parsing;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * mybatis 中的封装xpath处理的类,给定路径,获取某种类型比如字符串,整型等数据.
 * 用于配置解析,SQL解析.
 */
public class XPathParser {

    //封装的文档对象
    private final Document document;
    //是否开启验证
    private boolean validation;
    //实体解析器
    private EntityResolver entityResolver;

    //这里保存的是 配置文件中 properties 标签中设置的 配置信息
    //
    private Properties variables;
    //封装的xpath对象
    private XPath xpath;

    //重载的构造方法不赘述
    public XPathParser(String xml) {
        commonConstructor(false, null, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    //重载的构造方法不赘述
    public XPathParser(Reader reader) {
        commonConstructor(false, null, null);
        this.document = createDocument(new InputSource(reader));
    }

    //重载的构造方法不赘述
    public XPathParser(InputStream inputStream) {
        commonConstructor(false, null, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    //重载的构造方法不赘述
    public XPathParser(Document document) {
        commonConstructor(false, null, null);
        this.document = document;
    }

    //重载的构造方法不赘述
    public XPathParser(String xml, boolean validation) {
        commonConstructor(validation, null, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    //重载的构造方法不赘述
    public XPathParser(Reader reader, boolean validation) {
        commonConstructor(validation, null, null);
        this.document = createDocument(new InputSource(reader));
    }

    //重载的构造方法不赘述
    public XPathParser(InputStream inputStream, boolean validation) {
        commonConstructor(validation, null, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    //重载的构造方法不赘述
    public XPathParser(Document document, boolean validation) {
        commonConstructor(validation, null, null);
        this.document = document;
    }

    //重载的构造方法不赘述
    public XPathParser(String xml, boolean validation, Properties variables) {
        commonConstructor(validation, variables, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    //重载的构造方法不赘述
    public XPathParser(Reader reader, boolean validation, Properties variables) {
        commonConstructor(validation, variables, null);
        this.document = createDocument(new InputSource(reader));
    }

    //重载的构造方法不赘述
    public XPathParser(InputStream inputStream, boolean validation, Properties variables) {
        commonConstructor(validation, variables, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    //重载的构造方法不赘述
    public XPathParser(Document document, boolean validation, Properties variables) {
        commonConstructor(validation, variables, null);
        this.document = document;
    }

    //重载的构造方法不赘述
    public XPathParser(String xml, boolean validation, Properties variables, EntityResolver entityResolver) {
        commonConstructor(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    //重载的构造方法不赘述
    public XPathParser(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
        commonConstructor(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(reader));
    }


    /**
     * 从资源加载器中获取一个输入流,设置 其他配置信息,完成初始化.
     *
     * @param inputStream
     * @param validation
     * @param variables
     * @param entityResolver
     */
    public XPathParser(InputStream inputStream, boolean validation, Properties variables, EntityResolver entityResolver) {
        commonConstructor(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(inputStream));
    }

    //重载的构造方法不赘述
    public XPathParser(Document document, boolean validation, Properties variables, EntityResolver entityResolver) {
        commonConstructor(validation, variables, entityResolver);
        this.document = document;
    }

    /**
     * 设置解析过程中可能需要填充的变量
     * <p>
     * 此方法 在  XMLConfigBuilder#propertiesElement 中被使用
     *
     * @param variables
     */
    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    /**
     * 解析文档中的一个string值
     *
     * @param expression
     * @return
     */
    public String evalString(String expression) {
        return evalString(document, expression);
    }

    public String evalString(Object root, String expression) {
        //从文档中获取原始的文本.
        String result = (String) evaluate(expression, root, XPathConstants.STRING);
        //使用属性解析器,解析原始文本中带有可替换变量的文本.
        result = PropertyParser.parse(result, variables);
        return result;
    }

    /**
     * 解析文档中的一个布尔类型值
     *
     * @param expression
     * @return
     */
    public Boolean evalBoolean(String expression) {
        return evalBoolean(document, expression);
    }

    public Boolean evalBoolean(Object root, String expression) {
        return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
    }

    /**
     * 解析文档中的一个short类型值
     *
     * @param expression
     * @return
     */
    public Short evalShort(String expression) {
        return evalShort(document, expression);
    }

    public Short evalShort(Object root, String expression) {
        return Short.valueOf(evalString(root, expression));
    }

    public Integer evalInteger(String expression) {
        return evalInteger(document, expression);
    }

    /**
     * 解析文档中的一个int类型值
     *
     * @param expression
     * @return
     */
    public Integer evalInteger(Object root, String expression) {
        return Integer.valueOf(evalString(root, expression));
    }

    public Long evalLong(String expression) {
        return evalLong(document, expression);
    }

    /**
     * 解析文档中的一个lang类型值
     *
     * @param expression
     * @return
     */
    public Long evalLong(Object root, String expression) {
        return Long.valueOf(evalString(root, expression));
    }

    public Float evalFloat(String expression) {
        return evalFloat(document, expression);
    }

    /**
     * 解析文档中的一个float类型值
     *
     * @param expression
     * @return
     */
    public Float evalFloat(Object root, String expression) {
        return Float.valueOf(evalString(root, expression));
    }

    public Double evalDouble(String expression) {
        return evalDouble(document, expression);
    }

    /**
     * 解析文档中的一个double类型值
     *
     * @param expression
     * @return
     */
    public Double evalDouble(Object root, String expression) {
        return (Double) evaluate(expression, root, XPathConstants.NUMBER);
    }

    public List<XNode> evalNodes(String expression) {
        return evalNodes(document, expression);
    }

    /**
     * 解析文档中的一组节点
     *
     * @param expression
     * @return
     */
    public List<XNode> evalNodes(Object root, String expression) {
        List<XNode> xnodes = new ArrayList<XNode>();
        NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            xnodes.add(new XNode(this, nodes.item(i), variables));
        }
        return xnodes;
    }

    /**
     * 解析文档中的一个节点
     *
     * @param expression
     * @return
     */
    public XNode evalNode(String expression) {
        return evalNode(document, expression);
    }

    public XNode evalNode(Object root, String expression) {
        Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
        if (node == null) {
            return null;
        }
        return new XNode(this, node, variables);
    }

    /**
     * 根据表达式解析文档中的
     *
     * @param expression
     * @param root
     * @param returnType
     * @return
     */
    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return xpath.evaluate(expression, root, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    /**
     * 创建文档对象和新方法
     *
     * @param inputSource 读取的资源输入流
     * @return
     */
    private Document createDocument(InputSource inputSource) {
        //此方法调用必须在通用构造方法之后.
        try {
            //获取文档构建器工厂对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //设置是否校验,以及一系列配置
            factory.setValidating(validation);
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            //从构建器工厂获取文档构建对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            //设置文档解析器
            builder.setEntityResolver(entityResolver);
            //设置异常处理器
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    //有错误不处理直接抛出
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    //有错误不处理直接抛出
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    //有错误不处理直
                }
            });
            return builder.parse(inputSource);
        } catch (Exception e) {
            //原始代码是一个 自定义的解析异常, 这里先写为运行时异常.
            throw new RuntimeException("Error creating document instance.  Cause: " + e, e);
        }
    }

    /**
     * 通用的属性初始化方法,在调用  createDocument 方法之前必须调用此方法,完成属性初始化.
     *
     * @param validation
     * @param variables
     * @param entityResolver
     */
    private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
        this.validation = validation;
        this.entityResolver = entityResolver;
        this.variables = variables;
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();
    }

}

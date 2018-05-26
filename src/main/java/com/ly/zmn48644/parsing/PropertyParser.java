package com.ly.zmn48644.parsing;

import java.util.Properties;

/**
 * 用于解析文本中的占位符变量,比如 ${username},将变量替换成 Properties 中对应的值;
 */
public class PropertyParser {

    //key前缀
    private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";
    public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";
    //默认的分隔符 {@code ":"}
    public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";
    //不可用默认值
    private static final String ENABLE_DEFAULT_VALUE = "false";
    //默认的分割付
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private PropertyParser() {
        // Prevent Instantiation
    }

    /**
     * 替换字符串中的 占位符,从传入的variables中查找值
     *
     * @param string
     * @param variables
     * @return
     */
    public static String parse(String string, Properties variables) {
        //变量代号处理器,初始化占位符变量处理器,这是个静态内部类
        VariableTokenHandler handler = new VariableTokenHandler(variables);
        //普通代号解析器
        GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
        return parser.parse(string);
    }

    /**
     * 变量代号处理器,处理占位符中 使用 : 定义的默认值的情况
     */
    private static class VariableTokenHandler implements TokenHandler {
        private final Properties variables;
        private final boolean enableDefaultValue;
        private final String defaultValueSeparator;

        private VariableTokenHandler(Properties variables) {
            this.variables = variables;
            this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, ENABLE_DEFAULT_VALUE));
            this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
        }

        private String getPropertyValue(String key, String defaultValue) {
            return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
        }

        //处理可能带有 默认值的占位符变量
        public String handleToken(String content) {
            if (variables != null) {
                String key = content;
                if (enableDefaultValue) {
                    final int separatorIndex = content.indexOf(defaultValueSeparator);
                    String defaultValue = null;
                    if (separatorIndex >= 0) {
                        //处理默认值这种情况
                        key = content.substring(0, separatorIndex);//获取占位符变量key
                        //获取占位符中定义的默认值
                        defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
                    }
                    if (defaultValue != null) {
                        return variables.getProperty(key, defaultValue);
                    }
                }
                if (variables.containsKey(key)) {
                    return variables.getProperty(key);
                }
            }
            return "${" + content + "}";
        }
    }

}

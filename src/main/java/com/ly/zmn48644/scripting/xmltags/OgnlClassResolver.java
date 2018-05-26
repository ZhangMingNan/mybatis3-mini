
package com.ly.zmn48644.scripting.xmltags;



import com.ly.zmn48644.io.Resources;
import ognl.ClassResolver;


import java.util.HashMap;
import java.util.Map;


public class OgnlClassResolver implements ClassResolver {

    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>(101);

    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class<?> result;
        if ((result = classes.get(className)) == null) {
            try {
                result = Resources.classForName(className);
            } catch (ClassNotFoundException e1) {
                if (className.indexOf('.') == -1) {
                    result = Resources.classForName("java.lang." + className);
                    classes.put("java.lang." + className, result);
                }
            }
            classes.put(className, result);
        }
        return result;
    }

}

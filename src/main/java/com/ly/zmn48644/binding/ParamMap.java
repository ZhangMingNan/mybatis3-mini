package com.ly.zmn48644.binding;

import java.util.HashMap;

public class ParamMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = -2212268410512043556L;

    @Override
    public V get(Object key) {
        if (!super.containsKey(key)) {
            throw new RuntimeException("Parameter '" + key + "' not found. Available parameters are " + keySet());
        }
        return super.get(key);
    }

}

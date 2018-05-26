package com.ly.zmn48644.build;

import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.type.TypeAliasRegistry;

public class BaseBuilder {
    protected final Configuration configuration;

    private TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    protected Class<?> resolveClass(String type) {
        Class<?> clazz = resolveAlias(type);
        return clazz;
    }

    protected Class<?> resolveAlias(String type) {
        return typeAliasRegistry.resolveAlias(type);
    }


    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }
}

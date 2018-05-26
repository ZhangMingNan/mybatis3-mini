package com.ly.zmn48644.binding;

import com.ly.zmn48644.mapping.MappedStatement;
import com.ly.zmn48644.mapping.SqlCommandType;
import com.ly.zmn48644.session.Configuration;

import java.lang.reflect.Method;

public class SqlCommand {

    private String name;
    private SqlCommandType type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
        //根据传入的参数,设置name,和type
        String id = mapperInterface.getName() + "." + method.getName();
        MappedStatement ms = configuration.getMappedStatement(id);
        this.name = ms.getId();
        this.type = ms.getSqlCommandType();

    }

    public String getName() {
        return name;
    }


    public SqlCommandType getType() {
        return type;
    }

}

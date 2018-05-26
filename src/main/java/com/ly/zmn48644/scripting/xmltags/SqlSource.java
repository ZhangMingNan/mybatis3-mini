package com.ly.zmn48644.scripting.xmltags;

import com.ly.zmn48644.mapping.BoundSql;

public interface SqlSource {
    BoundSql getBoundSql(Object parameterObject);
}

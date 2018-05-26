package com.ly.zmn48644.scripting.xmltags;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangmingnan on 2018/5/13.
 */
public class WhereSqlNode extends TrimSqlNode {

    private static final List<String> prefixList = Arrays.asList("AND", "OR");

    public WhereSqlNode(SqlNode contents) {
        super("where", prefixList, null, null, contents);
    }
}

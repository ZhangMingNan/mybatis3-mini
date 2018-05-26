package com.ly.zmn48644.scripting.xmltags;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangmingnan on 2018/5/13.
 */
public class SetSqlNode extends TrimSqlNode {

    private static final List<String> suffixList = Arrays.asList(",");

    public SetSqlNode(SqlNode contents) {
        super(null, null, "set", suffixList, contents);
    }
}

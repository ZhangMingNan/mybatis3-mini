
package com.ly.zmn48644.mapping;


public enum StatementType {
    //用于通用查询,一般不传入参数
    STATEMENT,
    //参数化的查询更新等,数据库会预编译执行SQL,速度更快,避免sql注入问题
    PREPARED,
    //用于存储过程
    CALLABLE
}

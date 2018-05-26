package com.ly.zmn48644.binding;

import com.ly.zmn48644.session.Configuration;
import com.ly.zmn48644.session.SqlSession;

import java.lang.reflect.Method;

/**
 * 接口中的一个方法也就是,一个Method对象对应一个MapperMethod
 * 当调用接口中的方法时,通过动态代理调用 MapperMethod 的 execute 方法.
 */
public class MapperMethod {
    private final SqlCommand sqlCommand;
    //定义一个类封装 方法返回值数据,在execute方法执行select语句的时候判断
    private MethodSignature method;


    public MapperMethod(Configuration configuration, Class<?> mapperInterface, Method method) {
        //这里初始化当前对象的 SqlCommand 属性
        this.sqlCommand = new SqlCommand(configuration, mapperInterface, method);
        this.method = new MethodSignature(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {

        Object result;
        //在这里调用SqlSession中相对应的方法比如update,delete,select等方法.
        switch (sqlCommand.getType()) {
            case SELECT: {
                if (method.isReturnsVoid()) {
                    //接口方法返回void
                    result = null;
                } else if (method.isReturnsMany()) {
                    //接口方法返回集合或者数组
                    result = executeForMany(sqlSession);
                } else {
                    //转换通过动态代理获取的到 args 参数
                    Object param = method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(sqlCommand.getName(), param);
                }
                break;
            }
            case UPDATE: {
                result = null;
                System.out.println("执行更新操作!");
                break;
            }
            case DELETE: {
                result = null;
                System.out.println("执行删除操作");
                break;
            }
            case INSERT: {
                result = null;
                System.out.println("执行插入操作");
            }
            default:
                throw new RuntimeException();
        }

        return result;
    }

    private Object executeForMany(SqlSession sqlSession) {
        return null;
    }
}

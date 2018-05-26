package com.ly.zmn48644;

import com.ly.zmn48644.io.Resources;
import com.ly.zmn48644.mapper.AuthorMapper;
import com.ly.zmn48644.model.Author;
import com.ly.zmn48644.session.SqlSession;
import com.ly.zmn48644.session.SqlSessionFactory;
import com.ly.zmn48644.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionTest {

    public static void main(String[] args) throws IOException {
        //读取主配置文件
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        //建造会话工厂
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build(inputStream);
        //打开一个会话
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取mapper代理对象
        AuthorMapper mapper = sqlSession.getMapper(AuthorMapper.class);
        //调用查询方法
        Author author = mapper.selectAuthorById(2);

        System.out.println(author.toString());
    }
}

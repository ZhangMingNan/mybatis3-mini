
package com.ly.zmn48644.session;

import java.util.List;
import java.util.Map;

/**
 * 提供给上层引用的,用于获取mapper对象.
 */
public interface SqlSession {

    <T> T selectOne(String statement);

    <T> T selectOne(String statement, Object parameter);

    <K, V> Map<K, V> selectMap(String statement, String mapKey);

    <E> List<E> selectList(String statement,Object parameter);

    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();
}

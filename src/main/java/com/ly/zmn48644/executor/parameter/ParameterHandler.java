package com.ly.zmn48644.executor.parameter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 参数处理器
 */
public interface ParameterHandler {

  Object getParameterObject();

  void setParameters(PreparedStatement ps) throws SQLException;

}

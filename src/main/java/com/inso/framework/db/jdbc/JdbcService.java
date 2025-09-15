package com.inso.framework.db.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.inso.framework.service.Callback;

public interface JdbcService {
	
	public Connection getConnection() throws SQLException;
	
	public JdbcTemplate getJdbcTemplate();

	public long count(String sql, Object... values);

	public BigDecimal bigDecimalCount(String sql, Object... values);

	public <T> T queryForObject(String sql, Class<T> requiredType);
	public <T> T queryForObject(String sql, Class<T> requiredType, Object...values);
	
	public <T> List<T> queryForList(String sql, Class<T> requiredType, Object...values);
	public Map<String, Object> queryForMap(String sql, Object... values);
	public List<Map<String,Object>> queryForListMap(String sql, Object...values);
	
	public void queryResultSet(Callback<ResultSet> callback, String sql, Object... values);
	
	public <T> void queryAll(Callback<T> callback, String sql, Class<T> requiredType, Object...values);
	public <T> void queryAll(boolean onlyEntity, Callback<T> callback, String sql, Class<T> requiredType, Object...values);

	public  void queryAllForMap(Callback<Map<String, Object>> callback, String sql, Object... values);

	public int executeUpdate(String sql, Object... values);
	public long executeInsert(String sql,Object... values);//返回主键

}

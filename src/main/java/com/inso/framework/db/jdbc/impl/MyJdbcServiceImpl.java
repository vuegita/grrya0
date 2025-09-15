package com.inso.framework.db.jdbc.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Maps;
import com.inso.framework.context.MyEnvironment;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.inso.framework.db.jdbc.JdbcService;
import com.inso.framework.db.jdbc.ResultSetUtils;
import com.inso.framework.db.jdbc.util.DBUtil;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.reflect.JavaBeanField;
import com.inso.framework.reflect.MyFieldFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;


public class MyJdbcServiceImpl implements JdbcService
{
	private static Log LOG = LogFactory.getLog(MyJdbcServiceImpl.class);
	private DataSource mDstaSource;
	private JdbcTemplate jdbcTemplate;

	private boolean isDEV = MyEnvironment.isDev();
	
	public MyJdbcServiceImpl(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
		this.mDstaSource = jdbcTemplate.getDataSource();
	}
	
	public DataSource getDataSource()
	{
		return mDstaSource;
	}
	
	public Connection getConnection() throws SQLException {
		return mDstaSource.getConnection();
	}
	
	public int executeUpdate(String sql, Object... values)
	{
		int len = values.length;
		Object[] array = new Object[len];
		for(int i = 0; i < len; i ++ )
		{
			Object value = values[i];
			if(value instanceof String)
			{
				String valueString = StringEscapeUtils.escapeSql((String)value);
				array[i] = valueString;
			} 
			else if(value == CollectionUtils.emptyObjectArray())
			{
				break;
			}
			else
			{
				array[i] = value;
			}
		}
		return jdbcTemplate.update(sql, array);
	}
	
	public <T> T queryForObject(String sql, Class<T> requiredType)
	{
		return queryForObject(sql, requiredType, CollectionUtils.emptyObjectArray());
	}
	@SuppressWarnings("unchecked")
	public <T> T queryForObject(String sql, Class<T> requiredType, Object...values)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			
			if(requiredType == String.class)
			{
				if(!result.next())
				{
					return null;
				}
				String value =  result.getString(1);
				return (T) value;
			}
			else if(requiredType == Long.class)
			{
				if(!result.next())
				{
					return null;
				}
				Long value =  result.getLong(1);
				return (T) value;
			}
			else if(requiredType == BigDecimal.class)
			{
				if(!result.next())
				{
					return null;
				}
				BigDecimal value =  result.getBigDecimal(1);
				return (T) value;
			}
			else if(requiredType == Float.class)
			{
				if(!result.next())
				{
					return null;
				}
				Float value =  result.getFloat(1);
				return (T) value;
			}
			else if(requiredType == Double.class)
			{
				if(!result.next())
				{
					return null;
				}
				Double value =  result.getDouble(1);
				return (T) value;
			}
			
			return ResultSetUtils.convertJavaBean(requiredType, result);	
		} catch (SQLException e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
		return null;
	}
	
	public void queryResultSet(Callback<ResultSet> callback, String sql, Object... values)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			while(result.next())
			{
				callback.execute(result);
			}
		} catch (SQLException e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
	}
	public List<Map<String,Object>> queryForListMap(String sql, Object...values){
		List<Map<String,Object>> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			ResultSetMetaData meta = result.getMetaData();
			int count = meta.getColumnCount();
			while(result.next())
			{
				if(list == null) list = new ArrayList<Map<String,Object>>();

				Map<String,Object> model = new HashMap<String, Object>();

				for(int i = 1; i <= count; i ++)
				{
					String column = meta.getColumnName(i);
					Object value = result.getObject(column);
					column = column.substring(column.indexOf("_")+1);
					model.put(column, value);
				}
				list.add(model);
			}


		} catch (Exception e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
		return list;
	}


	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(String sql, Class<T> requiredType, Object...values)
	{
		List<T> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			ResultSetMetaData meta = result.getMetaData();

			while(result.next())
			{
				if(list == null) list = new ArrayList<T>();
				
				if(requiredType == String.class)
				{
					String value =  result.getString(1);
					list.add((T)value);
				}
				else if(requiredType == Long.class)
				{
					Long value =  result.getLong(1);
					list.add((T)value);
				}
				else if(requiredType == BigDecimal.class)
				{
					BigDecimal value =  result.getBigDecimal(1);
					list.add((T)value);
				}
				else if(requiredType == Float.class)
				{
					Float value =  result.getFloat(1);
					list.add((T)value);
				}
				else if(requiredType == Double.class)
				{
					Double value =  result.getDouble(1);
					list.add((T)value);
				}
				else
				{
					T model = requiredType.newInstance();
					JavaBeanField beanField = MyFieldFactory.getField(requiredType);
					int count = meta.getColumnCount();
					for(int i = 1; i <= count; i ++)
					{
						try {
							//String column = meta.getColumnName(i);
							//获取别名，如果没有设置别名，就按默认列
							String column = meta.getColumnLabel(i);
							Object value = result.getObject(column);
							Field field = beanField.getField(column);
							if(field != null) {
								ResultSetUtils.safeSetFieldValue(model, field, result, column);
//								String fieldType = field.getGenericType().toString();
//								 if(ResultSetUtils.BOOLEAN.equalsIgnoreCase(fieldType))
//								 {
//			                    	 field.set(model, result.getBoolean(column));
//			                    	 //System.out.println(column +  " = " + result.getBoolean(column));
//								 }
//								 else
//								 {
//									 field.set(model, value);
//								 }
//								field.set(model, value);
							}
						} catch (Exception e) {
						}
					}
					list.add(model);
				}
			} 
		} catch (Exception e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}

		if(list == null)
		{
			list = Collections.emptyList();
		}

		return list;
	}


	public <T> void queryAll(Callback<T> callback, String sql, Class<T> requiredType, Object...values)
	{
		queryAll(false, callback, sql, requiredType, values);
	}
	@SuppressWarnings("unchecked")
	public <T> void queryAll(boolean onlyEntity, Callback<T> callback, String sql, Class<T> requiredType, Object...values)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			ResultSetMetaData meta = result.getMetaData();

			T model = null;
			while(result.next())
			{
				
				if(requiredType == String.class)
				{
					String value =  result.getString(1);
					callback.execute((T)value);
				}
				else
				{
					if(onlyEntity)
					{
						if(model == null)
						{
							model = requiredType.newInstance();
						}
						else
						{
							Field[] fields = requiredType.getDeclaredFields();
							for(Field field : fields)
							{
								// 重置为空
								ResultSetUtils.safeSetFieldNull(model, field);
							}
						}
					}
					else
					{
						model = requiredType.newInstance();
					}
					JavaBeanField beanField = MyFieldFactory.getField(requiredType);
					int count = meta.getColumnCount();
					for(int i = 1; i <= count; i ++)
					{
						//String column = meta.getColumnName(i);
						//获取别名，如果没有设置别名，就按默认列
						String column = meta.getColumnLabel(i);
						Field field = beanField.getField(column);
						if(field != null) {
							ResultSetUtils.safeSetFieldValue(model, field, result, column);
						}
					}
					callback.execute(model);
				}
			} 
		} catch (com.alibaba.druid.pool.DataSourceClosedException e)
		{
			if(isDEV)
			{
				LOG.error("dataSource already closed, dev env ignored!");
			}
			else
			{
				LOG.error("read sql error:", e);
			}
		}
		catch (Exception e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
	}

	@Override
	public  void queryAllForMap(Callback<Map<String, Object>> callback, String sql, Object... values) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			ResultSetMetaData meta = result.getMetaData();
			while(result.next())
			{
				Map<String, Object> model = Maps.newHashMap();
				int count = meta.getColumnCount();
				for(int i = 1; i <= count; i ++)
				{
					//String column = meta.getColumnName(i);
					//获取别名，如果没有设置别名，就按默认列
					String column = meta.getColumnLabel(i);
					Object value = result.getObject(column);
					model.put(column, value);
				}
				callback.execute(model);
			}
		} catch (com.alibaba.druid.pool.DataSourceClosedException e)
		{
			if(isDEV)
			{
				LOG.error("dataSource already closed, dev env ignored!");
			}
			else
			{
				LOG.error("read sql error:", e);
			}
		}
		catch (Exception e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
	}

	@Override
	public long count(String sql, Object... values) {
		long count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			if(result.next()) {
				count = result.getLong(1);
			}
		} catch (SQLException e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
		return count;
	}

	@Override
	public BigDecimal bigDecimalCount(String sql, Object... values) {
		BigDecimal count = new BigDecimal(0);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			if(result.next()) {
				count = result.getBigDecimal(1);
			}
		} catch (SQLException e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
		return count;
	}
	
	public Map<String, Object> queryForMap(String sql, Object... values)
	{
		Map<String, Object> model = Collections.emptyMap();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			result = DBUtil.executeQuery(pstmt, values);
			ResultSetMetaData meta = result.getMetaData();
			int count = meta.getColumnCount();
			if(result.next())
			{
				model = new HashMap<String, Object>();
				for(int i = 1; i <= count; i ++)
				{
					String column = meta.getColumnName(i);
					Object value = result.getObject(column);
					model.put(column, value);
				}
			}
		} catch (SQLException e) {
			LOG.error("read sql error:", e);
		} finally
		{
			DBUtil.closeResultSet(result);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConnection(conn);
		}
		return model;
	}

	@Override
	public long executeInsert(String sql, Object... values) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				if(values != null){
					int len = values.length;
					for(int i = 0; i < len; i ++){
						ps.setObject(i+1, values[i]);
					}
				}
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}

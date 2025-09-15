package com.inso.framework.db.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class MyDruidDataSource implements DataSourceSupport{
	
	private static final int DEFAULT_JDBC_INIT_SIZE = 0;
	private static final int DEFAULT_JDBC_MIN_IDLE = 0;
	private static final int DEFAULT_JDBC_MAX_ACTIVE = 200;
	
	public static final int DEFAULT_JDBC_TIME_BETWEEN_EVICTION_RUN = 1000 * 60 * 2; // 空闲检测时间
	public static final int DEFAULT_JDBC_IDLE_MINUTE = 60 * 3; // 60 分钟, mysql 默认最大超时为8小时
	
	private static final Log LOG = LogFactory.getLog(MyDruidDataSource.class);
	
	private MyConfiguration mConf = null;
	private static Map<String, DruidDataSource> mapPool = new HashMap<String, DruidDataSource>();
	
	
	public MyDruidDataSource(MyConfiguration conf)
	{
		this.mConf = conf;
	}
	
	private void open(String name)
	{
		try {
			LOG.info("open druid, initconn = " + DEFAULT_JDBC_INIT_SIZE +
					", idleconn = " + DEFAULT_JDBC_MIN_IDLE + 
					", maxconn = " + DEFAULT_JDBC_MAX_ACTIVE);
			synchronized (MyDruidDataSource.class) {
				DruidDataSource pool = mapPool.get(name);
				if(pool == null)
				{
					String driver = mConf.getString(name + ".driver_class", "com.mysql.jdbc.Driver");
					String jdbcUrl = mConf.getString(name + ".url");
					String username = mConf.getString(name + ".username");
					String password = mConf.getString(name + ".password");
					
					Class.forName(driver);  
					
					pool = new DruidDataSource();
					pool.setUrl(jdbcUrl);
					pool.setUsername(username);
					pool.setPassword(password);
					pool.setInitialSize(DEFAULT_JDBC_INIT_SIZE); // 初始化连接
					pool.setMaxActive(DEFAULT_JDBC_MAX_ACTIVE);  // 最大连接
					pool.setMinIdle(DEFAULT_JDBC_MIN_IDLE); // 最小连接
					pool.setTimeBetweenEvictionRunsMillis(DEFAULT_JDBC_TIME_BETWEEN_EVICTION_RUN); // 空闲检测时间
					pool.setTestOnBorrow(true);
					pool.setTestOnReturn(false);
					pool.setTestWhileIdle(true);
					pool.setRemoveAbandoned(false); // 设置是否开启连接租期
					pool.setValidationQuery("select 1");
					//pool.setMaxWait(maxWaitMillis); // 获取连接最大等待时间
//					pool.restart();
					mapPool.put(name, pool);
				}
			}
			
		} catch (ClassNotFoundException e) {
			LOG.error("driver not found error:", e);
		} 
	}
	
	public Connection getConnection(String name) throws SQLException
	{
		DruidDataSource pool = mapPool.get(name);
		if(pool == null)
		{
			synchronized (MyDruidDataSource.class) {
				if(pool == null)
				{
					open(name);
				}
			}
			pool = mapPool.get(name);
		}
		return pool.getConnection();
	}
	
	public void destroy()
	{
		try {
			if(!mapPool.isEmpty())
			{
				Set<String> keys = mapPool.keySet();
				for(String key : keys)
				{
					DruidDataSource pool = mapPool.get(key);
					pool.close();
				}
			}
		} catch (Exception e) {
			LOG.error("close connection pool error:", e);
		} finally {
			mapPool.clear();
		}
	}
	
	@Override
	public DataSource getDataSource(String name) {
		DruidDataSource pool = mapPool.get(name);
		if(pool == null)
		{
			synchronized (MyDruidDataSource.class) {
				if(pool == null) open(name);
			}
			pool = mapPool.get(name);
		}
		return pool;
	}

	


}

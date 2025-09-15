package com.inso.framework.db;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MyDataSourceFactory {

	private static Log LOG = LogFactory.getLog(MyDataSourceFactory.class);
	private static MyConfiguration conf = MyConfiguration.getInstance();

	public static DataSource create(String name) {
		return getDruid(name);
//		return getHikariCP(name);
	}
	
	public static DataSource getDruid(String name) {
		try {
			// jdbc.master.driver_class
			// jdbc.master.url
			// jdbc.master.username
			// jdbc.master.password
			String driver = conf.getString(name + ".driver_class", "com.mysql.jdbc.Driver");
			String jdbcUrl = conf.getString(name + ".url");
			String username = conf.getString(name + ".username");
			String password = conf.getString(name + ".password");
			
			LOG.info("jdbcurl = " + jdbcUrl);

			Class.forName(driver);
			
			int maxActive = MyDBConfigManager.DEFAULT_JDBC_MAX_ACTIVE;
			

			DruidDataSource pool = new DruidDataSource();
			pool.setUrl(jdbcUrl);
			pool.setUsername(username);
			pool.setPassword(password);
			pool.setInitialSize(MyDBConfigManager.DEFAULT_JDBC_INIT_SIZE); // 初始化连接
			pool.setMaxActive(maxActive); // 最大连接
			pool.setMinIdle(MyDBConfigManager.DEFAULT_JDBC_MIN_IDLE); // 最小连接
			pool.setTimeBetweenEvictionRunsMillis(MyDBConfigManager.DEFAULT_JDBC_TIME_BETWEEN_EVICTION_RUN); // 空闲检测时间
			pool.setTestOnBorrow(true);
			pool.setTestOnReturn(false);
			pool.setTestWhileIdle(true);
			pool.setRemoveAbandoned(false); // 设置是否开启连接租期
			pool.setValidationQuery("select 1");

			return pool;
		} catch (ClassNotFoundException e) {
			LOG.error("init druid datasource error:", e);
		}
		return null;
	}
	
	private static DataSource getHikariCP(String name)
	{
		String driver = conf.getString(name + ".driver_class", "com.mysql.jdbc.Driver");
		String jdbcUrl = conf.getString(name + ".url");
		String username = conf.getString(name + ".username");
		String password = conf.getString(name + ".password");
		
		int maxActive = MyDBConfigManager.DEFAULT_JDBC_MAX_ACTIVE;
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setDriverClassName(driver);
		config.setMaximumPoolSize(maxActive); // 最大连接数
		config.setMinimumIdle(maxActive);
		config.setConnectionTimeout(1000);
		config.setConnectionInitSql("select 1");
		config.setIdleTimeout(MyDBConfigManager.DEFAULT_JDBC_TIME_BETWEEN_EVICTION_RUN); // 空闲超时：60秒
		
		if(name.startsWith("globaldb.master"))
		{
			config.setReadOnly(false);
		}
		else
		{
			config.setReadOnly(true);
		}
		
		DataSource ds = new HikariDataSource(config);
		
		return ds;
	}


}

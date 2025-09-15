package com.inso.framework.db.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public interface DataSourceSupport {

	public Connection getConnection(String name) throws SQLException;
	
	public DataSource getDataSource(String name);
	
	
}

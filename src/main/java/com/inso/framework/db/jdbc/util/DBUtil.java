package com.inso.framework.db.jdbc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringEscapeUtils;


public class DBUtil {
	
	public static int executeUpdate(Connection conn, String sql, Object... values) throws SQLException
	{
		int rs = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = executeUpdate(pstmt, values);
		} catch (SQLException e) {
			throw e;
		} finally {
			closeStatement(pstmt);
		}
		return rs;
	}
	
	public static ResultSet executeQuery(PreparedStatement pstmt, Object... values) throws SQLException
	{
		if(values != null && values.length > 0)
		{
			int len = values.length;
			for(int i = 0; i < len; i ++)
			{
				setValue(pstmt, i + 1, values[i]);
			}
		}
		return pstmt.executeQuery();
	}
	
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn = null;
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeStatement(Statement st)
	{
		try {
			if(st != null) {
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		st = null;
	}
	
	public static void rollback(Connection conn)
	{
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setAutoCommit(Connection conn, boolean autoCommit)
	{
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int executeUpdate(PreparedStatement pstmt, Object... values) throws SQLException
	{
		if(values != null && values.length > 0)
		{
			int len = values.length;
			for(int i = 0; i < len; i ++)
			{
				setValue(pstmt, i + 1, values[i]);
			}
		}
		return pstmt.executeUpdate();
	}
	
	
	private static void setValue(PreparedStatement pstmt, int index, Object value) throws SQLException
	{
		if(value instanceof String)
		{
			String valueString = escapeSql((String)value);
			pstmt.setObject(index, valueString);
		} else
		{
			pstmt.setObject(index, value);
		}
	}
	
	public static String escapeSql(String value)
	{
		String valueString = StringEscapeUtils.escapeSql((String)value);
		return valueString;
	}
	
	public static void main(String[] args)
	{
	}
	
}

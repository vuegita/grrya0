package com.inso.framework.db.jdbc.util;

import com.inso.framework.utils.StringUtils;

public class SimpleSQLBuilder {

	public static String insert(String table, String... keys)
	{
		String sql = "insert into " +  table + "(" + StringUtils.join(keys, ",") + ") " +
				     "values(" + StringUtils.join("?", ",", keys.length) + ")";
		return sql;
	}
	
	public static String buildDeleteSQL(String table, String... keys)
	{
		StringBuffer sb = new StringBuffer("delete from " + table + " where ");
		if(keys != null && keys.length > 0)
		{
			boolean first = true;
			String andString = " and ";
			for(String key : keys)
			{
				if(first) first = false; else sb.append(andString);
				sb.append(key);
			}
		} 
		return sb.toString();
	}
	
	public static String buildCountSQL(String table, String... keys)
	{
		StringBuffer buffer = new StringBuffer("select count(*) from " + table);
		if(keys != null && keys.length > 0)
		{
			buffer.append(" where ");
			boolean first = true;
			String andString = " and ";
			for(String key : keys)
			{
				if(first) first = false; else buffer.append(andString);
				buffer.append(key);
			}
		} 
		return buffer.toString();
	}
	
	public static void main(String[] args)
	{
		String[] insert = {"a", "b"};
		System.out.println("test insert SQL => " + SimpleSQLBuilder.insert("test_user", insert));
		
		String[] countStr = {"a = ?", "b = ?", "c = ?"};
		System.out.println("test count SQL => " + SimpleSQLBuilder.buildCountSQL("test_user", countStr));
		
		String[] deleteStr = {"a = ?", "b = ?", "c = ?"};
		System.out.println("test delete SQL => " + SimpleSQLBuilder.buildDeleteSQL("test_user", deleteStr));
	}
	
	
}

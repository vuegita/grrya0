package com.inso.framework.db.jdbc.util;

import com.inso.framework.utils.StringUtils;

public class UpdateSQLBuilder {
	
	private StringBuffer buffer = new StringBuffer();
	
	public static UpdateSQLBuilder builder()
	{
		UpdateSQLBuilder buider = new UpdateSQLBuilder();
		return buider;
	}
	
	public UpdateSQLBuilder update(String... tables)
	{
		buffer.append("update ").append(StringUtils.join(tables, ","));
		return this;
	} 
	
	public UpdateSQLBuilder set(String... columns)
	{
		buffer.append(" set " + StringUtils.join(columns, ", "));
		return this;
	}
	
	public UpdateSQLBuilder where(String... whereParames)
	{
		buffer.append(" where " + StringUtils.join(whereParames, " and "));
		return this;
	} 
	
	public String buider()
	{
		return buffer.toString();
	}

	public String toString()
	{
		return buider();
	}
	
	public static void main(String[] args)
	{
		UpdateSQLBuilder buider = new UpdateSQLBuilder();
		buider.update("user", "doc_file", "doc_file")
			  .set("u1 = ?", "pwd = ?")
			  .where("u1 = ?", "u2 = ?", "u3 = ?");
		System.out.println(buider.buider());
	}
	
}

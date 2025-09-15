package com.inso.framework.db.jdbc.util;

public class BaseSQLBuilder {
	
	protected StringBuffer buffer = new StringBuffer();
	
	protected void join(String split, String... columns)
	{
		int len = columns.length;
		for(int i=0;i<len;i++)
		{
			if( i > 0 )
				buffer.append(split);
			buffer.append(columns[i]);
		}
	}
	
}

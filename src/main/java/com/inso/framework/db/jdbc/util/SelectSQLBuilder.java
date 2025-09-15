package com.inso.framework.db.jdbc.util;

public class SelectSQLBuilder extends BaseSQLBuilder{
	
	public static SelectSQLBuilder builder()
	{
		SelectSQLBuilder buider = new SelectSQLBuilder();
		return buider;
	}
	
	public SelectSQLBuilder select(String... columns)
	{
		buffer.append("select ");
		join(",", columns);
		return this;
	} 
	
	public SelectSQLBuilder from(String... tables)
	{
		buffer.append(" from ");
		join(",", tables);
		return this;
	}
	
	public SelectSQLBuilder leftJoin(String leftJoin)
	{
		buffer.append(" left join " + leftJoin);
		return this;
	}
	
	public SelectSQLBuilder innerJoin(String innerJoin)
	{
		buffer.append(" left join " + innerJoin);
		return this;
	}
	
	public SelectSQLBuilder where(String... whereParames)
	{
		buffer.append(" where ");
		join(" and ", whereParames);
		return this;
	} 
	
	public SelectSQLBuilder orderby(String... columns)
	{
		buffer.append(" order by ");
		join(",", columns);
		return this;
	} 
	
	public SelectSQLBuilder groupby(String... columns)
	{
		buffer.append(" group by ");
		join(",", columns);
		return this;
	}

	public SelectSQLBuilder limit(int index,int size)
	{
		buffer.append(" limit ");
		join(",",index+",");
		join(",",String.valueOf(size));
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
		SelectSQLBuilder buider = new SelectSQLBuilder();
		buider.select("username", "pwd")
			  .from("user", "doc_file")
			  .where("u1 = ?", "u2 = ?", "u3 = ?")
			  .groupby("test")
			  .orderby("username desc", "id desc");
		System.out.println(buider.buider());
	}
	
}

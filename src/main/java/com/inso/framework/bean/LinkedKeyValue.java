package com.inso.framework.bean;

public class LinkedKeyValue {

	private int index = 0;
	
	private String[] keys;
	private Object[] values;
	
	public static LinkedKeyValue buildWithCapacity(int capacity)
	{
		return new LinkedKeyValue(capacity);
	}
	
	private LinkedKeyValue(int capacity)
	{
		this.keys = new String[capacity];
		this.values = new Object[capacity];
	}
	
	public LinkedKeyValue put(String key, Object value)
	{
		keys[index] = key;
		values[index] = value;
		index ++;
		return this;
	}
	
	public String[] getKeys()
	{
		return keys;
	}
	
	public Object[] getValues()
	{
		return values;
	}
	
	public int size()
	{
		return index;
	}
	
	public boolean isEmpty()
	{
		return index == 0;
	}
	
}

package com.inso.framework.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;


public class CollectionUtils {
	
	private static final Object[] mEmptyObjectArray = {};

	public static Object[] emptyObjectArray()
	{
		return mEmptyObjectArray;
	}
	
	public static Map<String, Object> asHashMap(Map<Integer, Object> input)
	{
		if(input == null || input.isEmpty()) return Collections.emptyMap();
		Map<String, Object> temp = new HashMap<String, Object>();
		for(Integer key : input.keySet())
		{
			temp.put(key + "", input.get(key));
		}
		return temp;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List map2List(Map<String, ?> maps)
	{
		if(maps == null || maps.isEmpty()) return Collections.emptyList();
		
		List list = Lists.newArrayList();
		for(String key : maps.keySet())
		{
			list.add(maps.get(key));
		}
		return list;
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}


	
}

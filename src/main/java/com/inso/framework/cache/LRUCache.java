package com.inso.framework.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = 1L;
	
	private final int MAX_CACHE_SIZE;

    public LRUCache(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {
        return size() > MAX_CACHE_SIZE;
    }
	
}

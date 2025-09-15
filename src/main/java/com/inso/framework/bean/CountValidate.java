package com.inso.framework.bean;


import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.StringUtils;

public class CountValidate {

    private String key;
    private int maxCount;
    private int currentCount;
    private int expires;

    public CountValidate(String key, int maxCount, int expires) {
        this.key = key;
        this.maxCount = maxCount;
        this.expires = expires;
    }

    public void loadStatus() {
        CacheManager mCache = CacheManager.getInstance();
        String value = mCache.getString(key);
        if (!StringUtils.isEmpty(value)) {
            this.currentCount = Integer.valueOf(value);
        }
    }

    public void saveStatus() {
        CacheManager mCache = CacheManager.getInstance();
        mCache.setString(key, String.valueOf(currentCount), expires);
    }
    
    public void increCurrent()
    {
    	this.currentCount ++;
    }

    public boolean check() {
        if (currentCount <= maxCount) {
            return true;
        }
        return false;
    }


}

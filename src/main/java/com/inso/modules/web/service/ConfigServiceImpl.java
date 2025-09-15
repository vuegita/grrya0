package com.inso.modules.web.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.cache.WebCacheHelper;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.dao.ConfigDao;

@Service
public class ConfigServiceImpl extends DaoSupport implements ConfigService {

	
	@Autowired
	private ConfigDao mConfigDao;

    public void addConfig(String key, String value)
    {
    	mConfigDao.addConfig(key, value);
    }
    
    @Override
    public void updateValue(String key, String value)
    {
    	mConfigDao.updateValue(key, value);
    	String cachekey = WebCacheHelper.getConfigCacheKey(key);
    	CacheManager.getInstance().delete(cachekey);
    }
    @Override
    public List<ConfigKey> findByList(boolean purge, String keyPrefix){
    	String cachekey = WebCacheHelper.queryConfigListCacheKey(keyPrefix);
    	List<ConfigKey> list = CacheManager.getInstance().getList(cachekey, ConfigKey.class);
    	if(purge || CollectionUtils.isEmpty(list))
    	{
    		list = mConfigDao.findByList(keyPrefix);
    		if(CollectionUtils.isEmpty(list))
    		{
    			list = Collections.emptyList();
    		}
    		CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
    	}
        return list;
    }

    @Override
    public ConfigKey findByKey(boolean purge, String key)
    {
    	String cachekey = WebCacheHelper.getConfigCacheKey(key);
    	ConfigKey config = CacheManager.getInstance().getObject(cachekey, ConfigKey.class);
    	if(purge || config == null)
    	{
    		config = mConfigDao.findByKey(key);
    		if(config != null)
    		{
    			CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(config), CacheManager.EXPIRES_WEEK);
    		}
    	}
        return config;
    }

    @Override
    public String getValueByKey(boolean purge, String key) {
        ConfigKey configKey = findByKey(purge, key);
        if (configKey != null){
            return configKey.getValue();
        }
        return null;
    }

    @Override
    public Map<String, String> findMaps(String prefix) {
        Map<String, String> result = Maps.newHashMap();
        List<ConfigKey> list = findByList(false, prefix);
        if (CollectionUtils.isEmpty(list))return result;

        list.forEach(configKey -> {
            result.put(configKey.getKey(), configKey.getValue());
        });

        return result;
    }

    @Override
    public float getFloat(boolean purge, String key) {
        String value = getValueByKey(false, key);
        return StringUtils.asFloat(value);
    }

    @Override
    public long getLong(boolean purge, String key) {
        String value = getValueByKey(false, key);
        return StringUtils.asLong(value);
    }

    @Override
    public int getInt(boolean purge, String key) {
        String value = getValueByKey(false, key);
        return StringUtils.asInt(value);
    }

    @Override
    public BigDecimal getBigDecimal(boolean purge, String key) {
        String value = getValueByKey(false, key);
        return StringUtils.asBigDecimal(value);
    }

    @Override
    public boolean getBoolean(boolean purge, String key) {
        String value = getValueByKey(false, key);
        return StringUtils.asBoolean(value);
    }


}

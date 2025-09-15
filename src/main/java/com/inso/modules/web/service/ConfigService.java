package com.inso.modules.web.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.inso.modules.web.model.ConfigKey;

public interface ConfigService {
	
	public void addConfig(String key, String value);
    /**
     * 更新配置
     * @param key
     * @param value
     */
    public void updateValue(String key, String value);
    List<ConfigKey> findByList(boolean purge, String keyPrefix);
    ConfigKey findByKey(boolean purge, String key);

    String getValueByKey(boolean purge, String key);

    Map<String, String> findMaps(String prefix);


    public float getFloat(boolean purge, String key);
    public long getLong(boolean purge, String key);
    public int getInt(boolean purge, String key);
    public BigDecimal getBigDecimal(boolean purge, String key);
    public boolean getBoolean(boolean purge, String key);


}

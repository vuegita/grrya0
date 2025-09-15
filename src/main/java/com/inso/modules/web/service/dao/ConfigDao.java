package com.inso.modules.web.service.dao;

import java.util.List;

import com.inso.modules.web.model.ConfigKey;

public interface ConfigDao {
	
	public void addConfig(String key, String value);
    public void updateValue(String key, String value);
    List<ConfigKey> findByList(String keyPrefix);
    ConfigKey findByKey(String key);

}

package com.inso.modules.web.service.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.modules.web.model.ConfigKey;

@Repository
public class ConfigDaoMysql extends DaoSupport implements ConfigDao {
	
	public void addConfig(String key, String value)
	{
		LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
		keyValues.put("config_key", key);
		keyValues.put("config_value", value);
		persistent("inso_config", keyValues);
	}

    @Override
    public void updateValue(String key, String value)
    {
        String sql = "update inso_config set config_value = ? where config_key = ?";
        mWriterJdbcService.executeUpdate(sql,value,key);
    }
    @Override
    public List<ConfigKey> findByList(String keyPrefix){

        String sql = "select config_key,config_value  from inso_config where config_key like '"+keyPrefix+"%'";
        return mSlaveJdbcService.queryForList(sql,ConfigKey.class);
    }

    @Override
    public ConfigKey findByKey(String key)
    {
        String sql = "select config_key,config_value from inso_config where config_key = ?";
        return mSlaveJdbcService.queryForObject(sql,ConfigKey.class,key);
    }
    
}

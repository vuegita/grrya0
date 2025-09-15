package com.inso.framework.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.inso.framework.db.MyDBConfigManager;
import com.inso.framework.db.MyDataSourceFactory;
import com.inso.framework.db.jdbc.JdbcService;
import com.inso.framework.db.jdbc.impl.MyJdbcServiceImpl;

@Configuration
public class DataSourceConfig {
	
	@Bean
	@Primary
	@Qualifier(MyDBConfigManager.DB_GLOBAL_MASTER)
	public DataSource gMasterDataSourceBean() {
	    return MyDataSourceFactory.create(MyDBConfigManager.DB_GLOBAL_MASTER);
	}

	@Bean
	@Primary
    public PlatformTransactionManager bfTransactionManager(@Qualifier(MyDBConfigManager.DB_GLOBAL_MASTER)DataSource prodDataSource) {
     return new DataSourceTransactionManager(prodDataSource);
     //return new DynamicDataSourceTransactionManager(prodDataSource);
    }

	@Bean(name = "masterJdbcTemplate")
	@Primary
    public JdbcTemplate gMasterJdbcTemplate(@Qualifier(MyDBConfigManager.DB_GLOBAL_MASTER) DataSource ds)
    {
        return new JdbcTemplate(ds, true);
    }

	@Bean(name = "masterJdbcService")
	@Primary
    public JdbcService masterJdbcService(@Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate)
    {
        return new MyJdbcServiceImpl(jdbcTemplate);
    }

	@Bean
	@Qualifier(MyDBConfigManager.DB_GLOBAL_SLAVE)
	public DataSource gSlaveDataSourceBean() {
	    return MyDataSourceFactory.create(MyDBConfigManager.DB_GLOBAL_SLAVE);
	}

	@Bean(name = "mSlaveJdbcTemplate")
    public JdbcTemplate gSlaveJdbcTemplate(@Qualifier(MyDBConfigManager.DB_GLOBAL_SLAVE) DataSource ds)
    {
        return new JdbcTemplate(ds, true);
    }

	@Bean(name = "slaveJdbcService")
    public JdbcService slaveJdbcService(@Qualifier("mSlaveJdbcTemplate") JdbcTemplate jdbcTemplate)
    {
        return new MyJdbcServiceImpl(jdbcTemplate);
    }

}

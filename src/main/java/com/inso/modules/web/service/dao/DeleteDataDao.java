package com.inso.modules.web.service.dao;

import org.joda.time.DateTime;

public interface DeleteDataDao {
	

    public void deleteAllByTime(String table, String timeField, DateTime dateTime);
    
}

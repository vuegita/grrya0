package com.inso.modules.web.service;

import org.joda.time.DateTime;

public interface DeleteDataService {
	

    public void deleteAllByTime(String table, String timeField, DateTime dateTime);
    
}

package com.inso.modules.web.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.modules.web.service.dao.DeleteDataDao;

@Service
public class DeleteDataServiceImpl  implements DeleteDataService{

    @Autowired
    private DeleteDataDao mDeleteDataDao;

    @Override
    public void deleteAllByTime(String table, String timeField, DateTime dateTime) {
        mDeleteDataDao.deleteAllByTime(table, timeField, dateTime);
    }
}

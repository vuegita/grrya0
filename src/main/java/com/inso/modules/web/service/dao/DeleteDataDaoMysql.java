package com.inso.modules.web.service.dao;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;

@Repository
public class DeleteDataDaoMysql extends DaoSupport implements DeleteDataDao {


    public void deleteAllByTime(String table, String timeField, DateTime dateTime)
    {
        String sql = "delete from " + table + " where " + timeField + " <= ?";
        mWriterJdbcService.executeUpdate(sql, dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
    }
    
}

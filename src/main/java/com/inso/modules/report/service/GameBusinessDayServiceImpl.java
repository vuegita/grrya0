package com.inso.modules.report.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.report.model.GameBusinessDay;
import com.inso.modules.report.service.dao.GameBusinessDayDao;

@Service
public class GameBusinessDayServiceImpl implements GameBusinessDayService{

    private LRUCache<String, String> mLRUCache = new LRUCache<>(500);

    @Autowired
    private GameBusinessDayDao mGameBusinessDayDao;

    @Override
    @Transactional
    public void updateLog(Date pdate, long agentid, String agentname, long staffid, String staffname, BusinessType businessType, BigDecimal betAmount, long betCount, BigDecimal feemoney, BigDecimal winAmount, long winCount) {
        initReport(pdate, agentid, agentname, staffid, staffname, businessType);
        mGameBusinessDayDao.updateReport(pdate, agentid, staffid, businessType, betAmount, betCount, feemoney, winAmount, winCount);
    }

    @Override
    public void delete(Date pdate, long agentid, long staffid, BusinessType businessType) {
        mGameBusinessDayDao.delete(pdate, agentid, staffid, businessType);
    }

    @Override
    public void queryAllStaff(String begintTime, String endTime, Callback<GameBusinessDay> callback) {
        mGameBusinessDayDao.queryAllStaff(begintTime, endTime,callback);
    }

    @Override
    public RowPager<GameBusinessDay> queryScrollPage(PageVo pageVo, long agentid, long staffid, BusinessType businessType) {
        return mGameBusinessDayDao.queryScrollPage(pageVo, agentid, staffid, businessType);
    }


    private void initReport(Date pdate, long agentid, String agentname, long staffid, String staffname, BusinessType businessType)
    {
        DateTime dateTime = new DateTime(pdate);
        String key = agentid + businessType.getKey() + staffid + dateTime.getDayOfYear();
        if(mLRUCache.containsKey(key))
        {
            return;
        }
        try {
            mGameBusinessDayDao.addReport(pdate, agentid, agentname, staffid, staffname, businessType);
            mLRUCache.put(key, StringUtils.getEmpty());
        } catch (Exception e) {
        }
    }
}

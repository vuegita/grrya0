package com.inso.modules.report.service;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.report.model.GameBusinessDay;

public interface GameBusinessDayService {

    public void updateLog(Date pdate, long agentid, String agentname, long staffid, String staffname, BusinessType businessType, BigDecimal betAmount, long betCount, BigDecimal feemoney, BigDecimal winAmount, long winCount);
    public void delete(Date pdate, long agentid, long staffid, BusinessType businessType);

    public void queryAllStaff(String begintTime, String endTime, Callback<GameBusinessDay> callback);
    public RowPager<GameBusinessDay> queryScrollPage(PageVo pageVo, long agentid, long staffid, BusinessType businessType);
}

package com.inso.modules.report.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.report.model.BusinessReportType;
import com.inso.modules.report.model.BusinessV2Report;
import com.inso.modules.report.model.StatsDimensionType;

import java.util.Date;

public interface BusinessDayV2Dao {


    public void addReport(Date pdate, BusinessV2Report report, RemarkVO remarkVO);
    public void delete(Date pdate, long agentid, long staffid, BusinessReportType businessType, String businessExternalid);
    public RowPager<BusinessV2Report> queryScrollPage(PageVo pageVo, long agentid, long staffid, CryptoCurrency currencyType, StatsDimensionType dimensionType, BusinessReportType businessType, String businessExternalid);

}

package com.inso.modules.report.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.report.model.BusinessV2Report;
import com.inso.modules.report.model.BusinessReportType;
import com.inso.modules.report.model.StatsDimensionType;
import com.inso.modules.report.service.dao.BusinessDayV2Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BusinessV2ServiceImpl implements BusinessV2Service {

    @Autowired
    private BusinessDayV2Dao mStaffBusinessDayDao;

    @Override
    public void addReport(Date pdate, BusinessV2Report report, RemarkVO remarkVO) {
        mStaffBusinessDayDao.addReport(pdate, report, remarkVO);
    }

    @Override
    public void delete(Date pdate, long agentid, long staffid, BusinessReportType businessType, String businessExternalid) {
        try {
            mStaffBusinessDayDao.delete(pdate, agentid, staffid, businessType, businessExternalid);
        } catch (Exception e) {
        }
    }

    @Override
    public RowPager<BusinessV2Report> queryScrollPage(PageVo pageVo, long agentid, long staffid, CryptoCurrency currencyType, StatsDimensionType dimensionType, BusinessReportType businessType, String businessExternalid) {
        return mStaffBusinessDayDao.queryScrollPage(pageVo, agentid, staffid, currencyType, dimensionType, businessType, businessExternalid);
    }
}

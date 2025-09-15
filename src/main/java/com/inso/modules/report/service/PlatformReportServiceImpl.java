package com.inso.modules.report.service;

import java.util.Date;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.PlatformReport;
import com.inso.modules.report.service.dao.PlatformReportDao;

@Service
public class PlatformReportServiceImpl implements PlatformReportService {

    @Autowired
    private PlatformReportDao mPlatformReportDao;

    @Override
    public void addReport(PlatformReport report) {
        mPlatformReportDao.addReport(report);
    }

    @Override
    public void delete(Date pdate, FundAccountType accountType, ICurrencyType currencyType) {
        mPlatformReportDao.delete(pdate, accountType, currencyType);
    }

    @Override
    public void queryAll(Callback<PlatformReport> callback) {
        mPlatformReportDao.queryAll(callback);
    }

    @Override
    public RowPager<PlatformReport> queryScrollPage(PageVo pageVo) {
        return mPlatformReportDao.queryScrollPage(pageVo);
    }
}

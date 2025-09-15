package com.inso.modules.report.service;

import java.util.Date;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.report.model.PlatformReport;

public interface PlatformReportService {

    public void addReport(PlatformReport report);

    public void delete(Date pdate, FundAccountType accountType, ICurrencyType currencyType);

    public void queryAll(Callback<PlatformReport> callback);
    public RowPager<PlatformReport> queryScrollPage(PageVo pageVo);
}

package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.SalesReport;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;
import java.util.Date;

public interface MerchantSalesReportService {


    public void addReport(UserAttr userAttr, BigDecimal totalAmount, long totalCount, BigDecimal refundAmount, long refundCount, BigDecimal returnAmount, Date pdate);
    public void delete(long userid, Date pdate);


    public RowPager<SalesReport> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid);



}

package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.SalesReport;
import com.inso.modules.ad.mall.service.dao.MerchantSalesReportDao;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class MerchantSalesReportServiceImpl implements MerchantSalesReportService{

    @Autowired
    private MerchantSalesReportDao merchantSalesReportDao;

    @Override
    public void addReport(UserAttr userAttr, BigDecimal totalAmount, long totalCount, BigDecimal refundAmount, long refundCount, BigDecimal returnAmount, Date pdate) {
        merchantSalesReportDao.addReport(userAttr, totalAmount, totalCount, refundAmount, refundCount, returnAmount, pdate);
    }

    @Override
    public void delete(long userid, Date pdate) {
        merchantSalesReportDao.delete(userid, pdate);
    }

    @Override
    public RowPager<SalesReport> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid) {
        return merchantSalesReportDao.queryScrollPage(pageVo, agentid, staffid, userid);
    }
}

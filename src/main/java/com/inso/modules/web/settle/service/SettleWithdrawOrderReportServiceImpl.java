package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONArray;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.service.dao.SettleWithdrawOrderReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettleWithdrawOrderReportServiceImpl implements SettleWithdrawOrderReportService{

    @Autowired
    private SettleWithdrawOrderReportDao settleWithdrawOrderReportDao;

    @Autowired
    private SettleOrderService settleOrderService;

    @Autowired
    private SettleWithdrawReportService settleWithdrawReportService;



    @Override
    @Transactional
    public void addOrder(List<SettleOrderInfo> list, JSONArray remark) {
        long reportid = settleWithdrawReportService.addOrder(list.get(0),  remark);
        for(SettleOrderInfo model : list){
            settleOrderService.updateTxStatus(model.getNo(), OrderTxStatus.getType(model.getStatus()), null, model.getChecker(), null,OrderTxStatus.REALIZED );

            settleWithdrawOrderReportDao.addOrder(model.getNo(), reportid);
        }

    }

    @Override
    public void delete(long id) {
        settleWithdrawOrderReportDao.delete(id);
    }

//    @Override
//    public RowPager<SettleRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ICurrencyType currencyType, SettleBusinessType businessType, String dimensionType) {
//        return settleRecordDao.queryScrollPage(pageVo, agentid, staffid, currencyType, businessType, dimensionType);
//    }
}

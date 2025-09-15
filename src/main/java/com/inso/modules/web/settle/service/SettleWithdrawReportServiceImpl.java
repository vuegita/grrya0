package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;
import com.inso.modules.web.settle.service.dao.SettleWithdrawReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettleWithdrawReportServiceImpl implements SettleWithdrawReportService{

    @Autowired
    private SettleWithdrawReportDao settleWithdrawReportDao;


    @Override
    public long addOrder(SettleOrderInfo recordInfo, JSONArray remark) {
       return settleWithdrawReportDao.addOrder(recordInfo, remark);
    }

    @Override
    public void delete(long id) {
        settleWithdrawReportDao.delete(id);
    }

    @Override
    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid){
        return settleWithdrawReportDao.queryScrollPagequeryScrollPage(pageVo,agentid);
    }


}

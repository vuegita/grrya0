package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;


public interface SettleWithdrawOrderReportDao {

    public void addOrder( String settleOrderNo,long reportid);

    public void delete(long id);
//    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid);

}

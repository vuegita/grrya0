package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;

public interface SettleWithdrawReportService {

    public long addOrder(SettleOrderInfo recordInfo, JSONArray remark);

    public void delete(long id);

    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid);

}

package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleRecordInfo;

import java.util.Date;
import java.util.List;

public interface SettleWithdrawOrderReportService {

    public void addOrder(List<SettleOrderInfo> list, JSONArray remark);

    public void delete(long id);
//    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid);

}

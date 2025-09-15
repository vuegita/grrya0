package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleRecordInfo;

import java.math.BigDecimal;
import java.util.Date;

public interface SettleRecordDao  {

    public void addOrder(Date pdate, SettleBusinessType businessType, SettleRecordInfo recordInfo, JSONObject remark);

    public void delete(Date pdate, SettleBusinessType businessType, long agentid, long staffid, ICurrencyType currencyType);
    public RowPager<SettleRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ICurrencyType currencyType, SettleBusinessType businessType, String dimensionType);

}

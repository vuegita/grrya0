package com.inso.modules.passport.money.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

public interface MoneyOrderDao {

    public void addOrder(FundAccountType accountType, ICurrencyType currencyTypem, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, BusinessType businessType, MoneyOrderType moneyOrderType, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, Date createtime, JSONObject remark);
    public void updateTxStatus(String outTradeNo, OrderTxStatus txStatus, BigDecimal balance);

    public BigDecimal findDateTime(long userid, DateTime dateTime, ICurrencyType currencyType);
    public MoneyOrder findByTradeNo(String no, MoneyOrderType moneyOrderType);

    public void queryAllMemberOrder(String startTime, String endTime, Callback<MoneyOrder> callback);
    public RowPager<MoneyOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                String systemOrderno, String outTradeno,
                                                ICurrencyType currencyType,
                                                MoneyOrderType orderType, OrderTxStatus txStatus);

    public long countByUserid(long userid);

    public List<MoneyOrder> queryScrollPageByUser(PageVo pageVo, long userid);

    public long countActive(DateTime fromTime, DateTime toTime);
}

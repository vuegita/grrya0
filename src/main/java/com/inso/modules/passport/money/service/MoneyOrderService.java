package com.inso.modules.passport.money.service;

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
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.money.model.UserMoney;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MoneyOrderService {


    public void createOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, BusinessType businessType, MoneyOrderType moneyOrderType, BigDecimal amount, BigDecimal feemoney, Date createtime, JSONObject remark);
    public void updateToRealized(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, MoneyOrderType orderType, String outTradeNo, UserInfo userInfo,
                                 BigDecimal amount, BigDecimal feemoney, BigDecimal newBalance, boolean upCodeValue, Date createtime, UserMoney userMoney, BigDecimal totalDeductCodeAmount);
    public void updateToError(String outTradeNo);
    public MoneyOrder findByTradeNo(String no, MoneyOrderType moneyOrderType);
    public BigDecimal findDateTime(boolean purge, int periodHour, long userid);

    public void queryAllMemberOrder(String startTime, String endTime, Callback<MoneyOrder> callback);
    public RowPager<MoneyOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                String systemOrderno, String outTradeno,
                                                ICurrencyType currencyType,
                                                MoneyOrderType orderType, OrderTxStatus txStatus);

    public RowPager<MoneyOrder> queryScrollPageByLongUsername(boolean purge,PageVo pageVo, long userid, long agentid, long staffid,
                                                              String systemOrderno, String outTradeno,
                                                              ICurrencyType currencyType,
                                                              MoneyOrderType orderType, OrderTxStatus txStatus);
    /**
     * 查看用户总记录，变更会员上级、下级
     * @param userid
     * @return
     */
    public long countByUserid(long userid);

    public List<MoneyOrder> queryScrollPageByUser(PageVo pageVo, long userid);

    public long countActive(boolean purge, int fromDays);

    public void clearUserQueryPageCache(long userid);
}

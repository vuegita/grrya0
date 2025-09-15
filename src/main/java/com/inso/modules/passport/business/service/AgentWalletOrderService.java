package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.paychannel.model.ChannelInfo;

import java.math.BigDecimal;

public interface AgentWalletOrderService {

    public String addOrder(BusinessType businessType, String outTradeNo, UserInfo userInfo, ChannelInfo channelInfo,
                         BigDecimal amount, BigDecimal feemoney, ICurrencyType currencyType, BigDecimal realAmount);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark, String outTradeNo, BusinessType businessType);

    public AgentWalletOrderInfo findByOutTradeNo(boolean purge, String outTradeNo, BusinessType businessType);

    public void queryAll(String startTimeString, String endTimeString, Callback<AgentWalletOrderInfo> callback);
    public RowPager<AgentWalletOrderInfo> queryScrollPage(PageVo pageVo, long userid, String systemNo, String outTradeNo, OrderTxStatus txStatus, BusinessType businessType);

}

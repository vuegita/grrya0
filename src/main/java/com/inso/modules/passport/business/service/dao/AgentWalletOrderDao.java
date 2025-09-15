package com.inso.modules.passport.business.service.dao;

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

public interface AgentWalletOrderDao {

    public void addOrder(String orderno, BusinessType businessType, String outTradeNo, UserInfo userInfo, ChannelInfo channelInfo, ICurrencyType currencyType,
                         BigDecimal amount, BigDecimal feemoney, OrderTxStatus txStatus, BigDecimal realAmount, RemarkVO remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);

    public AgentWalletOrderInfo findByNo(String orderno);

    public AgentWalletOrderInfo findByOutTradeNo(String outTradeNo, BusinessType businessType);

    public void queryAll(String startTimeString, String endTimeString, Callback<AgentWalletOrderInfo> callback);
    public RowPager<AgentWalletOrderInfo> queryScrollPage(PageVo pageVo, long userid, String systemNo, String outTradeNo, OrderTxStatus txStatus, BusinessType businessType);

}

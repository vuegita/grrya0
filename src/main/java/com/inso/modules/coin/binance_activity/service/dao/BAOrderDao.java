package com.inso.modules.coin.binance_activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;

public interface BAOrderDao {

    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         ICurrencyType currency,
                         BAOrderInfo.OrderType orderType,
                         BigDecimal totalAmount, BigDecimal feemoney);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public BAOrderInfo findById(String orderno);
    public RowPager<BAOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoCurrency currency, OrderTxStatus status);



}

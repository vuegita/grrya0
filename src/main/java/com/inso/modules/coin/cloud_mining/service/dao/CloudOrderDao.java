package com.inso.modules.coin.cloud_mining.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;

public interface CloudOrderDao {

    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         CloudProductType productType, ICurrencyType currency,
                         CloudOrderInfo.OrderType orderType,
                         BigDecimal totalAmount, BigDecimal feemoney, JSONObject jsonObject);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public CloudOrderInfo findById(String orderno);

    public RowPager<CloudOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoCurrency currency, OrderTxStatus status, CloudProductType productType , CloudOrderInfo.OrderType orderType);



}

package com.inso.modules.coin.defi_mining.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface MiningOrderDao  {

    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         CryptoNetworkType networkType, ICurrencyType currency,
                         MiningOrderInfo.OrderType orderType,
                         BigDecimal totalAmount, BigDecimal feemoney);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public MiningOrderInfo findById(String orderno);
    public void deleteById(String orderno);
    public long countByDatetime(long userid, DateTime dateTime);

    public BigDecimal sumAmount(long userid, MiningOrderInfo.OrderType orderType, CryptoNetworkType networkType, ICurrencyType currency);

    public RowPager<MiningOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<MiningOrderInfo> callback);


}

package com.inso.modules.coin.withdraw.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.coin.withdraw.model.CoinWithdrawOrderInfo;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface CoinWithdrawOrderDao {


    public void addOrder(String orderno, UserAttr userAttr,
                         CoinWithdrawChannel channelInfo,
                         OrderTxStatus txStatus,
                         BusinessType businessType, CryptoCurrency currency,
                         String toAddress, BigDecimal amount, BigDecimal feemoney);

    public void deleteByNo(String orderno);
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public CoinWithdrawOrderInfo findById(String orderno);

    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<CoinWithdrawOrderInfo> callback);
    public RowPager<CoinWithdrawOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status);

}

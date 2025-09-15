package com.inso.modules.coin.binance_activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;

public interface BAOrderService {

    public String addOrder(UserAttr userAttr,
                           ICurrencyType currency,
                           BAOrderInfo.OrderType orderType,
                           BigDecimal amount, BigDecimal feemoney);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public void updateToRealizedAndNewRewardAmount(String orderno, BAOrderInfo recordInfo, BigDecimal newTotalRewardAmount);

    public RowPager<BAOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoCurrency currency, OrderTxStatus status);

    public RowPager<BAOrderInfo> queryScrollPageByUser(boolean purge,PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status);

}

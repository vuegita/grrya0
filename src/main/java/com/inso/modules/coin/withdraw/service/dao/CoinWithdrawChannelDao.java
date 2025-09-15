package com.inso.modules.coin.withdraw.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;

public interface CoinWithdrawChannelDao   {

    public void add(String key, MyDimensionType dimensionType, String triggerPrivateKey, String triggerAddress, CryptoNetworkType networkType,
                    BigDecimal gasLimit,
                    BigDecimal feeRate, BigDecimal singleFeemoney, Status status);

    public CoinWithdrawChannel findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType);
    public CoinWithdrawChannel findById(long id);
    public void updateInfo(long id, String triggerPrivateKey, String triggerAddress, BigDecimal gasLimit, BigDecimal feeRate, BigDecimal singleFeemoney, Status status);
    public RowPager<CoinWithdrawChannel> queryScrollPage(PageVo pageVo, String key, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType);

}

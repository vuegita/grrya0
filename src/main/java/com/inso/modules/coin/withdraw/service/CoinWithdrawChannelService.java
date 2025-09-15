package com.inso.modules.coin.withdraw.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;

public interface CoinWithdrawChannelService {

    public void add(String key, String triggerPrivateKey, String triggerAddress, CryptoNetworkType networkType,
                    BigDecimal gasLimit,
                    BigDecimal feeRate, BigDecimal singleFeemoney);

    public CoinWithdrawChannel findByKey(boolean purge, long agentid, CryptoNetworkType networkType);
    public CoinWithdrawChannel findById(long id, boolean clearTriggerPrivateKey);
    public void updateInfo(CoinWithdrawChannel channelInfo, String triggerPrivateKey, String triggerAddress, BigDecimal gasLimit, BigDecimal feeRate, BigDecimal singleFeemoney, Status status);
    public RowPager<CoinWithdrawChannel> queryScrollPage(PageVo pageVo, String key, CryptoNetworkType networkType, Status status);

}

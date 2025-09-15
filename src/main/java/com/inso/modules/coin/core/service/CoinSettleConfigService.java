package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinSettleConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;

public interface CoinSettleConfigService {

    public void add(String key, MyDimensionType dimensionType, String address, CryptoNetworkType networkType, BigDecimal shareRatio, Status status);
    public void deleteByid(CoinSettleConfig settleConfig);

    public void updateInfo(CoinSettleConfig settleConfig, String receivAddress, BigDecimal shareRatio, Status status);

    public CoinSettleConfig findByProjectOrPlatformConfig(boolean purge, CryptoNetworkType networkType, MyDimensionType dimensionType);
    public CoinSettleConfig findByKey(boolean purge, String key, CryptoNetworkType networkType, MyDimensionType dimensionType);
    public CoinSettleConfig findById(long id);
    public RowPager<CoinSettleConfig> queryScrollPage(PageVo pageVo, String agentname, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType);
}

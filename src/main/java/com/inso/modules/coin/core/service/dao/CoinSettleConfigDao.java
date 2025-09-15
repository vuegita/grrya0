package com.inso.modules.coin.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinSettleConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;

public interface CoinSettleConfigDao  {

    public void add(String key, MyDimensionType dimensionType, String address, CryptoNetworkType networkType, BigDecimal shareRatio, Status status);
    public void deleteByid(long id);
    public CoinSettleConfig findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType);
    public CoinSettleConfig findById(long id);

    public void updateInfo(long id, String receivAddress, BigDecimal shareRatio, Status status);

    public RowPager<CoinSettleConfig> queryScrollPage(PageVo pageVo, String agentname, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType);
}

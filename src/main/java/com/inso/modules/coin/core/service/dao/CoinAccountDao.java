package com.inso.modules.coin.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;

public interface CoinAccountDao {

    public void add(long userid, String username, String address, CryptoNetworkType networkType);

    public void updateCreateTime(long userid, String address, CryptoNetworkType networkType);
    public void updateNewAddress(String oldAddress, String newAddress, CryptoNetworkType networkType);

    public CoinAccountInfo findByAddress(String address, CryptoNetworkType networkType);

    public CoinAccountInfo findByUserId(long id);

    public void deleteAddress(String address);

    public RowPager<CoinAccountInfo> queryScrollPage(PageVo pageVo, long userid, String address, CryptoNetworkType networkType, Status status, long agentid, long staffid);

}

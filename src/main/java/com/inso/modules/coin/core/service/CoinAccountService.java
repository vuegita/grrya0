package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;

public interface CoinAccountService {

    public void add(long userid, String username, String address, CryptoNetworkType networkType);

    public void updateCreateTime(long userid, String address, CryptoNetworkType networkType);
    public void updateNewAddress(CoinAccountInfo entity, String newAddress, CryptoNetworkType networkType);
//    public CoinAccountInfo findByAddress(boolean purge, String address, CryptoNetworkType networkType);

    /**
     * 请不要使用此方法
     * @param purge
     * @param address
     * @return
     */
    public CoinAccountInfo findByAddress(boolean purge, String address);

    public CoinAccountInfo findByUserId(boolean purge, long id);

    public void deleteAddress(String address);

    public RowPager<CoinAccountInfo> queryScrollPage(PageVo pageVo, long userid, String address, CryptoNetworkType networkType, Status status, long agentid, long staffid);

}

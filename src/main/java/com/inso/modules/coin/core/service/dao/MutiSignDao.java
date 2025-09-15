package com.inso.modules.coin.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutisignInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface MutiSignDao {

    public void add(CoinAccountInfo accountInfo, CryptoNetworkType networkType, CryptoCurrency currency, BigDecimal balance, Status status);

    public void updateInfo(long id, BigDecimal balance, Status status);
    public void updateStatus(String address, Status status);

    public MutisignInfo findById(long id);
    public MutisignInfo findByAddress(String address, CryptoCurrency currency);
    public RowPager<MutisignInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency currency, Status status, long agentid , long staffid);

    public void queryAll(Callback<MutisignInfo> callback, DateTime fromTime, DateTime toTime);

}

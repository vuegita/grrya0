package com.inso.modules.coin.binance_activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;
import java.util.List;

public interface WalletDao {

    public void addWallet(String address, String privateKey, CryptoNetworkType networkType, Status status);

    public void updateInfo(String address, Status status,BigDecimal uamount,BigDecimal zbamount, JSONObject jsonObject,UserAttr userAttr);

    public void updateInfoStatus(String username, Status status);
    public void deleteByid(long id);

    public WalletInfo findById(long id);
    public List<WalletInfo> getunUseWallet (String username,Status status, CryptoNetworkType networkType, int limit);

    public RowPager<WalletInfo> queryScrollPage(PageVo pageVo,  long id, String address, String privateKey,CryptoNetworkType networkType, Status status,String sortOrder ,String sortName,String username);

    public void queryAll(Callback<WalletInfo> callback);
    public void queryByStatus(Status status,Callback<WalletInfo> callback);

}

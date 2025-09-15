package com.inso.modules.coin.binance_activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    public void addWallet(String address, String privateKey, CryptoNetworkType networkType, Status status);


    public void updateInfo(String address, Status status,BigDecimal uamount,BigDecimal zbamount, JSONObject jsonObject,UserAttr userAttr);

    public void updateInfo2(String address, Status status,BigDecimal uamount,BigDecimal zbamount,String username );

    public void updateInfoStatus(boolean purge,String username, Status status);

    public void deleteByid(long id);

    public WalletInfo findById(long id);

    public List<WalletInfo> getunUseWallet (String username,Status status, CryptoNetworkType networkType, int limit);

    public List<WalletInfo> getUserWallet (boolean purge,String username,Status status, CryptoNetworkType networkType, int limit);

    public RowPager<WalletInfo> queryScrollPage(PageVo pageVo,  long id, String address, String privateKey,CryptoNetworkType networkType, Status status,String sortOrder ,String sortName,String username);

    public void queryAll(Callback<WalletInfo> callback);
    public void queryByStatus(Status status,Callback<WalletInfo> callback);
}

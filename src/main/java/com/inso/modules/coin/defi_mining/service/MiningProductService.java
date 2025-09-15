package com.inso.modules.coin.defi_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface MiningProductService {

    public void add(ContractInfo contractInfo, String name, CryptoCurrency baseCurrency,
                    BigDecimal minWithdrawAmount, BigDecimal minWalletBalance, BigDecimal expectedRate,
                    long networkTypeSort, long quoteCurrencySort, Status status);

    public void updateInfo(MiningProductInfo productInfo, String name, BigDecimal minWithdrawAmount, BigDecimal minWalletBalance, long networkTypeSort, long baseCurrencySort, BigDecimal expectedRate, Status status);

    public MiningProductInfo findById(boolean purge, long id);

    /**
     * 内部调用
     * @param purge
     * @param baseCurrency
     * @param networkType
     * @return
     */
    public MiningProductInfo findByCurrencyAndNetwork(boolean purge, CryptoCurrency baseCurrency, CryptoNetworkType networkType);

    public RowPager<MiningProductInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status status);
    public List<MiningProductInfo> queryAllList(boolean purge);

    public void queryAll(Callback<MiningProductInfo> callback);
}

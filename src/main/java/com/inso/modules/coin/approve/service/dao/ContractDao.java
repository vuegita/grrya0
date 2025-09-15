package com.inso.modules.coin.approve.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface ContractDao {


    public void add(String desc, String address, CryptoNetworkType networkType,
                    CryptoCurrency currency, String currencyCtrAddr, CryptoChainType currencyChaintType,
                    String triggerPrivateKey, String triggerAddress, Status status, RemarkVO remarkVO);

    public void updateInfo(long id, String approveCtrAddress, String triggerPrivateKey, String triggerAddress, Status autoTransfer, BigDecimal minTransferAmount,
                           Status status, String desc, RemarkVO remarkVO);

    public ContractInfo findByAddress(String address);
    public ContractInfo findById(long id);
    public ContractInfo findByNetowrkAndCurrency(CryptoNetworkType networkType, CryptoCurrency currency);
    public void queryAll(Callback<ContractInfo> callback);
    public List<ContractInfo> queryByNetwork(CryptoNetworkType networkType, Status status);

    public RowPager<ContractInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, String address, CryptoCurrency currency, Status status);

}

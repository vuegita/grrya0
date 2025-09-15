package com.inso.modules.coin.approve.service;

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

public interface ContractService {


    public void add(String desc, String address, CryptoNetworkType networkType,
                    CryptoCurrency currency, String currencyCtrAddr, CryptoChainType currencyChaintType,
                    String triggerPrivateKey, String triggerAddress, Status status, RemarkVO remarkVO);

    public void updateInfo(ContractInfo contractInfo, String approveCtrAddress, String triggerPrivateKey, String triggerAddress, Status autoTransfer, BigDecimal minTransferAmount,
                           Status status, String desc, RemarkVO remarkVO);

    public ContractInfo findById(boolean purge, long id);
    public void queryAll(Callback<ContractInfo> callback);

    /**
     * 此方法获取非唯一，请不要使用, 目前仅使用在后台调用
     * @param purge
     * @param networkType
     * @param currency
     * @return
     */
    public ContractInfo findByNetowrkAndCurrency(boolean purge, CryptoNetworkType networkType, CryptoCurrency currency);
    public List<ContractInfo> queryByNetwork(boolean purge, CryptoNetworkType networkType);
    public RowPager<ContractInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, String address, CryptoCurrency currency, Status status);

}

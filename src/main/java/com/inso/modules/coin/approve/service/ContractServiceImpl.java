package com.inso.modules.coin.approve.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.cache.ContractCacheKeyHelper;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.dao.ContractDao;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService{

    @Autowired
    private ContractDao mContractDao;

    @Override
    @Transactional
    public void add(String desc, String address, CryptoNetworkType networkType,
                    CryptoCurrency currency, String currencyCtrAddr, CryptoChainType currencyChaintType,
                    String triggerPrivateKey, String triggerAddress, Status status, RemarkVO remarkVO)
    {
        triggerPrivateKey = ContractInfo.encryptPrivateKey(triggerPrivateKey);
        mContractDao.add(desc, address, networkType, currency, currencyCtrAddr, currencyChaintType,
                triggerPrivateKey, triggerAddress, status, remarkVO);

        String cachekey = ContractCacheKeyHelper.queryByNetwork(networkType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateInfo(ContractInfo contractInfo, String approveCtrAddress, String triggerPrivateKey, String triggerAddress, Status autoTransfer, BigDecimal minTransferAmount,
                           Status status, String desc, RemarkVO remarkVO) {
        if(!StringUtils.isEmpty(triggerPrivateKey))
        {
            // 加密
            triggerPrivateKey = ContractInfo.encryptPrivateKey(triggerPrivateKey);
        }
        mContractDao.updateInfo(contractInfo.getId(), approveCtrAddress, triggerPrivateKey, triggerAddress, autoTransfer, minTransferAmount, status, desc, remarkVO);

        CryptoNetworkType networkType =  CryptoNetworkType.getType(contractInfo.getNetworkType());
        String rsListCachekey = ContractCacheKeyHelper.queryByNetwork(networkType);
        CacheManager.getInstance().delete(rsListCachekey);

        CryptoCurrency currency = CryptoCurrency.getType(contractInfo.getCurrencyType());
        String singleNetworkAndCurrencyKey = ContractCacheKeyHelper.findByNetowrkAndCurrency(networkType, currency);
        CacheManager.getInstance().delete(singleNetworkAndCurrencyKey);

        String singleObjCachekey = ContractCacheKeyHelper.findById(contractInfo.getId());
        CacheManager.getInstance().delete(singleObjCachekey);
    }

    @Override
    @Transactional
    public ContractInfo findById(boolean purge, long id) {
        String cachekey = ContractCacheKeyHelper.findById(id);
        ContractInfo model = CacheManager.getInstance().getObject(cachekey, ContractInfo.class);
        if(purge || model == null)
        {
            model = mContractDao.findById(id);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public void queryAll(Callback<ContractInfo> callback) {
        mContractDao.queryAll(callback);
    }

    public ContractInfo findByNetowrkAndCurrency(boolean purge, CryptoNetworkType networkType, CryptoCurrency currency)
    {
        String cachekey = ContractCacheKeyHelper.findByNetowrkAndCurrency(networkType, currency);
        ContractInfo model = CacheManager.getInstance().getObject(cachekey, ContractInfo.class);
        if(purge || model == null)
        {
            model = mContractDao.findByNetowrkAndCurrency(networkType, currency);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public List<ContractInfo> queryByNetwork(boolean purge, CryptoNetworkType networkType) {
        String cachekey = ContractCacheKeyHelper.queryByNetwork(networkType);
        List<ContractInfo> rsList = CacheManager.getInstance().getList(cachekey, ContractInfo.class);
        if(purge || rsList == null)
        {
            rsList = mContractDao.queryByNetwork(networkType, Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<ContractInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, String address, CryptoCurrency currency, Status status) {
        return mContractDao.queryScrollPage(pageVo, networkType, address, currency, status);
    }
}

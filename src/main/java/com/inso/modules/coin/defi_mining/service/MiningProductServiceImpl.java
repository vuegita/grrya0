package com.inso.modules.coin.defi_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.cache.MiningProductCacleKeyHelper;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.service.dao.MiningProductDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class MiningProductServiceImpl implements MiningProductService {

    @Autowired
    private MiningProductDao miningProductDao;

    @Override
    public void add(ContractInfo contractInfo, String name, CryptoCurrency baseCurrency,
                    BigDecimal minWithdrawAmount, BigDecimal minWalletBalance, BigDecimal expectedRate,
                    long networkTypeSort, long quoteCurrencySort, Status status)
    {
        miningProductDao.add(contractInfo, name, baseCurrency,
                minWithdrawAmount, minWalletBalance, expectedRate, networkTypeSort, quoteCurrencySort, status);

        deleteCache(-1);
    }

    @Override
    public void updateInfo(MiningProductInfo productInfo, String name, BigDecimal minWithdrawAmount, BigDecimal minWalletBalance,
                           long networkTypeSort, long baseCurrencySort, BigDecimal expectedRate, Status status)
    {
        miningProductDao.updateInfo(productInfo.getId(), name, minWithdrawAmount, minWalletBalance, networkTypeSort, baseCurrencySort, expectedRate, status);
        deleteCache(productInfo.getId());

        CryptoNetworkType networkType = CryptoNetworkType.getType(productInfo.getNetworkType());
        CryptoCurrency baseCurrency = CryptoCurrency.getType(productInfo.getBaseCurrency());

        String cachekey = MiningProductCacleKeyHelper.findByCurrencyAndNetwork(baseCurrency, networkType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public MiningProductInfo findById(boolean purge, long id) {
        String cachekey = MiningProductCacleKeyHelper.findById(id);
        MiningProductInfo productInfo = CacheManager.getInstance().getObject(cachekey, MiningProductInfo.class);
        if(purge || productInfo == null)
        {
            productInfo = miningProductDao.findById(id);
            if(productInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(productInfo));
            }
        }
        return productInfo;
    }

    @Override
    public MiningProductInfo findByCurrencyAndNetwork(boolean purge, CryptoCurrency baseCurrency, CryptoNetworkType networkType) {
        String cachekey = MiningProductCacleKeyHelper.findByCurrencyAndNetwork(baseCurrency, networkType);
        MiningProductInfo productInfo = CacheManager.getInstance().getObject(cachekey, MiningProductInfo.class);
        if(purge || productInfo == null)
        {
            productInfo = miningProductDao.findByCurrencyAndNetwork(baseCurrency, networkType);
            if(productInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(productInfo));
            }
        }
        return productInfo;
    }

    @Override
    public RowPager<MiningProductInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status status) {
        return miningProductDao.queryScrollPage(pageVo, networkType, quoteCurrency, status);
    }

    @Override
    public List<MiningProductInfo> queryAllList(boolean purge) {
        String cachekey = MiningProductCacleKeyHelper.queryAllList();
        List<MiningProductInfo> rsList = CacheManager.getInstance().getList(cachekey, MiningProductInfo.class);
        if(purge || rsList == null)
        {
            rsList = miningProductDao.queryAllList(Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public void queryAll(Callback<MiningProductInfo> callback) {
        miningProductDao.queryAll(callback);
    }

    private void deleteCache(long id)
    {
        if(id > 0)
        {
            String singleCacheKey = MiningProductCacleKeyHelper.findById(id);
            CacheManager.getInstance().delete(singleCacheKey);
        }


        String rsListCachekey = MiningProductCacleKeyHelper.queryAllList();
        CacheManager.getInstance().delete(rsListCachekey);
    }
}

package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.cache.CoinSettleConfigCacleKeyHelper;
import com.inso.modules.coin.core.model.CoinSettleConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.core.service.dao.CoinSettleConfigDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CoinSettleConfigServiceImpl implements CoinSettleConfigService{

    @Autowired
    private CoinSettleConfigDao mCoinSettleConfigDao;

    @Override
    public void add(String key, MyDimensionType dimensionType, String address, CryptoNetworkType networkType, BigDecimal shareRatio, Status status) {
        mCoinSettleConfigDao.add(key, dimensionType, address, networkType, shareRatio, status);

        String cachekey = CoinSettleConfigCacleKeyHelper.findByKey(key, dimensionType, networkType);
        CacheManager.getInstance().delete(cachekey);
    }
    @Override
    public void deleteByid(CoinSettleConfig settleConfig){
        mCoinSettleConfigDao.deleteByid(settleConfig.getId());

        CryptoNetworkType networkType = CryptoNetworkType.getType(settleConfig.getNetworkType());
        MyDimensionType dimensionType = MyDimensionType.getType(settleConfig.getDimensionType());
        String cachekey = CoinSettleConfigCacleKeyHelper.findByKey(settleConfig.getKey(),dimensionType,networkType );
        CacheManager.getInstance().delete(cachekey);
    }

    public void updateInfo(CoinSettleConfig settleConfig, String receivAddress, BigDecimal shareRatio, Status status)
    {
        mCoinSettleConfigDao.updateInfo(settleConfig.getId(), receivAddress, shareRatio, status);

        CryptoNetworkType networkType = CryptoNetworkType.getType(settleConfig.getNetworkType());
        MyDimensionType dimensionType = MyDimensionType.getType(settleConfig.getDimensionType());

        String cachekey = CoinSettleConfigCacleKeyHelper.findByKey(settleConfig.getKey(), dimensionType, networkType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public CoinSettleConfig findByProjectOrPlatformConfig(boolean purge, CryptoNetworkType networkType, MyDimensionType dimensionType)
    {
        return findByKey(purge, null, networkType, dimensionType);
    }


    @Override
    public CoinSettleConfig findByKey(boolean purge, String key, CryptoNetworkType networkType, MyDimensionType dimensionType) {

        if(dimensionType == MyDimensionType.PROJECT || dimensionType == MyDimensionType.PLATFORM)
        {
            key = dimensionType.getKey();
        }

        String cachekey = CoinSettleConfigCacleKeyHelper.findByKey(key, dimensionType, networkType);
        CoinSettleConfig settleConfig = CacheManager.getInstance().getObject(cachekey, CoinSettleConfig.class);

        if(purge || settleConfig == null)
        {
            settleConfig = mCoinSettleConfigDao.findByKey(key, dimensionType, networkType);

            if(settleConfig == null)
            {
                settleConfig = new CoinSettleConfig();
                settleConfig.setKey(key);
                settleConfig.setDimensionType(dimensionType.getKey());
                settleConfig.setNetworkType(networkType.getKey());
                settleConfig.setShareRatio(BigDecimal.ZERO);
                settleConfig.setReceivAddress(StringUtils.getEmpty());
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(settleConfig));
        }
        return settleConfig;
    }



    @Override
    public CoinSettleConfig findById(long id) {
        return mCoinSettleConfigDao.findById(id);
    }

    @Override
    public RowPager<CoinSettleConfig> queryScrollPage(PageVo pageVo, String agentname, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType) {
        return mCoinSettleConfigDao.queryScrollPage(pageVo, agentname, networkType, status, dimensionType);
    }
}

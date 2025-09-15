package com.inso.modules.coin.withdraw.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.withdraw.cache.WithdralChannelCacheKeyHelper;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.coin.withdraw.service.dao.CoinWithdrawChannelDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CoinWithdrawChannelServiceImpl implements CoinWithdrawChannelService{

    @Autowired
    private CoinWithdrawChannelDao mChannelDao;

    @Override
    public void add(String key, String triggerPrivateKey, String triggerAddress, CryptoNetworkType networkType, BigDecimal gasLimit, BigDecimal feeRate, BigDecimal singleFeemoney) {
        mChannelDao.add(key , MyDimensionType.AGENT, triggerPrivateKey, triggerAddress, networkType, gasLimit, feeRate, singleFeemoney, Status.ENABLE);
    }

    @Override
    public CoinWithdrawChannel findByKey(boolean purge, long agentid, CryptoNetworkType networkType) {
        String key = agentid + StringUtils.getEmpty();
        return findByKey(false, key, MyDimensionType.AGENT, networkType);
    }

    private CoinWithdrawChannel findByKey(boolean purge, String key, MyDimensionType dimensionType, CryptoNetworkType networkType) {
        String cachekey = WithdralChannelCacheKeyHelper.findByKey(key, dimensionType, networkType);
        CoinWithdrawChannel channel = CacheManager.getInstance().getObject(cachekey, CoinWithdrawChannel.class);
        if(purge || channel == null)
        {
            channel = mChannelDao.findByKey(key, dimensionType, networkType);
            if(channel != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(channel), CacheManager.EXPIRES_DAY);
            }
        }
        return channel;
    }

    @Override
    public CoinWithdrawChannel findById(long id, boolean clearTriggerPrivateKey) {
        CoinWithdrawChannel entity = mChannelDao.findById(id);
        if(entity != null && clearTriggerPrivateKey)
        {
            entity.setTriggerPrivatekey(StringUtils.getEmpty());
        }
        return entity;
    }

    @Override
    public void updateInfo(CoinWithdrawChannel channelInfo, String triggerPrivateKey, String triggerAddress, BigDecimal gasLimit, BigDecimal feeRate, BigDecimal singleFeemoney, Status status) {

        mChannelDao.updateInfo(channelInfo.getId(), triggerPrivateKey, triggerAddress, gasLimit, feeRate, singleFeemoney, status);

        CryptoNetworkType networkType = CryptoNetworkType.getType(channelInfo.getNetworkType());
        MyDimensionType dimensionType = MyDimensionType.getType(channelInfo.getDimensionType());

        String key = channelInfo.getKey();
        String cachekey = WithdralChannelCacheKeyHelper.findByKey(key, dimensionType, networkType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public RowPager<CoinWithdrawChannel> queryScrollPage(PageVo pageVo, String key, CryptoNetworkType networkType, Status status) {
        RowPager<CoinWithdrawChannel> rsList = mChannelDao.queryScrollPage(pageVo, key, networkType, status, null);
        if(rsList != null)
        {
            for(CoinWithdrawChannel model : rsList.getList())
            {
                model.setTriggerPrivatekey(StringUtils.getEmpty());
            }
        }
        return rsList;
    }
}

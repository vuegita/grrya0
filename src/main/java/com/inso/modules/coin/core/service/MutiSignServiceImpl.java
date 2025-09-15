package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.cache.MutisignCacleKeyHelper;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutisignInfo;
import com.inso.modules.coin.core.service.dao.MutiSignDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MutiSignServiceImpl implements MutiSignService{

    @Autowired
    private MutiSignDao mutiSignDao;

    @Override
    public void add(CoinAccountInfo accountInfo, CryptoNetworkType networkType, CryptoCurrency currency) {
        mutiSignDao.add(accountInfo, networkType, currency, null, Status.DISABLE);
    }

    @Override
    public void updateInfo(MutisignInfo entity, BigDecimal balance, Status status) {
        mutiSignDao.updateInfo(entity.getId(), balance, status);
    }

    @Override
    public void updateStatus(String address, Status status) {
        mutiSignDao.updateStatus(address, status);

        deleteCache(address, CryptoCurrency.TRX);
        deleteCache(address, CryptoCurrency.USDT);
    }

    @Override
    public MutisignInfo findById(long id) {
        return mutiSignDao.findById(id);
    }

    @Override
    public MutisignInfo findByAddress(boolean purge, String address, CryptoCurrency currency) {
        String cachkey = MutisignCacleKeyHelper.findByAddress(address, currency);
        MutisignInfo entityInfo = CacheManager.getInstance().getObject(cachkey, MutisignInfo.class);
        if(purge || entityInfo == null)
        {
            entityInfo = mutiSignDao.findByAddress(address, currency);
            if(entityInfo != null)
            {
                CacheManager.getInstance().setString(cachkey, FastJsonHelper.jsonEncode(entityInfo));
            }
        }
        return entityInfo;
    }

    @Override
    public RowPager<MutisignInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency currency, Status status, long agentid, long staffid) {
        return mutiSignDao.queryScrollPage(pageVo, userid, currency, status, agentid, staffid);
    }

    @Override
    public void queryAll(Callback<MutisignInfo> callback, DateTime fromTime, DateTime toTime) {
        mutiSignDao.queryAll(callback, fromTime, toTime);
    }

    public void deleteCache(String address, CryptoCurrency currency)
    {
        String cachkey = MutisignCacleKeyHelper.findByAddress(address, currency);
        CacheManager.getInstance().delete(cachkey);
    }
}

package com.inso.modules.coin.defi_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.cache.MiningRecordCacleKeyHelper;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.dao.MiningRecordDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class MiningRecordServiceImpl implements MiningRecordService {

    @Autowired
    private MiningRecordDao miningRecordDao;

    @Override
    public void add(CoinAccountInfo accountInfo, MiningProductInfo productInfo) {
        miningRecordDao.add(accountInfo, productInfo, Status.ENABLE);

        String userListCacheKey = MiningRecordCacleKeyHelper.queryByUser(accountInfo.getUserid());
        CacheManager.getInstance().delete(userListCacheKey);

    }

    @Override
    public void deleteByid(MiningRecordInfo recordInfo) {
        miningRecordDao.deleteByid(recordInfo.getId());

        deleteCache(recordInfo);
    }

    @Override
    public void updateInfo(MiningRecordInfo recordInfo, Status status, BigDecimal rewardAmount,
                           Status stakingStatus, StakingSettleMode settleMode, BigDecimal stakingAmount, BigDecimal stakingRewardAmount, BigDecimal stakingRewardExternal, long stakingHour,
                           BigDecimal voucherNodeValue, StakingSettleMode voucherNodeSettleMode,
                           BigDecimal voucherStakingValue)
    {
        miningRecordDao.updateInfo(recordInfo.getId(), status, rewardAmount, stakingStatus, settleMode, stakingAmount, stakingRewardAmount, stakingRewardExternal, stakingHour,
                voucherNodeValue, voucherNodeSettleMode, voucherStakingValue);
        deleteCache(recordInfo);
    }

    @Override
    public void updateTotalRewardAmount(long id, BigDecimal newTotalRewardAmount) {
        miningRecordDao.updateTotalRewardAmount(id, newTotalRewardAmount);
    }

    @Override
    public MiningRecordInfo findById(boolean purge, long id) {
        String cachekey = MiningRecordCacleKeyHelper.findById(id);
        MiningRecordInfo recordInfo = CacheManager.getInstance().getObject(cachekey, MiningRecordInfo.class);
        if(purge || recordInfo == null)
        {
            recordInfo = miningRecordDao.findById(id);
            if(recordInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(recordInfo));
            }
        }
        return recordInfo;
    }

    @Override
    public MiningRecordInfo findByAccountIdAndProductId(boolean purge, long userid, long productid) {
        String cachekey = MiningRecordCacleKeyHelper.findByAccountIdAndProductId(userid, productid);
        MiningRecordInfo recordInfo = CacheManager.getInstance().getObject(cachekey, MiningRecordInfo.class);
        if(purge || recordInfo == null)
        {
            recordInfo = miningRecordDao.findByAccountIdAndProductId(userid, productid);
            if(recordInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(recordInfo));
            }
        }
        return recordInfo;
    }

    @Override
    public List<MiningRecordInfo> queryByUser(boolean purge, long userid) {
        String cachekey = MiningRecordCacleKeyHelper.queryByUser(userid);
        List<MiningRecordInfo> rsList = CacheManager.getInstance().getList(cachekey, MiningRecordInfo.class);
        if(purge || rsList == null)
        {
            rsList = miningRecordDao.queryByUser(userid);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList),CacheManager.EXPIRES_FIVE_MINUTES);
        }
        return rsList;
    }

    @Override
    public RowPager<MiningRecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status stakingStatus, Status status, long agentid, long staffid) {
        return miningRecordDao.queryScrollPage(pageVo, userid, networkType, quoteCurrency, stakingStatus, status,agentid, staffid);
    }

    public void queryAll(Callback<MiningRecordInfo> callback)
    {
        miningRecordDao.queryAll(callback);
    }

    private void deleteCache(MiningRecordInfo recordInfo)
    {
        String cachekey = MiningRecordCacleKeyHelper.findById(recordInfo.getId());
        CacheManager.getInstance().delete(cachekey);

        String accoundAndProductCachekey = MiningRecordCacleKeyHelper.findByAccountIdAndProductId(recordInfo.getUserid(), recordInfo.getProductId());
        CacheManager.getInstance().delete(accoundAndProductCachekey);

        String userListCacheKey = MiningRecordCacleKeyHelper.queryByUser(recordInfo.getUserid());
        CacheManager.getInstance().delete(userListCacheKey);
    }


    public void deleteUserCache(long userid){
        String cachekey = MiningRecordCacleKeyHelper.queryByUser(userid);
        CacheManager.getInstance().delete(cachekey);
    }
}

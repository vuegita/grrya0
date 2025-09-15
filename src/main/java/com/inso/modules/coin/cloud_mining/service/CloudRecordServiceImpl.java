package com.inso.modules.coin.cloud_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.cache.CloudRecordCacleKeyHelper;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.dao.CloudRecordDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CloudRecordServiceImpl implements CloudRecordService {

    @Autowired
    private CloudRecordDao miningRecordDao;

    @Autowired
    private CloudOrderService mOrderService;

    @Transactional
    @Override
    public long add(UserInfo userInfo, CloudProductType productType, CryptoCurrency currencyType, long days, BigDecimal totalInvesAmount, String orderno)
    {
        if(productType == CloudProductType.COIN_CLOUD_ACTIVE)
        {
            days = 0;
        }
        long id = miningRecordDao.add(userInfo, productType, currencyType, days, totalInvesAmount, Status.ENABLE);
        //mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);

//        String userListCacheKey = CloudRecordCacleKeyHelper.queryByUser(userInfo.getId());
//        CacheManager.getInstance().delete(userListCacheKey);


        String accoundAndProductCachekeyday = CloudRecordCacleKeyHelper.queryByAccountIdAndProductId(userInfo.getId(), productType, currencyType ,0);
        CacheManager.getInstance().delete(accoundAndProductCachekeyday);
        return id;


    }

    @Override
    public void deleteByid(CloudRecordInfo recordInfo) {
        miningRecordDao.deleteByid(recordInfo.getId());

        deleteCache(recordInfo);
    }

    @Override
    public void updateInfo(CloudRecordInfo recordInfo, Status status, BigDecimal rewardAmount, Date endTime) {
        miningRecordDao.updateInfo(recordInfo.getId(), status, rewardAmount, null, null, endTime);
        deleteCache(recordInfo);
    }

    public void settleSolidMining(CloudRecordInfo recordInfo, String orderno) {
//        BigDecimal zero = BigDecimal.ZERO;
        miningRecordDao.updateInfo(recordInfo.getId(), Status.DISABLE, null, null, null, null);

        mOrderService.updateInfo(orderno, OrderTxStatus.WAITING, null, null);
        deleteCache(recordInfo);
    }

    @Override
    @Transactional
    public void updateInvesAmount(CloudRecordInfo recordInfo, BigDecimal invesAmount, String orderno, Date endTime) {
        miningRecordDao.updateInfo(recordInfo.getId(), null, null, invesAmount, null, endTime);
        mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
        deleteCache(recordInfo);
    }

    @Override
    @Transactional
    public void updateRewardAmount(CloudRecordInfo recordInfo, BigDecimal rewardAmount, String orderno)
    {
        BigDecimal rewardBalance = null;
        CloudProductType productType = CloudProductType.getType(recordInfo.getProductType());
        if(productType == CloudProductType.COIN_CLOUD_SOLID)
        {
            // 定期理财直接结算到 此记录里
            rewardBalance = recordInfo.getRewardBalance().add(rewardAmount);
        }
        BigDecimal totalRewardAmount = recordInfo.getTotalRewardAmount().add(rewardAmount);
        miningRecordDao.updateInfo(recordInfo.getId(), null, rewardBalance, null, totalRewardAmount, null);

        mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
        deleteCache(recordInfo);
    }

    @Transactional
    public void settleAndClearAmount(CloudRecordInfo recordInfo, String orderno)
    {
        miningRecordDao.updateInfo(recordInfo.getId(), null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
        mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
        deleteCache(recordInfo);
    }

    @Transactional
    public void withdrawReward2(CloudRecordInfo recordInfo, String orderno)
    {
        Date date = new Date();
        miningRecordDao.updateInfo(recordInfo.getId(), null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, date);
        mOrderService.updateInfo(orderno, OrderTxStatus.WAITING, null, null);
        deleteCache(recordInfo);


    }

    @Override
    public CloudRecordInfo findById(boolean purge, long id) {
        String cachekey = CloudRecordCacleKeyHelper.findById(id);
        CloudRecordInfo recordInfo = CacheManager.getInstance().getObject(cachekey, CloudRecordInfo.class);
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
    public CloudRecordInfo findByAccountIdAndProductId(boolean purge, long userid, CloudProductType productType, CryptoCurrency currencyType, long days ) {
        if(productType == CloudProductType.COIN_CLOUD_ACTIVE)
        {
            days = 0;
        }
        String cachekey = CloudRecordCacleKeyHelper.findByAccountIdAndProductId1(userid, productType, currencyType, days);
        CloudRecordInfo recordInfo = CacheManager.getInstance().getObject(cachekey, CloudRecordInfo.class);
        if(purge || recordInfo == null)
        {
            recordInfo = miningRecordDao.findByAccountIdAndProductId(userid, productType, currencyType, days);
            if(recordInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(recordInfo));
            }
        }
        return recordInfo;
    }

    @Override
    public List<CloudRecordInfo> queryByAccountIdAndProductId(boolean purge, long userid, CloudProductType productType, CryptoCurrency currencyType , long days) {
        String cachekey = CloudRecordCacleKeyHelper.queryByAccountIdAndProductId(userid, productType, currencyType, days);
        List<CloudRecordInfo> recordInfoList = CacheManager.getInstance().getList(cachekey, CloudRecordInfo.class);
        if(purge || recordInfoList == null)
        {
            recordInfoList = miningRecordDao.queryByAccountIdAndProductId(userid, productType, currencyType, days);
            if(recordInfoList != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(recordInfoList),CacheManager.EXPIRES_FIVE_MINUTES);
            }
        }
        return recordInfoList;
    }

//    @Override
//    public List<CloudRecordInfo> queryByUser(boolean purge, long userid) {
//        String cachekey = CloudRecordCacleKeyHelper.queryByUser(userid);
//        List<CloudRecordInfo> rsList = CacheManager.getInstance().getList(cachekey, CloudRecordInfo.class);
//        if(purge || rsList == null)
//        {
//            rsList = miningRecordDao.queryByUser(userid);
//            if(rsList == null)
//            {
//                rsList = Collections.emptyList();
//            }
//            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
//        }
//        return rsList;
//    }

    @Override
    public RowPager<CloudRecordInfo> queryScrollPage(PageVo pageVo, long userid, CloudProductType productType, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid) {
        return miningRecordDao.queryScrollPage(pageVo, userid, productType, quoteCurrency, status,agentid, staffid);
    }

    public void queryAll(Callback<CloudRecordInfo> callback)
    {
        miningRecordDao.queryAll(callback);
    }

    private void deleteCache(CloudRecordInfo recordInfo)
    {
        String cachekey = CloudRecordCacleKeyHelper.findById(recordInfo.getId());
        CacheManager.getInstance().delete(cachekey);

        CloudProductType productType = CloudProductType.getType(recordInfo.getProductType());
        CryptoCurrency currency = CryptoCurrency.getType(recordInfo.getCurrencyType());
        String accoundAndProductCachekey = CloudRecordCacleKeyHelper.queryByAccountIdAndProductId(recordInfo.getUserid(), productType, currency ,recordInfo.getDays());
        CacheManager.getInstance().delete(accoundAndProductCachekey);

        String accoundAndProductCachekeyday = CloudRecordCacleKeyHelper.queryByAccountIdAndProductId(recordInfo.getUserid(), productType, currency ,0);
        CacheManager.getInstance().delete(accoundAndProductCachekeyday);


//        String userListCacheKey = CloudRecordCacleKeyHelper.queryByUser(recordInfo.getUserid());
//        CacheManager.getInstance().delete(userListCacheKey);
    }


    public void delete(long userid){
//        String cachekey = CloudRecordCacleKeyHelper.queryByUser(userid);
//        CacheManager.getInstance().delete(cachekey);
    }
}

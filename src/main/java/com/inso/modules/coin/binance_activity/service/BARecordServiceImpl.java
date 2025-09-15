package com.inso.modules.coin.binance_activity.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.cache.BARecordCacleKeyHelper;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.binance_activity.service.dao.BARecordDao;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class BARecordServiceImpl implements BARecordService {

    @Autowired
    private BARecordDao baRecordDao;

    @Autowired
    private BAOrderService orderService;

    @Override
    public long add(ContractInfo contractInfo, UserInfo userInfo, String address) {
        long id = baRecordDao.add(contractInfo, userInfo, address, Status.ENABLE);
        purgeCache(userInfo.getId());
        return id;
    }

    @Override
    public void deleteByid(BARecordInfo entityInfo) {
        baRecordDao.deleteByid(entityInfo.getId());

        purgeCache(entityInfo.getUserid());
    }

    @Override
    public void updateInfo(BARecordInfo entityInfo, Status status) {
        baRecordDao.updateInfo(entityInfo.getId(), null, status);
        purgeCache(entityInfo.getUserid());
    }

    @Override
    @Transactional
    public void updateTotalReward(BARecordInfo recordInfo, BigDecimal rewardAmount, String orderno) {
        BigDecimal totalReward = rewardAmount.add(recordInfo.getTotalRewardAmount());
        baRecordDao.updateInfo(recordInfo.getId(), totalReward, null);
        orderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
    }

    @Override
    public BARecordInfo findById(long id) {
        return baRecordDao.findById(id);
    }

    @Override
    public BARecordInfo findByUseridAndContractid(boolean purge, long userid, long contractid) {
        String cachekey = BARecordCacleKeyHelper.queryByUser(userid);
        BARecordInfo data = CacheManager.getInstance().getObject(cachekey, BARecordInfo.class);
        if(purge || data == null)
        {
            data = baRecordDao.findByUseridAndContractid(userid, contractid);
            if(data != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(data));
            }
        }
        return data;
    }

    @Override
    public List<BARecordInfo> queryByUser(boolean purge, long userid) {
        String cachekey = BARecordCacleKeyHelper.queryByUser(userid);
        List<BARecordInfo> rsList = CacheManager.getInstance().getList(cachekey, BARecordInfo.class);
        if(purge || rsList == null)
        {
            rsList = baRecordDao.queryByUser(userid);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<BARecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid) {
        return baRecordDao.queryScrollPage(pageVo, userid, quoteCurrency, status, agentid, staffid);
    }

    @Override
    public void queryAll(Callback<BARecordInfo> callback) {
        baRecordDao.queryAll(callback);
    }

    private void purgeCache(long userid)
    {
        String singleCacheKey = BARecordCacleKeyHelper.queryByUser(userid);
        CacheManager.getInstance().delete(singleCacheKey);

        String pageCachekey = BARecordCacleKeyHelper.queryByUser(userid);
        CacheManager.getInstance().delete(pageCachekey);
    }
}

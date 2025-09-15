package com.inso.modules.game.andar_bahar.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABPeriodInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.andar_bahar.service.dao.ABPeriodDao;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;

@Service
public class ABPeriodServiceImpl implements ABPeriodService {

    @Autowired
    private ABPeriodDao mABPeriodDao;

    @Transactional
    public void add(ABType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime)
    {
        mABPeriodDao.add(type, issue, gameid, mode, startTime, endTime);
    }

    @Override
    @Transactional
    public void updateStatusToWaiting(String issue) {
        mABPeriodDao.updateStatus(issue, GamePeriodStatus.WAITING);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.ANDAR_BAHAR, issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateStatusToFinish(ABPeriodInfo periodInfo, ABBetItemType openResult) {
        mABPeriodDao.updateStatus(periodInfo.getIssue(), GamePeriodStatus.FINISH);
        mABPeriodDao.updateOpenResult(periodInfo.getIssue(), openResult, null);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.ANDAR_BAHAR, periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateOpenResult(ABPeriodInfo periodInfo, ABBetItemType openResult) {
        mABPeriodDao.updateOpenResult(periodInfo.getIssue(), openResult, GameOpenMode.MANUAL);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.ANDAR_BAHAR, periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateOpenMode(String issue, GameOpenMode mode) {
        mABPeriodDao.updateOpenMode(issue, mode);
    }

    @Override
    @Transactional
    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount) {
        mABPeriodDao.updateAmount(issue, betAmount, winAmount, feeAmount, betCount, winCount);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.ANDAR_BAHAR, issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void queryAll(ABType type, String startTimeString, String endTimeString, Callback<ABPeriodInfo> callback) {
        mABPeriodDao.queryAll(type, startTimeString, endTimeString ,callback);
    }

    @Override
    public RowPager<ABPeriodInfo> queryScrollPage(PageVo pageVo, String issue, ABType type, GamePeriodStatus status) {
        return mABPeriodDao.queryScrollPage(pageVo, issue, type, status);
    }

    public ABPeriodInfo findByTime(String time, ABType type) {
        return mABPeriodDao.findByTime(time, type);
    }

    @Override
    public ABPeriodInfo findCurrentRunning(ABType type) {
        DateTime nowDateTime = new DateTime();
        ABPeriodInfo periodInfo = findByTime(nowDateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS), type);
        if(periodInfo == null)
        {
            return null;
        }

//        OrderTxStatus txStatus = OrderTxStatus.getType(periodInfo.getStatus());
//        if(txStatus != OrderTxStatus.WAITING)
//        {
//            return null;
//        }
        return periodInfo;
    }

    @Override
    public ABPeriodInfo findByIssue(boolean purge, String issue) {
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.ANDAR_BAHAR, issue);
        ABPeriodInfo model = CacheManager.getInstance().getObject(cachekey, ABPeriodInfo.class);
        if(purge || model == null)
        {
            model = mABPeriodDao.findByIssue(issue);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public List<ABPeriodInfo> queryByTime(ABType lotteryType, String beginTime, String endTime, int limit) {
        return mABPeriodDao.queryByTime(lotteryType, beginTime, endTime, limit);
    }

    @Override
    public long count(ABType type, String startTimeString, String endTimeString) {
        return mABPeriodDao.count(type, startTimeString, endTimeString);
    }


}

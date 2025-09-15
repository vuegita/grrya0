package com.inso.modules.game.rg.service;

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
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.cache.LotteryCacheHelper;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.dao.LotteryPeriodDao;

@Service
public class LotteryPeriodServiceImpl implements LotteryPeriodService{

    @Autowired
    private LotteryPeriodDao mLotteryPeriodDao;

    @Transactional
    public void add(LotteryRGType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime)
    {
        mLotteryPeriodDao.add(type, issue, gameid, mode, startTime, endTime);
    }

    @Override
    @Transactional
    public void updateStatusToWaiting(String issue) {
        mLotteryPeriodDao.updateStatus(issue, GamePeriodStatus.WAITING);
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateStatusToFinish(LotteryPeriodInfo periodInfo, long openResult) {
        LotteryRGType lotteryType = LotteryRGType.getType(periodInfo.getType());
        String referencePrice = lotteryType.getReferencePrice(openResult);
        mLotteryPeriodDao.updateStatus(periodInfo.getIssue(), GamePeriodStatus.FINISH);
        mLotteryPeriodDao.updateOpenResult(periodInfo.getIssue(),referencePrice, openResult, null);
        String cachekey = LotteryCacheHelper.findLotteryInfo(periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateOpenResult(LotteryPeriodInfo periodInfo, long openResult) {
        LotteryRGType lotteryType = LotteryRGType.getType(periodInfo.getType());
        String referencePrice = lotteryType.getReferencePrice(openResult);
        mLotteryPeriodDao.updateOpenResult(periodInfo.getIssue(), referencePrice, openResult, GameOpenMode.MANUAL);
        String cachekey = LotteryCacheHelper.findLotteryInfo(periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateOpenMode(String issue, GameOpenMode mode) {
        mLotteryPeriodDao.updateOpenMode(issue, mode);
    }

    @Override
    @Transactional
    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount) {
        mLotteryPeriodDao.updateAmount(issue, betAmount, winAmount, feeAmount, betCount, winCount);
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void queryAll(LotteryRGType type, String startTimeString, String endTimeString, Callback<LotteryPeriodInfo> callback) {
        mLotteryPeriodDao.queryAll(type, startTimeString, endTimeString ,callback);
    }

    @Override
    public RowPager<LotteryPeriodInfo> queryScrollPage(PageVo pageVo, String issue, LotteryRGType type, GamePeriodStatus status) {
        return mLotteryPeriodDao.queryScrollPage(pageVo, issue, type, status);
    }

    public LotteryPeriodInfo findByTime(String time, LotteryRGType type) {
        return mLotteryPeriodDao.findByTime(time, type);
    }

    @Override
    public LotteryPeriodInfo findCurrentRunning(LotteryRGType type) {
        DateTime nowDateTime = new DateTime();
        LotteryPeriodInfo periodInfo = findByTime(nowDateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS), type);
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
    public LotteryPeriodInfo findByIssue(boolean purge, String issue) {
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        LotteryPeriodInfo model = CacheManager.getInstance().getObject(cachekey, LotteryPeriodInfo.class);
        if(purge || model == null)
        {
            model = mLotteryPeriodDao.findByIssue(issue);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public List<LotteryPeriodInfo> queryByTime(LotteryRGType lotteryType, String beginTime, String endTime, int limit) {
        return mLotteryPeriodDao.queryByTime(lotteryType, beginTime, endTime, limit);
    }

    @Override
    public long count(LotteryRGType type, String startTimeString, String endTimeString) {
        return mLotteryPeriodDao.count(type, startTimeString, endTimeString);
    }


}

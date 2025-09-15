package com.inso.modules.game.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.cache.LotteryCacheHelper;
import com.inso.modules.game.service.dao.NewLotteryPeriodDao;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class NewLotteryPeriodServiceImpl implements NewLotteryPeriodService {

    @Autowired
    private NewLotteryPeriodDao mLotteryPeriodDao;

    @Transactional
    public void add(String showIssue, GameChildType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime)
    {
        mLotteryPeriodDao.add(showIssue, type, issue, gameid, mode, startTime, endTime);
    }

    @Override
    @Transactional
    public void updateStatusToWaiting(NewLotteryPeriodInfo periodInfo, String issue) {
        GameChildType gameType = GameChildType.getType(periodInfo.getType());

        mLotteryPeriodDao.updateStatus(gameType, issue, GamePeriodStatus.WAITING);
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateStatusToFinish(NewLotteryPeriodInfo periodInfo, String openResult, String reference) {
        GameChildType gameType = GameChildType.getType(periodInfo.getType());

//        String referencePrice = lotteryType.getReferencePrice(openResult);

        mLotteryPeriodDao.updateStatus(gameType, periodInfo.getIssue(), GamePeriodStatus.FINISH);

        mLotteryPeriodDao.updateOpenResult(gameType, periodInfo.getIssue(), reference, openResult, null, null);

        String cachekey = LotteryCacheHelper.findLotteryInfo(periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateOpenResult(NewLotteryPeriodInfo periodInfo, String openResult) {
        GameChildType gameType = GameChildType.getType(periodInfo.getType());

//        String referencePrice = lotteryType.getReferencePrice(openResult);

        String reference = StringUtils.getEmpty();

        mLotteryPeriodDao.updateOpenResult(gameType, periodInfo.getIssue(), reference, openResult, GameOpenMode.MANUAL, null);
        String cachekey = LotteryCacheHelper.findLotteryInfo(periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    public void updateReference(NewLotteryPeriodInfo periodInfo, String reference, JSONObject jsonObject) {
        GameChildType gameType = GameChildType.getType(periodInfo.getType());
        mLotteryPeriodDao.updateOpenResult(gameType, periodInfo.getIssue(), reference, null, GameOpenMode.MANUAL, jsonObject);
        String cachekey = LotteryCacheHelper.findLotteryInfo(periodInfo.getIssue());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateOpenMode(GameChildType gameType, String issue, GameOpenMode mode) {
        mLotteryPeriodDao.updateOpenMode(gameType, issue, mode);
    }

    @Override
    @Transactional
    public void updateAmount(GameChildType gameType, String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount, BigDecimal winAmount2) {
        mLotteryPeriodDao.updateAmount(gameType, issue, betAmount, winAmount, feeAmount, betCount, winCount, winAmount2);
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void queryAll2(GameChildType type, String startTimeString, String endTimeString, GameChildType whereType, Callback<NewLotteryPeriodInfo> callback) {
        mLotteryPeriodDao.queryAll(type, startTimeString, endTimeString, whereType ,callback);
    }

    @Override
    public RowPager<NewLotteryPeriodInfo> queryScrollPage(PageVo pageVo, String issue, GameChildType type, GamePeriodStatus status) {
        return mLotteryPeriodDao.queryScrollPage(pageVo, issue, type, status);
    }

    public NewLotteryPeriodInfo findByTime(String time, GameChildType type) {
        return mLotteryPeriodDao.findByTime(time, type);
    }

    @Override
    public NewLotteryPeriodInfo findCurrentRunning(GameChildType type) {
        DateTime nowDateTime = new DateTime();
        NewLotteryPeriodInfo periodInfo = findByTime(nowDateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS), type);
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
    public NewLotteryPeriodInfo findByIssue(boolean purge, GameChildType gameType, String issue) {
        String cachekey = LotteryCacheHelper.findLotteryInfo(issue);
        NewLotteryPeriodInfo model = CacheManager.getInstance().getObject(cachekey, NewLotteryPeriodInfo.class);
        if(purge || model == null)
        {
            model = mLotteryPeriodDao.findByIssue(gameType, issue);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public List<NewLotteryPeriodInfo> queryByTime(GameChildType lotteryType, String beginTime, String endTime, int limit) {
        return mLotteryPeriodDao.queryByTime(lotteryType, beginTime, endTime, limit);
    }

    @Override
    public long count(GameChildType type, String startTimeString, String endTimeString) {
        return mLotteryPeriodDao.count(type, startTimeString, endTimeString);
    }


}

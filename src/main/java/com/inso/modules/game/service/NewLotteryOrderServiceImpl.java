package com.inso.modules.game.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rg.cache.LotteryCacheHelper;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.dao.NewLotteryOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NewLotteryOrderServiceImpl implements NewLotteryOrderService {

    @Autowired
    private NewLotteryOrderDao mLotteryOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, String issue, GameChildType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, BigDecimal amount, int totalBetCount, BigDecimal singleBetAmount, BigDecimal feemoney, JSONObject remark) {
        mLotteryOrderDao.addOrder(orderno, issue, lotteryType, userInfo, userAttr, betItem, OrderTxStatus.NEW, amount, totalBetCount, singleBetAmount, feemoney, remark);

        deleteCache(userInfo.getId(), lotteryType);
    }

    @Transactional
    public void updateTxStatus(long userid, GameChildType lotteryType, String orderno, OrderTxStatus txStatus, String betItem) {
//        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(lotteryType, orderno, txStatus, null, null, betItem, null, null, null);

        deleteCache(userid, lotteryType);
    }

    @Transactional
    public void updateTxStatusToRealized(long userid, GameChildType lotteryType, String orderno, String openResult, BigDecimal winmoney, NewLotteryPeriodInfo periodInfo, String betItem) {
//        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(lotteryType, orderno, OrderTxStatus.REALIZED, openResult, winmoney, betItem, null, null, periodInfo);

        deleteCache(userid, lotteryType);
    }

    @Transactional
    public void updateTxStatusToFailed(long userid, GameChildType lotteryType, String orderno, String openResult, NewLotteryPeriodInfo periodInfo) {
//        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(lotteryType, orderno, OrderTxStatus.FAILED, openResult, null, null, null, null, periodInfo);

        deleteCache(userid, lotteryType);
    }

    @Override
    public NewLotteryOrderInfo findByNo(GameChildType lotteryType, String orderno) {
        return mLotteryOrderDao.findByNo(lotteryType, orderno);
    }

    @Override
    public void updateCashoutItem(GameChildType lotteryType, String orderno, String betItem) {
        if(lotteryType != RocketType.CRASH)
        {
            return;
        }
        mLotteryOrderDao.updateCashoutItem(lotteryType, orderno, betItem);
    }

//    @Override
//    public NewLotteryOrderInfo findByIssueAndUser(boolean purge, GameChildType lotteryType, String issue, long userid) {
//        if(lotteryType != RocketType.CRASH)
//        {
//            return null;
//        }
//
//        String cachekey = LotteryCacheHelper.findByIssueAndUser(lotteryType, issue, userid);
//
//        if(purge || )
//        return null;
//    }

    @Override
    public List<NewLotteryOrderInfo> queryListByUserid(boolean purge, DateTime fromTime, long userid, GameChildType rgType, int offset) {
        List<NewLotteryOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = LotteryCacheHelper.queryLatestPage_100(userid, rgType);
            list = CacheManager.getInstance().getList(cachekey, NewLotteryOrderInfo.class);

            if(purge || list == null)
            {
                list = mLotteryOrderDao.queryListByUserid(fromTime, userid, rgType,100);
                if(list == null)
                {
                    list = Collections.emptyList();
                }
                // 缓存2分钟
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), 3600);
            }
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int addIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= 10)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }
        return rsList;
    }

    @Override
    public void queryAllByIssue(GameChildType lotteryType, String issue, Callback<NewLotteryOrderInfo> callback) {
        mLotteryOrderDao.queryAllByIssue(lotteryType, issue, callback);
    }

    public void queryAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<NewLotteryOrderInfo> callback)
    {
        mLotteryOrderDao.queryAllByTime(lotteryType, from, toTime, txStatus, callback);
    }

    public void queryAllPendingByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback)
    {
        mLotteryOrderDao.queryAllPendingByTime(lotteryType, from, toTime, callback);
    }

    @Override
    public void statsAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback) {
        mLotteryOrderDao.statsAllByTime(lotteryType, from, toTime, callback);
    }

    @Override
    public RowPager<NewLotteryOrderInfo> queryScrollPage(PageVo pageVo, GameChildType lotteryType, long userid, long agentid, long staffid, GameChildType tbGameType, String systemNo, String issue, OrderTxStatus txStatus, String sortName, String sortOrder) {
        return mLotteryOrderDao.queryScrollPage(pageVo, lotteryType, userid, agentid,staffid, tbGameType, systemNo, issue, txStatus, sortName, sortOrder);
    }

    @Override
    public void queryAllMember(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<NewLotteryOrderInfo> callback) {
        mLotteryOrderDao.queryAllMember(lotteryType, startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback) {
        mLotteryOrderDao.queryAllMemberByTime(lotteryType, startTimeString, endTimeString, callback);
    }

    @Override
    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback) {
        mLotteryOrderDao.statsAllMemberByTime(lotteryType, from, toTime, callback);
    }


    private void deleteCache(long userid, GameChildType gameChildType)
    {
        String cachekey = LotteryCacheHelper.queryLatestPage_100(userid, gameChildType);
        CacheManager.getInstance().delete(cachekey);
    }

}

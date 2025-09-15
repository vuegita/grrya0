package com.inso.modules.game.fm.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.dao.FMOrderDao;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FMOrderServiceImpl implements FMOrderService {

    @Autowired
    private FMOrderDao mFMOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, long rpid, UserInfo userInfo, UserAttr userAttr,
                         BigDecimal buyAmount, BigDecimal return_expected_amount, BigDecimal return_real_rate, long timeHorizon)
    {
        OrderTxStatus txStatus = OrderTxStatus.NEW;
        mFMOrderDao.addOrder(orderno, rpid, userInfo, userAttr, txStatus, buyAmount, return_expected_amount,return_real_rate ,timeHorizon);
        clearUserCache(userInfo.getId(), null);

        //String userCacheKey = MyConstants.DEFAULT_GAME_MODULE_NAME + "_financial_mgr_period_status_user_buy_money_"+ userInfo.getId() + rpid;
        String userCacheKey = GameCacheKeyHelper.queryTotalAmountByUserAndIssue(GameCategory.FINANCIAL_MANAGEMENT, userInfo.getId(),rpid);
        CacheManager.getInstance().delete(userCacheKey);

    }

    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, BigDecimal return_real_amount, BigDecimal feemoney, JSONObject remark){
        mFMOrderDao.updateTxStatus(orderno, txStatus, return_real_amount, feemoney, remark);
    }

    @Override
    public FMOrderInfo findByNo(String orderno) {
        return mFMOrderDao.findByNo(orderno);
    }

    @Override
    public List<FMOrderInfo> queryListByUserid(boolean purge, long userid, OrderTxStatus Status, int offset) {
        List<FMOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = GameCacheKeyHelper.queryOrderLatestPage_status_100(GameCategory.FINANCIAL_MANAGEMENT, userid,Status);
            list = CacheManager.getInstance().getList(cachekey, FMOrderInfo.class);

            if(purge ||list == null )
            {
                // 最新3天的数据的前100条数据
                DateTime dateTime = new DateTime().minusDays(31);
                String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

                list = mFMOrderDao.queryListByUserid(timeString, userid, Status,100);
                if(!CollectionUtils.isEmpty(list))
                {
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_HOUR_5);
                }
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
            if(addIndex >= 90)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }
        return rsList;
    }

    @Override
    public void queryAllByIssue(long issue, Callback<FMOrderInfo> callback) {
        mFMOrderDao.queryAllByIssue(issue, callback);
    }

    @Override
    public FMOrderInfo findByNoAndUserid(boolean purge, long issue, long userid) {
        //String userCacheKey = MyConstants.DEFAULT_GAME_MODULE_NAME + "_financial_mgr_period_status_user_buy_money_"+ userid + issue;
        String userCacheKey = GameCacheKeyHelper.queryTotalAmountByUserAndIssue(GameCategory.FINANCIAL_MANAGEMENT, userid,issue);
        FMOrderInfo mFMOrderInfo = CacheManager.getInstance().getObject(userCacheKey, FMOrderInfo.class);
        if(mFMOrderInfo==null || purge){
            mFMOrderInfo = mFMOrderDao.findByNoAndUserid(issue,userid);
            if(mFMOrderInfo!=null)
            {
                CacheManager.getInstance().setString(userCacheKey, FastJsonHelper.jsonEncode(mFMOrderInfo), CacheManager.EXPIRES_HOUR_5);
            }

        }
        return mFMOrderInfo;
    }

    @Override
    public RowPager<FMOrderInfo> queryScrollPage(PageVo pageVo, FMType lotteryType, long userid, String systemNo, long issue, OrderTxStatus txStatus,long agentid,long staffid) {
        return mFMOrderDao.queryScrollPage(pageVo, lotteryType, userid, systemNo, issue, txStatus,agentid,staffid);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback) {
        mFMOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllByEndtime(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback) {
        mFMOrderDao.queryAllByEndtime(startTimeString, endTimeString, callback);
    }

    @Override
    @Async
    public void clearUserCache(long userid, OrderTxStatus Status)
    {
//        String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.FINANCIAL_MANAGEMENT, null,  userid);
//        CacheManager.getInstance().delete(cachekey);


//        queryListByUserid(true, userid, null,0);
//        queryListByUserid(true, userid, OrderTxStatus.WAITING,0);
//        queryListByUserid(true, userid, OrderTxStatus.REALIZED,0);
//        queryListByUserid(true, userid, OrderTxStatus.FAILED,0);

        clearUserCacheByStatus( userid, null);
        clearUserCacheByStatus( userid, OrderTxStatus.WAITING);
        clearUserCacheByStatus( userid, OrderTxStatus.REALIZED);
        clearUserCacheByStatus( userid, OrderTxStatus.FAILED);
    }

    public void clearUserCacheByStatus(long userid, OrderTxStatus Status){
        String cachekey = GameCacheKeyHelper.queryOrderLatestPage_status_100(GameCategory.FINANCIAL_MANAGEMENT, userid,Status);
        CacheManager.getInstance().delete(cachekey);
    }
}

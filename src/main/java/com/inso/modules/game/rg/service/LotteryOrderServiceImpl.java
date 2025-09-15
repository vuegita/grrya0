package com.inso.modules.game.rg.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.inso.modules.game.GameChildType;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.rg.cache.LotteryCacheHelper;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.dao.LotteryOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class LotteryOrderServiceImpl implements LotteryOrderService {

    @Autowired
    private LotteryOrderDao mLotteryOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, String issue, LotteryRGType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark) {
        mLotteryOrderDao.addOrder(orderno, issue, lotteryType, userInfo, userAttr, betItem, OrderTxStatus.NEW, basicAmount, betCount, amount, feemoney, remark);
    }

//    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, JSONObject remark) {
        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(orderno, txStatus, openResult, winmoney, null, remark);
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus) {
//        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(orderno, txStatus, -1, null, null, null);
    }

    @Transactional
    public void updateTxStatusToRealized(String orderno, long openResult, BigDecimal winmoney) {
        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(orderno, OrderTxStatus.REALIZED, openResult, winmoney, null, null);
    }

    @Transactional
    public void updateTxStatusToFailed(String orderno, long openResult) {
        openResult = LotteryHelper.getOpenResult(openResult);
        mLotteryOrderDao.updateTxStatus(orderno, OrderTxStatus.FAILED, openResult, null, null, null);
    }

    @Override
    public LotteryOrderInfo findByNo(String orderno) {
        return mLotteryOrderDao.findByNo(orderno);
    }

    @Override
    public List<LotteryOrderInfo> queryListByUserid(String createtime, long userid, LotteryRGType rgType, int offset) {
        List<LotteryOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = LotteryCacheHelper.queryLatestPage_100(userid, rgType);
            list = CacheManager.getInstance().getList(cachekey, LotteryOrderInfo.class);

            if(list == null)
            {
                list = mLotteryOrderDao.queryListByUserid(createtime, userid, rgType,100);
                if(list == null)
                {
                    list = Collections.emptyList();
                }
                // 缓存2分钟
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), 120);
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
    public void queryAllByIssue(String issue, Callback<LotteryOrderInfo> callback) {
        mLotteryOrderDao.queryAllByIssue(issue, callback);
    }

    @Override
    public RowPager<LotteryOrderInfo> queryScrollPage(PageVo pageVo, LotteryRGType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder) {
        return mLotteryOrderDao.queryScrollPage(pageVo, lotteryType, userid, agentid,staffid, systemNo, issue, txStatus, sortName, sortOrder);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<LotteryOrderInfo> callback) {
        mLotteryOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback) {
        mLotteryOrderDao.queryAllMemberByTime(startTimeString, endTimeString, callback);
    }

    @Override
    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback) {

    }
}

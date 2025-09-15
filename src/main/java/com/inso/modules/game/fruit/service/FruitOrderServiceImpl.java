package com.inso.modules.game.fruit.service;

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
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitOrderInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.dao.FruitOrderDao;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class FruitOrderServiceImpl implements FruitOrderService {

    @Autowired
    private FruitOrderDao mOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, String issue, FruitType lotteryType, UserInfo userInfo, UserAttr userAttr, FruitBetItemType betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark) {
        mOrderDao.addOrder(orderno, issue, lotteryType, userInfo, userAttr, betItem, OrderTxStatus.NEW, basicAmount, betCount, amount, feemoney, remark);
    }

//    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, FruitBetItemType openResult, BigDecimal winmoney, JSONObject remark) {
        mOrderDao.updateTxStatus(orderno, txStatus, openResult, winmoney, null, remark);
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus) {
        mOrderDao.updateTxStatus(orderno, txStatus, null, null, null, null);
    }

    @Transactional
    public void updateTxStatusToRealized(String orderno, FruitBetItemType openResult, BigDecimal winmoney) {
        mOrderDao.updateTxStatus(orderno, OrderTxStatus.REALIZED, openResult, winmoney, null, null);
    }

    @Transactional
    public void updateTxStatusToFailed(String orderno, FruitBetItemType openResult) {
        mOrderDao.updateTxStatus(orderno, OrderTxStatus.FAILED, openResult, null, null, null);
    }

    @Override
    public FruitOrderInfo findByNo(String orderno) {
        return mOrderDao.findByNo(orderno);
    }

    @Override
    public List<FruitOrderInfo> queryListByUserid(String createtime, long userid, int offset) {
        List<FruitOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.FRUIT, null, userid);
            list = CacheManager.getInstance().getList(cachekey, FruitOrderInfo.class);

            if(list == null)
            {
                list = mOrderDao.queryListByUserid(createtime, userid, 100);
                if(!CollectionUtils.isEmpty(list))
                {
                    // 缓存2分钟
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), 120);
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
    public void queryAllByIssue(String issue, Callback<FruitOrderInfo> callback) {
        mOrderDao.queryAllByIssue(issue, callback);
    }

    @Override
    public RowPager<FruitOrderInfo> queryScrollPage(PageVo pageVo, FruitType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus) {
        return mOrderDao.queryScrollPage(pageVo, lotteryType, userid, agentid,staffid, systemNo, issue, txStatus);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<FruitOrderInfo> callback) {
        mOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllMemberByTime(GameChildType gameChildType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback) {
        mOrderDao.queryAllMemberByTime(startTimeString, endTimeString, callback);
    }

    @Override
    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback) {

    }

}

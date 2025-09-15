package com.inso.modules.game.andar_bahar.service;

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
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABOrderInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.andar_bahar.service.dao.ABOrderDao;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class ABOrderServiceImpl implements ABOrderService {

    @Autowired
    private ABOrderDao mABOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, String issue, ABType lotteryType, UserInfo userInfo, UserAttr userAttr, ABBetItemType betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark) {
        mABOrderDao.addOrder(orderno, issue, lotteryType, userInfo, userAttr, betItem, OrderTxStatus.NEW, basicAmount, betCount, amount, feemoney, remark);
    }

//    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, ABBetItemType openResult, BigDecimal winmoney, JSONObject remark) {
        mABOrderDao.updateTxStatus(orderno, txStatus, openResult, winmoney, null, remark);
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus) {
        mABOrderDao.updateTxStatus(orderno, txStatus, null, null, null, null);
    }

    @Transactional
    public void updateTxStatusToRealized(String orderno, ABBetItemType openResult, BigDecimal winmoney) {
        mABOrderDao.updateTxStatus(orderno, OrderTxStatus.REALIZED, openResult, winmoney, null, null);
    }

    @Transactional
    public void updateTxStatusToFailed(String orderno, ABBetItemType openResult) {
        mABOrderDao.updateTxStatus(orderno, OrderTxStatus.FAILED, openResult, null, null, null);
    }

    @Override
    public ABOrderInfo findByNo(String orderno) {
        return mABOrderDao.findByNo(orderno);
    }

    @Override
    public List<ABOrderInfo> queryListByUserid(String createtime, long userid, int offset) {
        List<ABOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.ANDAR_BAHAR, null, userid);
            list = CacheManager.getInstance().getList(cachekey, ABOrderInfo.class);

            if(list == null)
            {
                list = mABOrderDao.queryListByUserid(createtime, userid, 100);
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
    public void queryAllByIssue(String issue, Callback<ABOrderInfo> callback) {
        mABOrderDao.queryAllByIssue(issue, callback);
    }

    @Override
    public RowPager<ABOrderInfo> queryScrollPage(PageVo pageVo, ABType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder) {
        return mABOrderDao.queryScrollPage(pageVo, lotteryType, userid, agentid,staffid, systemNo, issue, txStatus, sortName, sortOrder);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<ABOrderInfo> callback) {
        mABOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback) {
        mABOrderDao.queryAllMemberByTime(startTimeString,  endTimeString,  callback);
    }

    @Override
    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback) {

    }
}

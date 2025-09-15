package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.red_package.model.RedPBetOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.dao.RedPBetOrderDao;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class RedPBetOrderServiceImpl implements RedPBetOrderService {

    @Autowired
    private RedPBetOrderDao mABOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, long rpid, RedPType lotteryType, UserInfo userInfo, long agentid,
                         String betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark) {
        mABOrderDao.addOrder(orderno, rpid, lotteryType, userInfo, agentid, betItem, OrderTxStatus.NEW, basicAmount, betCount, amount, feemoney, remark);
    }

//    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, JSONObject remark) {
        mABOrderDao.updateTxStatus(orderno, txStatus, openResult, winmoney, null, remark);
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus) {
        mABOrderDao.updateTxStatus(orderno, txStatus, -1, null, null, null);
    }

    @Transactional
    public void updateTxStatusToRealized(String orderno, long openResult, BigDecimal winmoney) {
        mABOrderDao.updateTxStatus(orderno, OrderTxStatus.REALIZED, openResult, winmoney, null, null);
    }

    @Transactional
    public void updateTxStatusToFailed(String orderno, long openResult) {
        mABOrderDao.updateTxStatus(orderno, OrderTxStatus.FAILED, openResult, null, null, null);
    }

    @Override
    public RedPBetOrderInfo findByNo(String orderno) {
        return mABOrderDao.findByNo(orderno);
    }

    @Override
    public List<RedPBetOrderInfo> queryListByUserid(boolean purge, RedPType type, long userid, int offset) {
        List<RedPBetOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.RED_PACKAGE, type, userid);
            list = CacheManager.getInstance().getList(cachekey, RedPBetOrderInfo.class);

            if(list == null)
            {
                // 最新3天的数据的前100条数据
                DateTime dateTime = new DateTime().minusDays(7);
                String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

                list = mABOrderDao.queryListByUserid(type, timeString, userid, 100);
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
    public void queryAllByIssue(long rpid, Callback<RedPBetOrderInfo> callback) {
        mABOrderDao.queryAllByIssue(rpid, callback);
    }

    @Override
    public RowPager<RedPBetOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long rpid, OrderTxStatus txStatus) {
        return mABOrderDao.queryScrollPage(pageVo, lotteryType, userid, systemNo, rpid, txStatus);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPBetOrderInfo> callback) {
        mABOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }
}

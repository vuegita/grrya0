package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import com.inso.modules.game.red_package.model.RedPReceivOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.dao.RedPReceivOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class RedPReceivOrderServiceImpl implements RedPReceivOrderService {

    @Autowired
    private RedPReceivOrderDao mReceivOrderDao;

    @Override
    @Transactional
    public void addOrder(String orderno, long rpid, RedPType type, UserInfo userInfo, UserAttr userAttr, BigDecimal amount, long index, JSONObject remark) {
        OrderTxStatus txStatus = OrderTxStatus.NEW;
        mReceivOrderDao.addOrder(orderno, rpid, type, userInfo, userAttr, txStatus, amount, index, remark);
    }

    @Override
    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, JSONObject remark){
        mReceivOrderDao.updateTxStatus(orderno, txStatus, remark);
    }

    @Override
    public RedPReceivOrderInfo findByNo(String orderno) {
        return mReceivOrderDao.findByNo(orderno);
    }

    @Override
    public List<RedPReceivOrderInfo> queryListByUserid(boolean purge, long userid, int offset) {
        List<RedPReceivOrderInfo> list = null;
        if(offset <= 90)
        {
            String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.RED_PACKAGE, null,  userid);
            list = CacheManager.getInstance().getList(cachekey, RedPReceivOrderInfo.class);

            if(purge || list == null)
            {
                // 最新3天的数据的前100条数据
                DateTime dateTime = new DateTime().minusDays(7);
                String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

                list = mReceivOrderDao.queryListByUserid(timeString, userid, 100);
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
    public void queryAllByRedPId(long id, Callback<RedPReceivOrderInfo> callback) {
        mReceivOrderDao.queryAllByRedPId(id, callback);
    }

    @Override
    public RowPager<RedPReceivOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long issue, long agentid,long staffid, OrderTxStatus txStatus) {
        return mReceivOrderDao.queryScrollPage(pageVo, lotteryType, userid, systemNo, issue, agentid,staffid, txStatus);
    }

    @Override
    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPReceivOrderInfo> callback) {
        mReceivOrderDao.queryAllMember(startTimeString, endTimeString, callback);
    }

    @Override
    @Async
    public void clearUserCache(long userid, RedPType childType)
    {
//        String pageCacheKey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.RED_PACKAGE, childType, userid);
//        CacheManager.getInstance().delete(pageCacheKey);
        queryListByUserid(true, userid, 0);
    }
}

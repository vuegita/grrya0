package com.inso.modules.ad.core.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.cache.EventOrderCacheHelper;
import com.inso.modules.ad.core.helper.AdHelper;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.dao.EventOrderDao;
import com.inso.modules.ad.mall.logical.DeliveryLocationManager;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EventOrderServiceImpl implements EventOrderService {

    /*** 用户下载的东西，21天不能重复下载，不能重复做任务 ***/
    private static final int DEFAULT_EXPIRES_DAY = 15;

    @Autowired
    private EventOrderDao mEventLogDao;

    @Autowired
    private DeliveryLocationManager mDeliveryLocationManager;

    @Override
    @Transactional
    public String createOrder(AdMaterielInfo materielInfo, UserAttr userAttr) {
        String orderno = AdHelper.nextOrderId();

        //     public void addOrder(String orderno, AdMaterielInfo materielInfo, UserAttr userAttr, BigDecimal brokerage, long quanlity, BigDecimal totalAmount, UserInfo merchantInfo,
        //                         long addressid, String targetLocation, OrderTxStatus txStatus);

        mEventLogDao.addOrder(orderno, materielInfo, userAttr, null, 1, materielInfo.getPrice(),
                null, 0, null, null, OrderTxStatus.NEW, null, null);

        String cachekey = EventOrderCacheHelper.queryLatestMaterielIds(userAttr.getUserid());
        CacheManager.getInstance().delete(cachekey);

        clearUserPageCache(userAttr.getUserid());

        // 历史总金额
        String userTotalAmountCachekey = EventOrderCacheHelper.statsHistoryAll(userAttr.getUserid());
        CacheManager.getInstance().delete(userTotalAmountCachekey);
        return orderno;
    }

    public String createOrderByShop(AdMaterielInfo materielInfo, UserAttr userAttr, BigDecimal totalAmount, long quanlity, BigDecimal brokerage,
                                    MallCommodityInfo commodityInfo, long addressid, String location, String buyerPhone, String shopFrom, JSONObject remark) {
        String orderno = AdHelper.nextOrderId();

        mEventLogDao.addOrder(orderno, materielInfo, userAttr, brokerage, quanlity, totalAmount, commodityInfo, addressid, location, buyerPhone, OrderTxStatus.NEW, shopFrom, remark);

        if(userAttr != null)
        {
            String cachekey = EventOrderCacheHelper.queryLatestMaterielIds(userAttr.getUserid());
            CacheManager.getInstance().delete(cachekey);

            clearUserPageCache(userAttr.getUserid());

            // 历史总金额
            String userTotalAmountCachekey = EventOrderCacheHelper.statsHistoryAll(userAttr.getUserid());
            CacheManager.getInstance().delete(userTotalAmountCachekey);
        }
        return orderno;
    }

    @Override
    @Transactional
    public void updateInfo(AdEventOrderInfo logInfo, OrderTxStatus status) {
        mEventLogDao.updateInfo(logInfo.getNo(), status);

        String cachekey = EventOrderCacheHelper.queryLatestMaterielIds(logInfo.getUserid());
        CacheManager.getInstance().delete(cachekey);

        clearUserPageCache(logInfo.getUserid());
    }

    @Transactional
    public void updateShippingInfo(AdEventOrderInfo orderInfo, OrderTxStatus shippingStatus)
    {
        String trachid = null;
        if(shippingStatus == OrderTxStatus.WAITING && orderInfo.getShippingTrackno() == null)
        {
            trachid = AdHelper.nextTrackId();
        }

        mEventLogDao.updateShippingInfo(orderInfo.getNo(), shippingStatus, trachid);

        // 添加物流信息
        mDeliveryLocationManager.addDeliveryLocation(orderInfo, shippingStatus);

        String cachekey = EventOrderCacheHelper.queryLatestMaterielIds(orderInfo.getUserid());
        CacheManager.getInstance().delete(cachekey);
        clearUserPageCache(orderInfo.getUserid());
    }

    @Override
    public AdEventOrderInfo findById(boolean purge, String orderno) {
        return mEventLogDao.findById(orderno);
    }

    @Override
    public AdEventOrderInfo findLatestOrderInfo(boolean purge, long userid, long materielid) {
        DateTime dateTime = getDateTime();
        String cachekey = EventOrderCacheHelper.findLatestOrderInfoByUserAndMaterielid(userid, materielid);
        AdEventOrderInfo orderInfo = CacheManager.getInstance().getObject(cachekey, AdEventOrderInfo.class);
        if(purge || orderInfo == null)
        {
            orderInfo = mEventLogDao.findLatestOrderInfo(dateTime, userid, materielid);
            if(orderInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(orderInfo), CacheManager.EXPIRES_HOUR_5);
            }
        }
        return orderInfo;
    }

    @Override
    public List<AdEventOrderInfo> queryLatestByUser(boolean purge, long userid)
    {
        DateTime dateTime = getDateTime();
        String cachekey = EventOrderCacheHelper.queryLatestByUser(userid);
        List<AdEventOrderInfo> rsList = CacheManager.getInstance().getList(cachekey, AdEventOrderInfo.class);
        if(purge || rsList == null)
        {
            rsList = mEventLogDao.queryByUser(dateTime, userid, 100);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    public BigDecimal findAllHistoryAmountByUser(boolean purge, long userid)
    {
        String cachekey = EventOrderCacheHelper.statsHistoryAll(userid);
        BigDecimal amount = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(purge || amount == null)
        {
            amount = mEventLogDao.findAllHistoryAmountByUser(userid);
            if(amount == null)
            {
                amount = BigDecimal.ZERO;
            }
            CacheManager.getInstance().setString(cachekey, amount.toString());
        }
        return amount;
    }

    public List<AdEventOrderInfo> queryByUserAndTxStatus(boolean purge, long userid, OrderTxStatus txStatus, PageVo pageVo){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(90);

        List<AdEventOrderInfo> list = null;
        if(pageVo.getOffset() <= 90)
        {
            pageVo.setLimit(100);

            String cachekey = EventOrderCacheHelper.queryByUserAndTxStatus(userid, txStatus);
            list = CacheManager.getInstance().getList(cachekey, AdEventOrderInfo.class);

            if(purge ||list == null)
            {
                list = mEventLogDao.queryByUserAndTxStatus(dateTime, userid, txStatus, pageVo.getOffset(), pageVo.getLimit());
                if(list == null)
                {
                    list = Collections.emptyList();
                }
                // 缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_DAY);
            }
        }
        else
        {
            list = mEventLogDao.queryByUserAndTxStatus(dateTime, userid, txStatus, pageVo.getOffset(), pageVo.getLimit());
            return list;
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = pageVo.getOffset(); i < size; i ++)
        {
            if(rsIndex >= 10)
            {
                break;
            }
            rsList.add(list.get(i));
            rsIndex ++;
        }
        return rsList;
    }

    @Override
    public List<Long> queryLatestMaterielIds(boolean purge, long userid) {
        DateTime dateTime = getDateTime();
        String cachekey = EventOrderCacheHelper.queryLatestMaterielIds(userid);
        List<Long> rsList = CacheManager.getInstance().getList(cachekey, Long.class);
        if(purge || rsList == null)
        {
            rsList = mEventLogDao.queryLatestMaterielIds(dateTime, userid, 300);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, AdEventType eventType, Callback<AdEventOrderInfo> callback) {
        mEventLogDao.queryAll(fromTime, toTime, eventType, callback);
    }

    @Override
    public RowPager<AdEventOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status, AdEventType eventType, long materielid) {
        return mEventLogDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status, eventType, materielid);
    }

    private static DateTime getDateTime()
    {
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.minusDays(DEFAULT_EXPIRES_DAY);
        return dateTime;
    }

    public void clearUserPageCache(long userid)
    {
        String newCachekey = EventOrderCacheHelper.queryByUserAndTxStatus(userid, OrderTxStatus.NEW);
        CacheManager.getInstance().delete(newCachekey);

        String realizedCachekey = EventOrderCacheHelper.queryByUserAndTxStatus(userid, OrderTxStatus.REALIZED);
        CacheManager.getInstance().delete(realizedCachekey);

        String failedCachekey = EventOrderCacheHelper.queryByUserAndTxStatus(userid, OrderTxStatus.FAILED);
        CacheManager.getInstance().delete(failedCachekey);

        String allCacheKey = EventOrderCacheHelper.queryLatestByUser(userid);
        CacheManager.getInstance().delete(allCacheKey);


    }
}

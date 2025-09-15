package com.inso.modules.web.activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.cache.ActivityCacleKeyHelper;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.service.dao.ActivityDao;
import com.inso.modules.web.team.model.TeamBusinessType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.Cache;

import java.math.BigDecimal;

@Service
public class ActivityServiceImpl implements ActivityService{

    @Autowired
    private ActivityDao mActivityDao;

    @Override
    public long add(String title, ActivityBusinessType businessType,
                    BigDecimal limitMinInvesAmount, long limitMinInviteCount, BigDecimal basicPresentAmount, String extraPresentTier,
                    DateTime beginTime, DateTime endTime) {

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        long id = mActivityDao.add(title, null, businessType, currencyType,
                limitMinInvesAmount, limitMinInviteCount, basicPresentAmount, extraPresentTier,
                OrderTxStatus.NEW, beginTime, endTime);

        deleteCache(id, businessType);
        return id;
    }

    @Override
    public void updateInfo(ActivityInfo entity, String title, long finishInviteCount, long finishInvesCount, BigDecimal finishInvesAmount, BigDecimal finishPresentAmount, OrderTxStatus txStatus, JSONObject remark) {
        mActivityDao.updateInfo(entity.getId(), title, finishInviteCount, finishInvesCount, finishInvesAmount, finishPresentAmount, txStatus, remark);

        ActivityBusinessType businessType = ActivityBusinessType.getType(entity.getBusinessType());
        deleteCache(entity.getId(), businessType);
    }

    @Override
    public ActivityInfo findById(boolean purge, long id) {
        String cacheckey = ActivityCacleKeyHelper.findById(id);
        ActivityInfo entity = CacheManager.getInstance().getObject(cacheckey, ActivityInfo.class);
        if(purge || entity == null)
        {
            entity = mActivityDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cacheckey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public ActivityInfo findLatestActive(boolean purge, ActivityBusinessType businessType) {
        String cacheckey = ActivityCacleKeyHelper.findLatestActive(businessType);
        DateTime dateTime = DateTime.now().minusDays(21);
        ActivityInfo entity = CacheManager.getInstance().getObject(cacheckey, ActivityInfo.class);
        if(purge || entity == null)
        {
            entity = mActivityDao.findLatest(dateTime, businessType);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cacheckey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public void deleteById(ActivityInfo entity) {
        mActivityDao.deleteById(entity.getId());

        ActivityBusinessType businessType = ActivityBusinessType.getType(entity.getBusinessType());
        deleteCache(entity.getId(), businessType);
    }

    @Override
    public RowPager<ActivityInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ActivityBusinessType businessType, OrderTxStatus status) {
        return mActivityDao.queryScrollPage(pageVo, agentid, staffid, businessType, status);
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, Callback<ActivityInfo> callback) {
        mActivityDao.queryAll(fromTime, toTime, callback);
    }


    private void deleteCache(long id, ActivityBusinessType businessType)
    {
        if(id > 0)
        {
            String cacheckey = ActivityCacleKeyHelper.findById(id);
            CacheManager.getInstance().delete(cacheckey);
        }

        if(businessType != null)
        {
            String cacheckey = ActivityCacleKeyHelper.findLatestActive(businessType);
            CacheManager.getInstance().delete(cacheckey);
        }

    }
}

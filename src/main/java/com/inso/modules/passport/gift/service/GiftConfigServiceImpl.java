package com.inso.modules.passport.gift.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.gift.cache.GiftCacheUtils;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import com.inso.modules.passport.gift.service.dao.GiftDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class GiftConfigServiceImpl implements GiftConfigService {

    @Autowired
    private GiftDao mGiftDao;

    @Override
    public void add(String title, String desc, GiftTargetType targetType, GiftPeriodType periodType, BigDecimal presentAmount, BigDecimal limitAmount, long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable) {
        mGiftDao.add(title, desc, targetType, periodType, presentAmount, limitAmount, sort, status, presentAmountArrValue, presentAmountArrEnable, null);
        deleteCache(-1);
    }

    @Override
    public void update(GiftConfigInfo entityInfo, String title, String desc, BigDecimal presentAmount, BigDecimal limitAmount, long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable) {
        mGiftDao.update(entityInfo.getId(), title, desc, presentAmount, limitAmount, sort, status, presentAmountArrValue, presentAmountArrEnable);
        deleteCache(entityInfo.getId());
    }

    @Override
    public GiftConfigInfo findById(boolean purge, long id) {
        String cachekey = GiftCacheUtils.findById(id);
        GiftConfigInfo entity = CacheManager.getInstance().getObject(cachekey, GiftConfigInfo.class);
        if(purge || entity == null)
        {
            entity = mGiftDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public List<GiftConfigInfo> queryAll(boolean purge) {
        String cachekey = GiftCacheUtils.queryAll();
        List<GiftConfigInfo> rsList = CacheManager.getInstance().getList(cachekey, GiftConfigInfo.class);
        if(purge || rsList == null)
        {
            rsList = mGiftDao.queryAll(Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<GiftConfigInfo> queryScrollPage(PageVo pageVo, GiftTargetType targetType, Status status) {
        return mGiftDao.queryScrollPage(pageVo, targetType, status);
    }

    private void deleteCache(long id)
    {
        if(id > 0)
        {
            String cachekey = GiftCacheUtils.findById(id);
            CacheManager.getInstance().delete(cachekey);
        }

        String cachekey2 = GiftCacheUtils.queryAll();
        CacheManager.getInstance().delete(cachekey2);
    }
}

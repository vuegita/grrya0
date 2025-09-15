package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.cache.MerchantPromotionCacheHelper;
import com.inso.modules.ad.mall.model.PromotionInfo;
import com.inso.modules.ad.mall.service.dao.PromotionDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromotionServiceImpl implements PromotionService{

    @Autowired
    private PromotionDao mPromotionDao;


    @Override
    public void addOrder(UserInfo userInfo, BigDecimal price, BigDecimal totalAmount) {
        mPromotionDao.addOrder(userInfo, price, totalAmount, Status.ENABLE, null);

        long userid = userInfo.getId();
        String cachekey = MerchantPromotionCacheHelper.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateInfo(PromotionInfo entity, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject) {
        mPromotionDao.updateInfo(entity.getId(), price, totalAmount, status, jsonObject);

        long userid = entity.getUserid();
        String cachekey = MerchantPromotionCacheHelper.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public PromotionInfo findByUserId(boolean purge, long userid) {
        String cachekey = MerchantPromotionCacheHelper.findByUserId(userid);
        PromotionInfo entity = CacheManager.getInstance().getObject(cachekey, PromotionInfo.class);
        if(purge || entity == null)
        {
            entity = mPromotionDao.findByUserId(userid);
            if(entity == null)
            {
                entity = new PromotionInfo();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
        }
        if(entity == null || entity.getId() <= 0)
        {
            entity = null;
        }
        return entity;
    }

    @Override
    public RowPager<PromotionInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status) {
        return mPromotionDao.queryScrollPage(pageVo, agentid, staffid, userid, status);
    }
}

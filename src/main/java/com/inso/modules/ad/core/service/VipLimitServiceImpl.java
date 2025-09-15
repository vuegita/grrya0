package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.cache.AdVipLimitCacheHelper;
import com.inso.modules.ad.core.cache.UserVipLimitCacheHelper;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.service.dao.VipLimitDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class VipLimitServiceImpl implements VipLimitService{

    @Autowired
    private VipLimitDao mVipLimitDao;

    @Override
    public void add(long vipid, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                    long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                    long buyCountOfDay, BigDecimal buyMoneyOfDay, BigDecimal maxMoneyOfSingle)
    {
        mVipLimitDao.add(vipid, totalMoneyOfDay, freeMoneyOfDay, inviteCountOfDay, inviteMoneyOfDay, buyCountOfDay, buyMoneyOfDay, Status.DISABLE, maxMoneyOfSingle);
        String adCachekey = UserVipLimitCacheHelper.queryAllEnable(VIPType.AD);
        CacheManager.getInstance().delete(adCachekey);
    }

    @Override
    public void updateInfo(AdVipLimitInfo limitInfo, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                           long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                           long buyCountOfDay, BigDecimal buyMoneyOfDay,
                           BigDecimal maxMoneyOfSingle, long paybackPeriod, Status status,
                           BigDecimal lv1RebateBalanceRate, BigDecimal lv2RebateBalanceRate,
                           BigDecimal lv1WithdrawlRate, BigDecimal lv2WithdrawlRate) {

        mVipLimitDao.updateInfo(limitInfo.getId(), totalMoneyOfDay, freeMoneyOfDay,
                inviteCountOfDay, inviteMoneyOfDay,
                buyCountOfDay, buyMoneyOfDay, maxMoneyOfSingle,
                paybackPeriod, status,
                lv1RebateBalanceRate, lv2RebateBalanceRate,
                lv1WithdrawlRate, lv2WithdrawlRate
        );

        String cachekey = AdVipLimitCacheHelper.findByVipId(limitInfo.getVipid());
        CacheManager.getInstance().delete(cachekey);
        String adCachekey = UserVipLimitCacheHelper.queryAllEnable(VIPType.AD);
        CacheManager.getInstance().delete(adCachekey);
    }

    @Override
    public AdVipLimitInfo findById(boolean purge, long id) {
//        String cachekey = AdVipLimitCacheHelper.findById(id);
//        AdVipLimitInfo model = CacheManager.getInstance().getObject(cachekey, AdVipLimitInfo.class);
//        if(purge || model == null)
//        {
//            model = mVipLimitDao.findById(id);
//            if(model != null)
//            {
//                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
//            }
//        }
        return mVipLimitDao.findById(id);
    }

    @Override
    public AdVipLimitInfo findByVipId(boolean purge, long vipid) {
        String cachekey = AdVipLimitCacheHelper.findByVipId(vipid);
        AdVipLimitInfo model = CacheManager.getInstance().getObject(cachekey, AdVipLimitInfo.class);
        if(purge || model == null)
        {
            model = mVipLimitDao.findByVipId(vipid);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }


    @Override
    public RowPager<AdVipLimitInfo> queryScrollPage(PageVo pageVo, Status forceVipStatus) {
        return mVipLimitDao.queryScrollPage(pageVo, forceVipStatus);
    }

    @Override
    public List<AdVipLimitInfo> queryAllEnable(boolean purge, VIPType type) {

        String cachekey = UserVipLimitCacheHelper.queryAllEnable(type);
        List<AdVipLimitInfo> list = CacheManager.getInstance().getList(cachekey, AdVipLimitInfo.class);
        if(purge || list == null || list.size()==0)
        {
            list = mVipLimitDao.queryAllEnable(type);
            if(list == null)
            {
                list = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_DAY);
        }
        return list;

    }
}

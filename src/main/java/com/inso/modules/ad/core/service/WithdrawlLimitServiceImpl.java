package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.cache.AdWithdrawalLimitCacheHelper;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.ad.core.service.dao.WithdrawlLimitDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WithdrawlLimitServiceImpl implements WithdrawlLimitService{

    @Autowired
    private WithdrawlLimitDao mWithdrawlLimitDao;

    @Override
    @Transactional
    public void add(UserInfo userInfo, BigDecimal amount) {
        mWithdrawlLimitDao.add(userInfo, amount);
    }

    @Override
    @Transactional
    public void updateInfo(long userid, BigDecimal amount) {
        mWithdrawlLimitDao.updateInfo(userid, amount);

        String cachekey = AdWithdrawalLimitCacheHelper.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public WithdrawlLimitInfo findByUserId(boolean purge, long userid) {

        String cachekey = AdWithdrawalLimitCacheHelper.findByUserId(userid);
        WithdrawlLimitInfo model = CacheManager.getInstance().getObject(cachekey, WithdrawlLimitInfo.class);
        if(purge || model == null)
        {
            model = mWithdrawlLimitDao.findByUserId(userid);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }

        return model;
    }

    @Override
    public RowPager<WithdrawlLimitInfo> queryScrollPage(PageVo pageVo, Status status, long userid) {
        return mWithdrawlLimitDao.queryScrollPage(pageVo, status, userid);
    }
}

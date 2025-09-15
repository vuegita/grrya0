package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.red_package.cache.RedPStaffLimitCache;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.game.red_package.service.dao.RedPStaffLimitDao;
import com.inso.modules.passport.user.model.UserAttr;

@Service
public class RedPStaffLimitServiceImpl implements RedPStaffLimitService {

    @Autowired
    private RedPStaffLimitDao mRedPConfigDao;

    @Override
    public void addConfig(UserAttr staffAttrInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark) {
        mRedPConfigDao.addConfig(staffAttrInfo, maxMoneyOfSingle, maxMoneyOfDay, maxCountOfDay, status, remark);
    }

    @Override
    public void updateInfo(RedPStaffLimit limitInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark) {
        mRedPConfigDao.updateInfo(limitInfo.getId(), maxMoneyOfSingle, maxMoneyOfDay, maxCountOfDay, status, remark);
        String cachekey = RedPStaffLimitCache.findByStaffid(limitInfo.getStaffid());
        CacheManager.getInstance().delete(cachekey);
    }
    @Override
    public RedPStaffLimit findById(long id) {
        return mRedPConfigDao.findById(id);
    }

    @Override
    public RedPStaffLimit findByStaffId(boolean purge, long staffid) {

        String cachekey = RedPStaffLimitCache.findByStaffid(staffid);
        RedPStaffLimit limit = CacheManager.getInstance().getObject(cachekey, RedPStaffLimit.class);

        if(purge || limit == null)
        {
            limit = mRedPConfigDao.findByStaffId(staffid);

            if(limit == null)
            {
                limit = new RedPStaffLimit();
                limit.setMaxCountOfDay(-1);
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(limit), CacheManager.EXPIRES_DAY);
        }


        return limit;
    }

    @Override
    public void deleteById(RedPStaffLimit limitInfo) {
        mRedPConfigDao.deleteById(limitInfo.getId());
        String cachekey = RedPStaffLimitCache.findByStaffid(limitInfo.getStaffid());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public RowPager<RedPStaffLimit> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status) {
        return mRedPConfigDao.queryScrollPage(pageVo, agentid, staffid, status);
    }
}

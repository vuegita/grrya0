package com.inso.modules.passport.returnwater.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.cache.ReturnWaterLogCountCacheHelper;
import com.inso.modules.passport.returnwater.service.dao.ReturnWaterLogCountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnWaterLogCountServiceImpl implements ReturnWaterLogCountService{

    @Autowired
    private ReturnWaterLogCountDao mReturnWaterLogCountDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addLog(long userid, String username)
    {
        try {
            mReturnWaterLogCountDao.addLog(userid, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCount(long userid, int level) {
        try {
            mReturnWaterLogCountDao.updateCount(userid, level);
        } catch (Exception e) {
        }
    }

    @Override
    public ReturnWaterLog findByUserid(boolean purge, long userid) {
        String cachekey = ReturnWaterLogCountCacheHelper.findByUser(userid);

        ReturnWaterLog model = CacheManager.getInstance().getObject(cachekey, ReturnWaterLog.class);
        if(purge || model == null)
        {
            model = mReturnWaterLogCountDao.findByUserid(userid);
            if(model == null)
            {
                //addLog(userid, username);
                model = mReturnWaterLogCountDao.findByUserid(userid);
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
        }
        return model;
    }

    @Override
    public RowPager<ReturnWaterLog> queryScrollPage(PageVo pageVo, long userid) {
        return mReturnWaterLogCountDao.queryScrollPage(pageVo, userid);
    }
}

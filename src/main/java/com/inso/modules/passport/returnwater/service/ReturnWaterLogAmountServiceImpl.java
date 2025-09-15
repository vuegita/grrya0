package com.inso.modules.passport.returnwater.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.returnwater.cache.ReturnWaterCache;
import com.inso.modules.passport.returnwater.cache.ReturnWaterLogAmountCacheHelper;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.dao.ReturnWaterLogAmountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ReturnWaterLogAmountServiceImpl implements ReturnWaterLogAmountService {

    @Autowired
    private ReturnWaterLogAmountDao mReturnWaterLogDao;

//    @Transactional
//    public void addLog(long userid, String username, FundAccountType accountType, ICurrencyType currencyType)
//    {
//        mReturnWaterLogDao.addLog(userid, username, accountType, currencyType);
//    }

//    @Transactional
//    public void updateChildCount(long userid, FundAccountType accountType, ICurrencyType currencyType, int level)
//    {
//        mReturnWaterLogDao.updateCount(userid, accountType, currencyType, level);
//    }

    public void updateTotalAmount(long userid, String username, ICurrencyType currencyType, int level, BigDecimal amount)
    {
        FundAccountType accountType = FundAccountType.Spot;
        ReturnWaterLog logInfo = findByUserid(MyEnvironment.isDev(), userid, username, currencyType);
        if(logInfo != null)
        {
            mReturnWaterLogDao.updateAmount(userid, accountType, currencyType, level, amount);
        }
    }


    public ReturnWaterLog findByUserid(boolean purge, long userid, String username, ICurrencyType currencyType)
    {
        FundAccountType fundAccountType = FundAccountType.Spot;
        String cachekey = ReturnWaterCache.findReturnWaterLogByUserid(userid, fundAccountType, currencyType);

        ReturnWaterLog log = CacheManager.getInstance().getObject(cachekey, ReturnWaterLog.class);

        if(purge || log == null)
        {
            log = mReturnWaterLogDao.findByUserid(userid, fundAccountType, currencyType);
            if(log == null)
            {
                mReturnWaterLogDao.addLog(userid, username, fundAccountType, currencyType);
                this.clearWaterLogAmountCache(userid);
                log = mReturnWaterLogDao.findByUserid(userid, fundAccountType, currencyType);
            }

            if(log != null)
            {
                // 缓存2分钟
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(log), 120);
            }
        }

        return log;
    }

    @Override
    public List<ReturnWaterLog> queryByUser(boolean purge, long userid) {
        FundAccountType fundAccountType = FundAccountType.Spot;
        String cachekey = ReturnWaterLogAmountCacheHelper.queryListByUser(userid);

        List<ReturnWaterLog> rsList = CacheManager.getInstance().getList(cachekey, ReturnWaterLog.class);

        if(purge || rsList == null)
        {
            rsList = mReturnWaterLogDao.queryByUser(userid, fundAccountType);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }

            if(rsList != null)
            {
                // 缓存5分钟
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 60);
            }
        }
        return rsList;
    }

    @Override
    public RowPager<ReturnWaterLog> queryScrollPageBy(PageVo pageVo, long userid) {
        return mReturnWaterLogDao.queryScrollPage(pageVo, userid);
    }

    public void clearWaterLogAmountCache(long userid)
    {
        String cachekey = ReturnWaterLogAmountCacheHelper.queryListByUser(userid);
        CacheManager.getInstance().delete(cachekey);

    }


}

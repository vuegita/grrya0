package com.inso.modules.passport.returnwater.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.returnwater.cache.ReturnWaterLogAmountCacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.passport.returnwater.cache.ReturnWaterCache;
import com.inso.modules.passport.business.model.ReturnWaterLogDetail;
import com.inso.modules.passport.returnwater.service.dao.ReturnWaterLogDetailDao;

@Service
public class ReturnWaterLogDetailServiceImpl implements ReturnWaterLogDetailService{

    private static Log LOG = LogFactory.getLog(ReturnWaterLogDetailServiceImpl.class);

    @Autowired
    private ReturnWaterLogDetailDao mReturnWaterLogDetailDao;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogAmountService;


    @Transactional
    public void addLogDetail(int level, UserInfo userInfo, FundAccountType accountType, ICurrencyType currencyType, UserInfo childUserInfo)
    {
        mReturnWaterLogDetailDao.addLog(level, userInfo, accountType, currencyType, childUserInfo);
        //mReturnWaterLogService.updateChildCount(userInfo.getId(), accountType, currencyType, level);
    }


    @Override
    public void updateAmount(int level, UserInfo userInfo, UserInfo childUserInfo, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount) {
        ReturnWaterLogDetail logDetail = findById(MyEnvironment.isDev(), level, userInfo, childUserInfo, accountType, currencyType);
        if(logDetail != null)
        {
            mReturnWaterLogDetailDao.updateAmount(level, userInfo.getId(), childUserInfo.getId(), accountType, currencyType, amount);

            mReturnWaterLogAmountService.updateTotalAmount(userInfo.getId(), userInfo.getName(), currencyType, level, amount);


            String cachekey = ReturnWaterCache.queryReturnWaterLogByUserid(userInfo.getId(), level);
            CacheManager.getInstance().delete(cachekey);
        }
    }


    public ReturnWaterLogDetail findById(boolean purge, int level, UserInfo userInfo, UserInfo childUserInfo, FundAccountType accountType, ICurrencyType currencyType)
    {
        String cachekey = ReturnWaterLogAmountCacheHelper.findById(level, userInfo.getId(), childUserInfo.getId(), accountType, currencyType);

        ReturnWaterLogDetail model = CacheManager.getInstance().getObject(cachekey, ReturnWaterLogDetail.class);
        if(purge || model == null)
        {
            model = mReturnWaterLogDetailDao.findById(level, userInfo.getId(), childUserInfo.getId(), accountType, currencyType);

            if(model == null)
            {
                addLog(level, userInfo, childUserInfo, accountType, currencyType);
                model = mReturnWaterLogDetailDao.findById(level, userInfo.getId(), childUserInfo.getId(), accountType, currencyType);
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
        }

        return model;
    }

    private void addLog(int level, UserInfo userInfo, UserInfo childUserInfo, FundAccountType accountType, ICurrencyType currencyType)
    {
        try {
            mReturnWaterLogDetailDao.addLog(level, userInfo, accountType, currencyType, childUserInfo);

            String cachekey = ReturnWaterCache.queryReturnWaterLogByUserid(userInfo.getId(), level);
            CacheManager.getInstance().delete(cachekey);

        } catch (Exception e) {
            LOG.error("add log detail error:", e);
        }
    }

    public List<ReturnWaterLogDetail> queryByUserid(boolean purge, long userid, int level, int offset, int limit)
    {
        String cachekey = ReturnWaterCache.queryReturnWaterLogByUserid(userid, level);
        List<ReturnWaterLogDetail> list = CacheManager.getInstance().getList(cachekey, ReturnWaterLogDetail.class);

        //purge = true;
        if(purge || list == null)
        {
            list = mReturnWaterLogDetailDao.queryByUserid(userid, level, 100);
            if(CollectionUtils.isEmpty(list))
            {
                list = Collections.emptyList();
            }
            // 缓存5分钟
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), 120);
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= pagesize)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }

        return rsList;
    }

}

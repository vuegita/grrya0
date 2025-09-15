package com.inso.modules.coin.cloud_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.cache.CloudProfitConfigCacleKeyHelper;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.coin.cloud_mining.service.dao.CloudProfitConfigDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class CloudProfitConfigServiceImpl implements CloudProfitConfigService {

    @Autowired
    private CloudProfitConfigDao mProfitConfigDao;

    @Override
    public long add(long days, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status) {
        long id = mProfitConfigDao.add(days, level, minAmount, dailyRate, status);
//        deleteCache(currency, -1);

        deleteCache(-1);
        return id;
    }

    @Override
    public void updateInfo(CloudProfitConfigInfo entity, BigDecimal dailyRate, BigDecimal minAmount, long level, Status status) {
        mProfitConfigDao.updateInfo(entity.getId(), dailyRate, minAmount, level, status);

        deleteCache(entity.getId());
    }

    @Override
    public CloudProfitConfigInfo findById(boolean purge, long id) {
        String cachekey = CloudProfitConfigCacleKeyHelper.findById(id);
        CloudProfitConfigInfo recordInfo = CacheManager.getInstance().getObject(cachekey, CloudProfitConfigInfo.class);
        if(purge || recordInfo == null)
        {
            recordInfo = mProfitConfigDao.findById(id);
            if(recordInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(recordInfo));
            }
        }
        return recordInfo;
    }

    @Override
    public RowPager<CloudProfitConfigInfo> queryScrollPage(PageVo pageVo, long days, Status status){
        return mProfitConfigDao.queryScrollPage(pageVo, days, status);
    }

    @Override
    public List<CloudProfitConfigInfo> queryAllList(boolean purge) {
        String cachekey = CloudProfitConfigCacleKeyHelper.queryAllList();
        List<CloudProfitConfigInfo> rsList = CacheManager.getInstance().getList(cachekey, CloudProfitConfigInfo.class);
        if(purge || rsList == null)
        {
            rsList = mProfitConfigDao.queryAllList(Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public List<CloudProfitConfigInfo> queryAllListByDays(long days) {
        return mProfitConfigDao.queryAllListByDays(days);
    }

    private void deleteCache(long id)
    {
        if(id > 0)
        {
            String cachekey = CloudProfitConfigCacleKeyHelper.findById(id);
            CacheManager.getInstance().delete(cachekey);
        }

        queryAllList(true);
    }

}

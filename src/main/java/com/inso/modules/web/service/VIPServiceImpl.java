package com.inso.modules.web.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.cache.VIPCacheHelper;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.dao.VIPDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Service
public class VIPServiceImpl implements VIPService{

    @Autowired
    private VIPDao mVIPDao;

    @Override
    @Transactional
    public void addVIPLevel(VIPType vipType, long level, String name, BigDecimal price) {
        mVIPDao.addVIPLevel(vipType, level, name, Status.DISABLE, price);

        String adCachekey = VIPCacheHelper.queryAllEnable(vipType);
        CacheManager.getInstance().delete(adCachekey);
    }

    @Override
    @Transactional
    public void updateInfo(VIPInfo vipInfo, Status status, String name, BigDecimal price, long level) {
        mVIPDao.updateInfo(vipInfo.getId(), status, name, price,level);

        String singleCacheKey = VIPCacheHelper.findById(vipInfo.getId());
        CacheManager.getInstance().delete(singleCacheKey);

        String listCacheKey = VIPCacheHelper.queryAllEnable(VIPType.getType(vipInfo.getType()));
        CacheManager.getInstance().delete(listCacheKey);

        if(vipInfo.getLevel() == 0)
        {
            String freeCacheKey = VIPCacheHelper.findFree(VIPType.getType(vipInfo.getType()));
            CacheManager.getInstance().delete(freeCacheKey);
        }

    }

    @Override
    @Transactional
    public List<VIPInfo> queryAllEnable(boolean purge, VIPType vipType) {
        String cachekey = VIPCacheHelper.queryAllEnable(vipType);
        List<VIPInfo> list = CacheManager.getInstance().getList(cachekey, VIPInfo.class);
        if(purge || list == null)
        {
            list = mVIPDao.queryAllStatus(vipType, Status.ENABLE);
            if(list == null)
            {
                list = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_DAY);
        }
        return list;
    }

    public List<VIPInfo> queryAll(VIPType vipType) {
        return mVIPDao.queryAllStatus(vipType, null);
    }

    @Override
    public VIPInfo findById(boolean purge, long id) {
        String cachekey = VIPCacheHelper.findById(id);
        VIPInfo model = CacheManager.getInstance().getObject(cachekey, VIPInfo.class);
        if(purge || model == null)
        {
            model = mVIPDao.findById(id);
            if(model == null)
            {
                model = new VIPInfo();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
        }
        if(model.getId() <= 0)
        {
            return null;
        }
        return model;
    }

    public VIPInfo findFree(boolean purge, VIPType vipType) {
        String cachekey = VIPCacheHelper.findFree(vipType);
        VIPInfo model = CacheManager.getInstance().getObject(cachekey, VIPInfo.class);
        if(purge || model == null)
        {
            model = mVIPDao.findByLevel(vipType, 0);
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
        }
        return model;
    }

    @Override
    public long findMaxLevel(VIPType vipType) {
        return mVIPDao.findMaxLevel(vipType);
    }

    @Override
    @Transactional
    public RowPager<VIPInfo> queryScrollPage(PageVo pageVo, VIPType vipType, Status status) {
        return mVIPDao.queryScrollPage(pageVo, vipType, status);
    }
}

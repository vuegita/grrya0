package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.cache.DispatchConfigCacheHelper;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.dao.MallDispatchConfigDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MallDispatchConfigServiceImpl implements MallDispatchConfigService{


    @Autowired
    private MallDispatchConfigDao mallDispatchConfigDao;

    @Override
    public void addCategory(MallStoreLevel levelType, long minCount, long maxCount) {
        mallDispatchConfigDao.addCategory(levelType, Status.ENABLE, minCount, maxCount);

        String allCachekey = DispatchConfigCacheHelper.queryAll();
        CacheManager.getInstance().delete(allCachekey);
    }

    @Override
    public void updateInfo(MallDispatchConfigInfo entity, Status status, long minCount, long maxCount) {
        mallDispatchConfigDao.updateInfo(entity.getId(), status, minCount, maxCount);
        String cachekey = DispatchConfigCacheHelper.findByKey(entity.getLevel());
        CacheManager.getInstance().delete(cachekey);

        String allCachekey = DispatchConfigCacheHelper.queryAll();
        CacheManager.getInstance().delete(allCachekey);
    }

    @Override
    public MallDispatchConfigInfo findByKey(boolean purge, MallStoreLevel levelType) {
        String cachekey = DispatchConfigCacheHelper.findByKey(levelType.getKey());
        MallDispatchConfigInfo entity = CacheManager.getInstance().getObject(cachekey, MallDispatchConfigInfo.class);
        if(purge || entity == null)
        {
            entity = mallDispatchConfigDao.findByKey(levelType);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public RowPager<MallDispatchConfigInfo> queryScrollPage(PageVo pageVo) {
        return mallDispatchConfigDao.queryScrollPage(pageVo);
    }

    @Override
    public List<MallDispatchConfigInfo> queryAll(boolean purge) {
        String cachekey = DispatchConfigCacheHelper.queryAll();
        List<MallDispatchConfigInfo> rsList = CacheManager.getInstance().getList(cachekey, MallDispatchConfigInfo.class);
        if(purge || rsList == null)
        {
            rsList = mallDispatchConfigDao.queryAll(Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }
}

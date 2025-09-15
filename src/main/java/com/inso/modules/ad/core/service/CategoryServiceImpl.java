package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.cache.AdCategoryCacheHelper;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.service.dao.CategoryDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryDao mCategoryDao;

    @Override
    public void addCategory(String key, String name, BigDecimal returnRate, Status status, BigDecimal beginPrice, BigDecimal endPrice) {
        mCategoryDao.addCategory(key, name, returnRate, Status.ENABLE, beginPrice, endPrice);
    }

    @Override
    public void updateInfo(AdCategoryInfo category, Status status, BigDecimal returnRate, String name, BigDecimal beginPrice, BigDecimal endPrice) {
        mCategoryDao.updateInfo(category.getId(), status, returnRate, name, beginPrice, endPrice);

        String cachekey = AdCategoryCacheHelper.queryAllEnable();
        CacheManager.getInstance().delete(cachekey);

        String singleCachekey = AdCategoryCacheHelper.findById(category.getId());
        CacheManager.getInstance().delete(singleCachekey);
    }

    @Override
    public AdCategoryInfo findByKey(boolean purge, String key) {
        String cachekey = AdCategoryCacheHelper.findByKey(key);
        AdCategoryInfo model = CacheManager.getInstance().getObject(cachekey, AdCategoryInfo.class);
        if(purge || model == null)
        {
            model = mCategoryDao.findByKey(key);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
            }
        }
        return model;
    }

    @Override
    public AdCategoryInfo findById(boolean purge, long id) {
        String cachekey = AdCategoryCacheHelper.findById(id);
        AdCategoryInfo model = CacheManager.getInstance().getObject(cachekey, AdCategoryInfo.class);
        if(purge || model == null)
        {
            model = mCategoryDao.findById(id);
            if(model == null)
            {
                model = new AdCategoryInfo();
                model.setId(-1);
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
        }
        if(model.getId() <= 0)
        {
            model = null;
        }
        return model;
    }

    @Override
    public List<AdCategoryInfo> queryAllEnable(boolean purge) {
        String cachekey = AdCategoryCacheHelper.queryAllEnable();
        List<AdCategoryInfo> rsList = CacheManager.getInstance().getList(cachekey, AdCategoryInfo.class);
        if(purge || rsList == null)
        {
            rsList = mCategoryDao.queryAllByStatus(Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    public List<AdCategoryInfo> queryAll() {
        return mCategoryDao.queryAllByStatus(null);
    }

    @Override
    public RowPager<AdCategoryInfo> queryScrollPage(PageVo pageVo, Status status) {
        return mCategoryDao.queryScrollPage(pageVo, status);
    }
}

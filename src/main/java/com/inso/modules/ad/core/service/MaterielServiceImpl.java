package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.cache.MaterielCacheHelper;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielDetailInfo;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.dao.MaterielDao;
import com.inso.modules.ad.core.service.dao.MaterielDetailDao;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MaterielServiceImpl implements MaterielService{

    @Autowired
    private MaterielDao materielDao;

    @Autowired
    private MaterielDetailDao materielDetailDao;

    @Override
    @Transactional
    public void add(String key, long categoryid, String name, String desc, Status status, String thumb, String introImg, String jumpUrl,
                    BigDecimal price, String provider, String admin, AdEventType eventType, long limitMinDay, int expiresDay,
                    String content, String sizes, String images) {
        long id = materielDao.add(key, categoryid, name, desc, status, thumb, introImg, jumpUrl, price, provider, admin, eventType, limitMinDay, expiresDay);

        if(eventType == AdEventType.SHOP)
        {
            materielDetailDao.add(id, content, sizes, images);
        }

        deleteCache(0);
        deleteCache(categoryid);
    }

    @Override
    @Transactional
    public void updateInfo(AdMaterielInfo materielInfo, String name, String desc, Status status,
                           String thumb, String introImg, String jumpUrl, BigDecimal price, Date endTime) {
        materielDao.updateInfo(materielInfo.getId(), name, desc, status, thumb, introImg, jumpUrl, price, endTime);

        String cachekey = MaterielCacheHelper.findById(materielInfo.getId());
        CacheManager.getInstance().delete(cachekey);


        deleteCache(0);
        deleteCache(materielInfo.getCategoryid());
    }

    @Override
    @Transactional
    public void updateDetailInfo(AdMaterielInfo materielInfo, String content, String sizes, String images) {
        materielDetailDao.updateInfo(materielInfo.getId(), content, sizes, images);
        String cachekey = MaterielCacheHelper.findDetailInfoById(materielInfo.getId());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public AdMaterielInfo findById(boolean purge, long id) {
        String cachekey = MaterielCacheHelper.findById(id);
        AdMaterielInfo model = CacheManager.getInstance().getObject(cachekey, AdMaterielInfo.class);
        if(purge || model == null)
        {
            model = materielDao.findById(id);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
            }
        }
        return model;
    }

    public AdMaterielDetailInfo findDetailById(boolean purge, long id) {
        String cachekey = MaterielCacheHelper.findDetailInfoById(id);
        AdMaterielDetailInfo model = CacheManager.getInstance().getObject(cachekey, AdMaterielDetailInfo.class);
        if(purge || model == null)
        {
            model = materielDetailDao.findById(id);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
            }
        }
        return model;
    }

    @Override
    public long countByKey(boolean purge, String key) {
        return materielDao.countByKey(key);
    }

    @Override
    public long count() {
        return materielDao.count();
    }

    @Override
    public void queryAll(Callback<AdMaterielInfo> callback) {
        materielDao.queryAll(callback);
    }

    @Override
    public List<AdMaterielInfo> queryByCategory(boolean purge, long categoryid, PageVo pageVo,long minPrice,long maxPrice) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(90);

        List<AdMaterielInfo> list = null;
        if(pageVo.getOffset() <= 90)
        {
            pageVo.setLimit(100);

            String cachekey = MaterielCacheHelper.queryByCategory(categoryid,minPrice,maxPrice);
            list = CacheManager.getInstance().getList(cachekey, AdMaterielInfo.class);

            if(purge ||list == null)
            {
                list = materielDao.queryByCategory(dateTime, categoryid, 0, 100, minPrice, maxPrice);
                if(list == null)
                {
                    list = Collections.emptyList();
                }
                // 缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_FIVE_MINUTES);
            }
        }
        else
        {
            list = materielDao.queryByCategory(dateTime, categoryid, pageVo.getOffset(), pageVo.getLimit(), minPrice, maxPrice);
            return list;
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = pageVo.getOffset(); i < size; i ++)
        {
            if(rsIndex >= 10)
            {
                break;
            }
            rsList.add(list.get(i));
            rsIndex ++;
        }
        return rsList;
    }

    @Override
    public RowPager<AdMaterielInfo> queryScrollPage(PageVo pageVo, long categoryid, Status status, AdEventType eventType) {
        return materielDao.queryScrollPage(pageVo, categoryid, status, eventType);
    }

    /**
     * 5-50
     * 55-100
     * 105-200
     * 205-300
     * 305-400
     * @param categoryid

     */

    public void deleteCache(long categoryid){
        String cachekey5 = MaterielCacheHelper.queryByCategory(categoryid,0,0);
        CacheManager.getInstance().delete(cachekey5);

//        String cachekey = MaterielCacheHelper.queryByCategory(categoryid,5,50);
//        CacheManager.getInstance().delete(cachekey);
//        String cachekey1 = MaterielCacheHelper.queryByCategory(categoryid,55,100);
//        CacheManager.getInstance().delete(cachekey1);
//        String cachekey2 = MaterielCacheHelper.queryByCategory(categoryid,105,200);
//        CacheManager.getInstance().delete(cachekey2);
//        String cachekey3 = MaterielCacheHelper.queryByCategory(categoryid,205,300);
//        CacheManager.getInstance().delete(cachekey3);
//        String cachekey4 = MaterielCacheHelper.queryByCategory(categoryid,305,400);
//        CacheManager.getInstance().delete(cachekey4);

        String cachekey = MaterielCacheHelper.queryByCategory(categoryid,0,1);
        CacheManager.getInstance().delete(cachekey);
        String cachekey1 = MaterielCacheHelper.queryByCategory(categoryid,1,5);
        CacheManager.getInstance().delete(cachekey1);
        String cachekey2 = MaterielCacheHelper.queryByCategory(categoryid,5,50);
        CacheManager.getInstance().delete(cachekey2);
        String cachekey3 = MaterielCacheHelper.queryByCategory(categoryid,50,100);
        CacheManager.getInstance().delete(cachekey3);
        String cachekey4 = MaterielCacheHelper.queryByCategory(categoryid,100,500);
        CacheManager.getInstance().delete(cachekey4);

    }
}

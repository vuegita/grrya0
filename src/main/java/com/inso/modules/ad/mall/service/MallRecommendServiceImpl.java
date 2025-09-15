package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.cache.MalRecommendCacheHelper;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallRecommendInfo;
import com.inso.modules.ad.mall.model.MallRecommentType;
import com.inso.modules.ad.mall.service.dao.MallRecommendDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MallRecommendServiceImpl implements MallRecommendService{

    @Autowired
    private MallRecommendDao mallRecommendDao;

    @Override
    public void addCategory(MallCommodityInfo materielInfo, MallRecommentType recommentType, long sort) {
        mallRecommendDao.addCategory(materielInfo, recommentType, sort);

        deleteCache(recommentType);
    }

    public void updateInfo(MallRecommendInfo entyty, MallRecommentType recommentType, long sort)
    {
        mallRecommendDao.updateInfo(entyty.getId(), recommentType, sort);

        MallRecommentType dbRecommentType = MallRecommentType.getType(entyty.getType());
        deleteCache(dbRecommentType);

        deleteCache(recommentType);
    }

    @Override
    public void deleteEntity(MallRecommendInfo recommendInfo) {
        mallRecommendDao.deleteByid(recommendInfo.getId());

        MallRecommentType dbRecommentType = MallRecommentType.getType(recommendInfo.getType());
        deleteCache(dbRecommentType);
    }

    @Override
    public MallRecommendInfo findById(boolean purge, long id) {
        return mallRecommendDao.findById(id);
    }

    @Override
    public RowPager<MallRecommendInfo> queryScrollPage(PageVo pageVo, MallRecommentType type, long merchantid) {
        return mallRecommendDao.queryScrollPage(pageVo, type, merchantid);
    }

    @Override
    public List<AdMaterielInfo> queryListByType(boolean purge, MallRecommentType recommentType) {
        String cachekey = MalRecommendCacheHelper.queryListByType(recommentType);
        List<AdMaterielInfo> rsList = CacheManager.getInstance().getList(cachekey, AdMaterielInfo.class);
        if(purge || rsList == null)
        {
            rsList = mallRecommendDao.queryScrollByType(recommentType);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    public void deleteCache(MallRecommentType recommentType)
    {
        String pageCachekey = MalRecommendCacheHelper.queryListByType(recommentType);
        CacheManager.getInstance().delete(pageCachekey);
    }
}

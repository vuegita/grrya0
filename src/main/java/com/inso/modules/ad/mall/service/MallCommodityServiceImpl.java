package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.cache.MallCommodityCacheHelper;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.service.dao.MallCommodityDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallCommodityServiceImpl implements MallCommodityService{

    private static String mEmptyEntityJson = FastJsonHelper.jsonEncode(new MallCommodityInfo());

    @Autowired
    private MallCommodityDao mallCommodityDao;

    @Override
    public void addCategory(UserInfo userInfo, AdMaterielInfo materielInfo) {
        mallCommodityDao.addCategory(userInfo, materielInfo, Status.ENABLE);
    }

    @Override
    public void updateInfo(MallCommodityInfo entity, Status status) {
        mallCommodityDao.updateInfo(entity.getId(), status);

        String cachekey = MallCommodityCacheHelper.findByKey(entity.getMerchantid(), entity.getMaterielid());
        CacheManager.getInstance().delete(cachekey);

        String cachekey2 = MallCommodityCacheHelper.findById(entity.getId());
        CacheManager.getInstance().delete(cachekey2);
    }

    @Override
    public MallCommodityInfo findById(boolean purge, long id) {
        String cachekey = MallCommodityCacheHelper.findById(id);
        MallCommodityInfo entity = CacheManager.getInstance().getObject(cachekey, MallCommodityInfo.class);
        if(purge || entity == null)
        {
            entity = mallCommodityDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public MallCommodityInfo findByKey(boolean purge, long merchantid, long materielid) {
        String cachekey = MallCommodityCacheHelper.findByKey(merchantid, materielid);
        MallCommodityInfo entity = CacheManager.getInstance().getObject(cachekey, MallCommodityInfo.class);
        if(purge || entity == null)
        {
            entity = mallCommodityDao.findByKey(merchantid, materielid);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public void queryAll(Callback<MallCommodityInfo> callback, long minId, int limit) {
        mallCommodityDao.queryAll(callback, minId, limit);
    }

    @Override
    public RowPager<MallCommodityInfo> queryScrollPage(PageVo pageVo, Status status, long merchantid) {
        return mallCommodityDao.queryScrollPage(pageVo, status, merchantid);
    }
}

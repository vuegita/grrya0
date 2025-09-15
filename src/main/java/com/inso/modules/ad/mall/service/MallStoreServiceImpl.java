package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.cache.MallStoreCacheHelper;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.dao.MallStoreDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallStoreServiceImpl implements MallStoreService{

    private static String mEmptyEntityJson = FastJsonHelper.jsonEncode(new MallStoreInfo());

    @Autowired
    private MallStoreDao mallStoreDao;

    @Override
    public void addCategory(UserInfo userInfo, String name, MallStoreLevel level, Status status) {
        mallStoreDao.addCategory(userInfo, name, level, status);
    }

    @Override
    public void updateInfo(MallStoreInfo entity, Status status, MallStoreLevel level, String name) {
        mallStoreDao.updateInfo(entity.getId(), status, level, name);

        String cachekey = MallStoreCacheHelper.findUserid(entity.getUserid());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public MallStoreInfo findUserid(boolean purge, long userid) {
        String cachekey = MallStoreCacheHelper.findUserid(userid);
        MallStoreInfo entity = CacheManager.getInstance().getObject(cachekey, MallStoreInfo.class);
        if(purge || entity == null)
        {
            entity = mallStoreDao.findUserid(userid);
            String value = mEmptyEntityJson;
            if(entity != null)
            {
                value = FastJsonHelper.jsonEncode(entity);
            }
            CacheManager.getInstance().setString(cachekey, value);
        }
        if(entity == null || entity.getUserid() <= 0)
        {
            entity = null;
        }
        return entity;
    }

    @Override
    public RowPager<MallStoreInfo> queryScrollPage(PageVo pageVo, Status status, long userid) {
        return mallStoreDao.queryScrollPage(pageVo, status, userid);
    }

    @Override
    public void queryAll(Callback<MallStoreInfo> callback) {
        mallStoreDao.queryAll(callback);
    }
}

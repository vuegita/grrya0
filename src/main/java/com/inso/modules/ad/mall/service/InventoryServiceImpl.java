package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.cache.MallDeliveryCacheHelper;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.ad.mall.service.dao.InventoryDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService{

    @Autowired
    private InventoryDao mInventoryDao;

    @Override
    public void addOrder(UserInfo userInfo, AdMaterielInfo materielInfo, long quantity, Status status, JSONObject jsonObject) {
        mInventoryDao.addOrder(userInfo, materielInfo, quantity, status, jsonObject);
    }

    @Override
    public void updateInfo(InventoryInfo entity, Status status, long quantity, JSONObject jsonObject) {
        mInventoryDao.updateInfo(entity.getId(), status, quantity, jsonObject);

        String cachekey = MallDeliveryCacheHelper.findById(entity.getId());
        CacheManager.getInstance().delete(cachekey);

        String userCachekey = MallDeliveryCacheHelper.findByUseridAndMaterielid(entity.getUserid(), entity.getMaterielid());
        CacheManager.getInstance().delete(userCachekey);
    }

    @Override
    public InventoryInfo findById(boolean purge, long id) {
        String cachekey = MallDeliveryCacheHelper.findById(id);
        InventoryInfo entity = CacheManager.getInstance().getObject(cachekey, InventoryInfo.class);
        if(purge || entity == null)
        {
            entity = mInventoryDao.findById(id);
            String value = InventoryInfo.mEmptyEntityJson;
            if(entity != null)
            {
                value = FastJsonHelper.jsonEncode(entity);
            }
            CacheManager.getInstance().setString(cachekey, value);
        }
        if(entity.getId() <= 0)
        {
            entity = null;
        }
        return entity;
    }

    @Override
    public InventoryInfo findByUseridAndMaterielid(boolean purge, long userid, long materielid) {
        String cachekey = MallDeliveryCacheHelper.findByUseridAndMaterielid(userid, materielid);
        InventoryInfo entity = CacheManager.getInstance().getObject(cachekey, InventoryInfo.class);
        if(purge || entity == null)
        {
            entity = mInventoryDao.findByUseridAndMaterielid(userid, materielid);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }

        }
        return entity;
    }

    @Override
    public RowPager<InventoryInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, long categoryid) {
        return mInventoryDao.queryScrollPage(pageVo, agentid, staffid, userid, status, categoryid);
    }
}

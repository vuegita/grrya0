package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.cache.MallBuyerAddrCacheHelper;
import com.inso.modules.ad.mall.cache.MallStoreCacheHelper;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.ad.mall.service.dao.MallBuyerAddrDao;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallBuyerAddrServiceImpl implements MallBuyerAddrService {

    private static String mEmptyEntityJson = FastJsonHelper.jsonEncode(new MallBuyerAddrInfo());

    @Autowired
    private MallBuyerAddrDao mallBuyerAddrDao;

    @Override
    public void addCategory(UserInfo userInfo, String location, Status status, String phone) {
        mallBuyerAddrDao.addCategory(userInfo, location, status, phone);
    }

    @Override
    public void updateInfo(MallBuyerAddrInfo entity, String phone, Status status, String location) {
        mallBuyerAddrDao.updateInfo(entity.getId(), phone, status, location);

        String cachekey = MallStoreCacheHelper.findUserid(entity.getUserid());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public MallBuyerAddrInfo findUserid(boolean purge, long userid) {
        String cachekey = MallBuyerAddrCacheHelper.findUserid(userid);
        MallBuyerAddrInfo entity = CacheManager.getInstance().getObject(cachekey, MallBuyerAddrInfo.class);
        if(purge || entity == null)
        {
            entity = mallBuyerAddrDao.findUserid(userid);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public RowPager<MallBuyerAddrInfo> queryScrollPage(PageVo pageVo, Status status, long userid) {
        return mallBuyerAddrDao.queryScrollPage(pageVo, status, userid);
    }
}

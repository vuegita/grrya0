package com.inso.modules.passport.share_holder.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.share_holder.cache.ShareHolderCacheUtils;
import com.inso.modules.passport.share_holder.model.ShareHolderInfo;
import com.inso.modules.passport.share_holder.service.dao.ShareHolderDao;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShareHolderServiceImpl implements ShareHolderService{

    @Autowired
    private ShareHolderDao mShareHolderDao;

    @Override
    public void add(UserInfo userInfo, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus) {
        mShareHolderDao.add(userInfo, lv1RwStatus, lv2RwStatus, sysStatus);
        deleteCache(userInfo.getId());
    }

    @Override
    public void updateInfo(long userid, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus) {
        mShareHolderDao.updateInfo(userid, lv1RwStatus, lv2RwStatus, sysStatus);
        deleteCache(userid);
    }

    @Override
    public ShareHolderInfo findByUserId(boolean purge, long userid) {
        String cachekey = ShareHolderCacheUtils.findByUserId(userid);
        ShareHolderInfo model = CacheManager.getInstance().getObject(cachekey, ShareHolderInfo.class);
        if(purge || model == null)
        {
            model = mShareHolderDao.findByUserId(userid);
            if(model == null)
            {
                model = new ShareHolderInfo();
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
        }
        if(model.getUserid() <= 0)
        {
            model = null;
        }
        return model;
    }

    @Override
    public RowPager<ShareHolderInfo> queryScrollPage(PageVo pageVo, long userid, Status sysStatus) {
        return mShareHolderDao.queryScrollPage(pageVo, userid, sysStatus);
    }

    private void deleteCache(long userid)
    {
        String cachekey = ShareHolderCacheUtils.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }
}

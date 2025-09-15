package com.inso.modules.web.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BannerType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.cache.BannerCacheHelper;
import com.inso.modules.web.model.Banner;
import com.inso.modules.web.service.dao.BannerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class BannerServiceImpl extends DaoSupport implements BannerService {

    @Autowired
    private BannerDao mBannerDao;

    @Override
    public void addBanner(String title, String content, BannerType bannerType, String img, String webUrl, JSONObject remark) {
        mBannerDao.addBanner( title,  content, bannerType,  img,  webUrl, Status.DISABLE, Status.ENABLE, remark);
        deleteCache();
    }

    @Override
    public void updateInfo(long id, String title, String content, BannerType bannerType, String img, String webUrl, Status forceLogin, Status status, JSONObject remark) {
        mBannerDao.updateInfo(id,  title,  content,  bannerType, img, webUrl, forceLogin,  status,  remark);
        deleteCache();
    }

    @Override
    public Banner findById(long id) {
        return mBannerDao.findById(id);
    }

    @Override
    public void deleteById(long id) {
        mBannerDao.deleteById(id);

        deleteCache();
    }

    @Override
    public List<Banner> queryAllByBannerType(boolean purge, BannerType bannerType, Status status) {
        String cachekey = BannerCacheHelper.getBannerListBystatus(status.getKey());
        List<Banner> rsList = CacheManager.getInstance().getList(cachekey, Banner.class);

        if(purge || rsList == null)
        {
            rsList=mBannerDao.queryAllByBannerType(bannerType,status);
            if(CollectionUtils.isEmpty(rsList))
            {
                rsList = Collections.emptyList();
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
        }
        return rsList;
    }

    @Override
    public RowPager<Banner> queryScrollPage(PageVo pageVo, BannerType bannerType, Status status) {
        return mBannerDao.queryScrollPage(pageVo,bannerType,status);
    }

    public void deleteCache(){
        String enablecachekey = BannerCacheHelper.getBannerListBystatus(Status.ENABLE.getKey());
        CacheManager.getInstance().delete(enablecachekey);
    }
}

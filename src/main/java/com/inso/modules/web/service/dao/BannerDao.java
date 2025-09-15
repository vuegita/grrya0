package com.inso.modules.web.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BannerType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.Banner;

import java.util.List;


public interface BannerDao {

    public void addBanner(String title, String content, BannerType bannerType, String img, String webUrl, Status forceLogin, Status status, JSONObject remark);
    public void updateInfo(long id, String title, String content, BannerType bannerType, String img, String webUrl, Status forceLogin, Status status, JSONObject remark);
    public Banner findById(long id);
    public void deleteById(long id);

    public List<Banner> queryAllByBannerType(BannerType bannerType, Status status);
    public RowPager<Banner> queryScrollPage(PageVo pageVo, BannerType bannerType, Status status);
}

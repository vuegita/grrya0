package com.inso.modules.web.logical;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.model.Banner;

@Component
public class BannerManager {

    private List<Banner> mBannerList = Lists.newArrayList();

    public BannerManager()
    {
        synchronized (mBannerList)
        {
            if(!MyEnvironment.isProd())
            {
                addItem("home", "/static/web/home/img/banners_1a.png", "https://www.baidu.com","enable");
                addItem("home", "/static/web/home/img/banners_2b.png", "https://www.baidu.com","enable");
                addItem("home", "/static/web/home/img/banners_3c.png", "https://www.baidu.com","enable");
//                addItem("bonus", "/static/web/home/img/activity1a.png", "https://www.baidu.com","disable");
//                addItem("bonus", "/static/web/home/img/activity2b.png", "https://www.baidu.com","disable");
//                addItem("bonus", "/static/web/home/img/activity3c.png", "https://www.baidu.com","disable");
            }
        }
    }

    private void addItem(String type, String img, Object data ,String status)
    {
        Banner banner = new Banner();
        banner.setTitle(StringUtils.getEmpty());
        banner.setDesc(StringUtils.getEmpty());
        banner.setImg(img);
        banner.setType(StringUtils.getNotEmpty(type));
        banner.setData(data);
        banner.setStatus(status);
        mBannerList.add(banner);
    }

    public List<Banner> getDataList()
    {
        return mBannerList;
    }

}

package com.inso.modules.web.logical;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.model.Goods;

/**
 * 商城管理器
 */
@Component
public class MallManager {

    private List<Goods> mRSList = Lists.newArrayList();

    public MallManager()
    {
        synchronized (mRSList)
        {
            if(!MyEnvironment.isProd())
            {
//                addItem("ad", "aa", 1000,  "https://t7.baidu.com/it/u=2100461953,4165915283&fm=193&f=GIF", "https://www.baidu.com");
//                addItem("view", "bb", 200, "https://t7.baidu.com/it/u=2417635591,2147979943&fm=193&f=GIF", "https://www.baidu.com");
                addItem("ad", "YouBella Jewellery Sets for Women Gold Plated Bridal Necklace Jewellery Set with Earrings for Girls/Women ", 6900, "/static/web/home/img/shop1a.jpg", "https://www.baidu.com");
                addItem("view", "Shining Diva Fashion Infinity Crystal Charm Bracelet for Women and Girls", 4990, "/static/web/home/img/shop2b.jpg", "https://www.baidu.com");
                addItem("view", "Soie Padded Wired Push Up Bra", 5299, "/static/web/home/img/shop3c.jpg", "https://www.baidu.com");
                addItem("view", "UR HIGHER SELF Clothing Accessories Nipple Ring No Piercing", 8990, "/static/web/home/img/shop4d.jpg", "https://www.baidu.com");
                addItem("view", "Exotic India Men's cotton Dhoti", 12390, "/static/web/home/img/shop5e.jpg", "https://www.baidu.com");
                addItem("view", " Sukkhi Ethnic Pearl Gold Plated Wedding Jewellery Peacock Meenakari Kada For Women (12120KADS650) ", 10890, "/static/web/home/img/shop6f.jpg", "https://www.baidu.com");
            }
        }
    }

    private void addItem(String type, String title, float amount, String imgUrl, Object data)
    {
        Goods banner = new Goods();
        banner.setTitle(title);
//        banner.setTitle(StringUtils.getEmpty());
        banner.setDesc(StringUtils.getEmpty());
        banner.setImgUrl(imgUrl);
        banner.setType(StringUtils.getNotEmpty(type));
        banner.setData(data);
        banner.setAmount(amount);
        mRSList.add(banner);
    }

    public List<Goods> getDataList()
    {
        return mRSList;
    }

}

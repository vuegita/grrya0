package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielDetailInfo;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MaterielService {

    public void add(String key, long categoryid, String name, String desc, Status status,
                    String thumb, String introImg, String jumpUrl,
                    BigDecimal price, String provider, String admin,
                    AdEventType eventType, long limitMinDay, int expiresDay,
                    String content, String sizes, String images);

    public void updateInfo(AdMaterielInfo materielInfo, String name, String desc, Status status,
                           String thumb, String introImg, String jumpUrl, BigDecimal price, Date endTime);

    public void updateDetailInfo(AdMaterielInfo materielInfo, String content, String sizes, String images);

    public AdMaterielInfo findById(boolean purge, long id);
    public AdMaterielDetailInfo findDetailById(boolean purge, long id);

    public long countByKey(boolean purge, String key);
    public long count();

    public void queryAll(Callback<AdMaterielInfo> callback);
    public List<AdMaterielInfo> queryByCategory(boolean purge, long categoryid, PageVo pageVo ,long minPrice,long maxPrice);
    public RowPager<AdMaterielInfo> queryScrollPage(PageVo pageVo, long categoryid, Status status, AdEventType eventType);

}

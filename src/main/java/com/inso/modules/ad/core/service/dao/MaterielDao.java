package com.inso.modules.ad.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MaterielDao  {

    public long add(String key, long categoryid, String name, String desc, Status status,
                    String thumb, String introImg, String jumpUrl,
                    BigDecimal price, String provider, String admin,
                    AdEventType eventType, long limitMinDay, int expiresDay);

    public void updateInfo(long id, String name, String desc, Status status, String thumb, String introImg, String jumpUrl, BigDecimal price, Date endTime);

    public AdMaterielInfo findById(long id);
    public long countByKey(String key);
    public long count();

    public void queryAll(Callback<AdMaterielInfo> callback);
    public List<AdMaterielInfo> queryByCategory(DateTime dateTime, long categoryid, int pageOffset, int pageSize,long minPrice,long maxPrice);
    public RowPager<AdMaterielInfo> queryScrollPage(PageVo pageVo, long categoryid, Status status, AdEventType eventType);

}

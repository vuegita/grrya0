package com.inso.modules.ad.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CategoryDao {

    public void addCategory(String key, String name, BigDecimal returnRate, Status status, BigDecimal beginPrice, BigDecimal endPrice);
    public void updateInfo(long id, Status status, BigDecimal returnRate, String name, BigDecimal minPrice, BigDecimal maxPrice);


    public AdCategoryInfo findById(long id);
    public AdCategoryInfo findByKey(String key);

    public List<AdCategoryInfo> queryAllByStatus(Status status);
    public RowPager<AdCategoryInfo> queryScrollPage(PageVo pageVo, Status status);

}

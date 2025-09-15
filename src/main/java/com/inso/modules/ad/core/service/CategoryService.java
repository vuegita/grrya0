package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CategoryService {

    public void addCategory(String key, String name, BigDecimal returnRate, Status status, BigDecimal beginPrice, BigDecimal endPrice);
    public void updateInfo(AdCategoryInfo category, Status status, BigDecimal returnRate, String name, BigDecimal beginPrice, BigDecimal endPrice);

    public AdCategoryInfo findByKey(boolean purge, String key);
    public AdCategoryInfo findById(boolean purge, long id);
    public List<AdCategoryInfo> queryAllEnable(boolean purge);

    /**
     * 内部调用，不要在接口中调用
     * @return
     */
    public List<AdCategoryInfo> queryAll();
    public RowPager<AdCategoryInfo> queryScrollPage(PageVo pageVo, Status status);

}

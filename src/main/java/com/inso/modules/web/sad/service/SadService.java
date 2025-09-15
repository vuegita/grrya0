package com.inso.modules.web.sad.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.sad.model.SadInfo;

public interface SadService {

    public void addOrder(String key, MyDimensionType dimensionType, String title, String content, Status status, String gb1Value);

    public void delete(long id);
    public SadInfo find(long id);

    public RowPager<SadInfo> queryScrollPage(PageVo pageVo, String key, MyDimensionType dimensionType);

}

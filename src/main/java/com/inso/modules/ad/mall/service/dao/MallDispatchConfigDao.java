package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.common.model.Status;

import java.util.List;

public interface MallDispatchConfigDao  {


    public void addCategory(MallStoreLevel levelType, Status status, long minCount, long maxCount);

    public void updateInfo(long id, Status status, long minCount, long maxCount);

    public MallDispatchConfigInfo findById(long id);

    public MallDispatchConfigInfo findByKey(MallStoreLevel levelType);
    public RowPager<MallDispatchConfigInfo> queryScrollPage(PageVo pageVo);

    public List<MallDispatchConfigInfo> queryAll(Status status);

}

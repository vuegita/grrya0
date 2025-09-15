package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.common.model.Status;

import java.util.List;

public interface MallDispatchConfigService {


    public void addCategory(MallStoreLevel levelType, long minCount, long maxCount);

    public void updateInfo(MallDispatchConfigInfo entity, Status status, long minCount, long maxCount);

    public MallDispatchConfigInfo findByKey(boolean purge, MallStoreLevel levelType);
    public RowPager<MallDispatchConfigInfo> queryScrollPage(PageVo pageVo);

    public List<MallDispatchConfigInfo> queryAll(boolean purge);

}

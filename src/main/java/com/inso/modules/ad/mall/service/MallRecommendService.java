package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallRecommendInfo;
import com.inso.modules.ad.mall.model.MallRecommentType;

import java.util.List;

public interface MallRecommendService {

    public void addCategory(MallCommodityInfo materielInfo, MallRecommentType recommentType, long sort);

    public void updateInfo(MallRecommendInfo entyty, MallRecommentType recommentType, long sort);

    public void deleteEntity(MallRecommendInfo recommendInfo);

    public MallRecommendInfo findById(boolean purge, long id);
    public RowPager<MallRecommendInfo> queryScrollPage(PageVo pageVo, MallRecommentType type, long merchantid);

    public List<AdMaterielInfo> queryListByType(boolean purge, MallRecommentType recommentType);

}

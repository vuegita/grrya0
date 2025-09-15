package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallRecommendInfo;
import com.inso.modules.ad.mall.model.MallRecommentType;

import java.util.List;

public interface MallRecommendDao  {

    public void addCategory(MallCommodityInfo materielInfo, MallRecommentType recommentType, long sort);
    public void updateInfo(long id, MallRecommentType recommentType, long sort);

    public void deleteByid(long id);
    public List<MallRecommendInfo> queryListByType(MallRecommentType recommentType);
    public List<AdMaterielInfo> queryScrollByType(MallRecommentType recommentType);


    public MallRecommendInfo findById(long id);
    public RowPager<MallRecommendInfo> queryScrollPage(PageVo pageVo, MallRecommentType status, long merchantid);

}

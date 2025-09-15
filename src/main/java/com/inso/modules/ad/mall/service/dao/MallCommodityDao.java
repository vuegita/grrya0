package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface MallCommodityDao {

    public void addCategory(UserInfo userInfo, AdMaterielInfo materielInfo, Status status);

    public MallCommodityInfo findById(long id);
    public void updateInfo(long id, Status status);

    public MallCommodityInfo findByKey(long merchantid, long materielid);

    public void queryAll(Callback<MallCommodityInfo> callback, long minId, int limit);

    public RowPager<MallCommodityInfo> queryScrollPage(PageVo pageVo, Status status, long merchantid);

}

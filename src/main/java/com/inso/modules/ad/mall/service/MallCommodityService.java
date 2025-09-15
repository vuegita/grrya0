package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface MallCommodityService {

    public void addCategory(UserInfo userInfo, AdMaterielInfo materielInfo);

    public void updateInfo(MallCommodityInfo entity, Status status);

    /**
     * 后台调用，前端接口不可调用此接口
     * @param id
     * @return
     */
    public MallCommodityInfo findById(boolean purge, long id);
    public MallCommodityInfo findByKey(boolean purge, long merchantid, long materielid);
    public void queryAll(Callback<MallCommodityInfo> callback, long minId, int limit);

    public RowPager<MallCommodityInfo> queryScrollPage(PageVo pageVo, Status status, long merchantid);

}

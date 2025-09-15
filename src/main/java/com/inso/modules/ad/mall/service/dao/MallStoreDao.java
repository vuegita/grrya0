package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface MallStoreDao  {

    public void addCategory(UserInfo userInfo, String name, MallStoreLevel level, Status status);

    public void updateInfo(long id, Status status, MallStoreLevel level, String name);

    public MallStoreInfo findUserid(long userid);
    public void queryAll(Callback<MallStoreInfo> callback);
    public RowPager<MallStoreInfo> queryScrollPage(PageVo pageVo, Status status, long userid);

}

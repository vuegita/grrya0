package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface MallBuyerAddrService {

    public void addCategory(UserInfo userInfo, String location, Status status, String phone);

    public void updateInfo(MallBuyerAddrInfo entity, String phone, Status status, String location);

    public MallBuyerAddrInfo findUserid(boolean purge, long userid);
    public RowPager<MallBuyerAddrInfo> queryScrollPage(PageVo pageVo, Status status, long userid);

}

package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface MallBuyerAddrDao {

    public void addCategory(UserInfo userInfo, String location, Status status, String phone);

    public void updateInfo(long id, String phone, Status status, String location);

    public MallBuyerAddrInfo findUserid(long userid);
    public RowPager<MallBuyerAddrInfo> queryScrollPage(PageVo pageVo, Status status, long userid);

}

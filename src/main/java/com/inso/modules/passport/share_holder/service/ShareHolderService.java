package com.inso.modules.passport.share_holder.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.share_holder.model.ShareHolderInfo;
import com.inso.modules.passport.user.model.UserInfo;

public interface ShareHolderService {

    public void add(UserInfo userInfo, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus);

    public void updateInfo(long userid, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus);
    public ShareHolderInfo findByUserId(boolean purge, long userid);
    public RowPager<ShareHolderInfo> queryScrollPage(PageVo pageVo, long userid, Status sysStatus);

}

package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface InventoryService {


    public void addOrder(UserInfo userInfo, AdMaterielInfo materielInfo,
                         long quantity, Status status, JSONObject jsonObject);
    public void updateInfo(InventoryInfo entity, Status status, long quantity, JSONObject jsonObject);

    public InventoryInfo findById(boolean purge, long id);
    public InventoryInfo findByUseridAndMaterielid(boolean purge, long userid, long materielid);

    public RowPager<InventoryInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, long categoryid);



}

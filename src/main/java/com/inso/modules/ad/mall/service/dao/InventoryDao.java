package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

public interface InventoryDao  {


    public void addOrder(UserInfo userInfo, AdMaterielInfo materielInfo,
                         long quantity, Status status, JSONObject jsonObject);
    public void updateInfo(long id, Status status, long quantity, JSONObject jsonObject);

    public InventoryInfo findById(long id);
    public InventoryInfo findByUseridAndMaterielid(long userid, long materielid);

    public RowPager<InventoryInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status Status, long categoryid);



}

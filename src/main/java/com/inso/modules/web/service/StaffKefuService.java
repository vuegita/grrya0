package com.inso.modules.web.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.StaffKefu;
import com.inso.modules.web.model.StaffkefuType;

import java.util.List;

public interface StaffKefuService {


    public void addKefu(UserAttr staffAttrInfo, String title, String describe, String icon, String whatsapp, String telegram);
    public void updateInfo(long id, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark);
    public void deleteById(long id);
   // public StaffKefu findById(boolean purge ,UserAttr staffAttrInfo);
    public List<StaffKefu> findById(boolean purge ,UserAttr staffAttrInfo, StaffkefuType staffkefuType);


    public List<StaffKefu> queryOnlinestaffKefuList(boolean purge, UserAttr userAttr, StaffkefuType staffkefuType);
    /**
     * 后台调用
     * @param id
     * @return
     */
    public StaffKefu findById(long id);
    public RowPager<StaffKefu> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status);

}

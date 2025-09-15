package com.inso.modules.web.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.KefuGroup;

public interface KefuGroupService {

    public void addGroup(String name, String describe, String icon, Status status, JSONObject remark);
    public void updateInfo(long groupid, String name, String describe, String icon, Status status, JSONObject remark);
    public KefuGroup findById(long id);
    public void deleteById(long id);

    public KefuGroup findEnableGroup();
    public List<KefuGroup> queryAll(boolean purge);
    public RowPager<KefuGroup> queryScrollPage(PageVo pageVo, String name, Status status);

}

package com.inso.modules.web.service.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.KefuMember;


public interface KefuMemberDao {

    public void addMember(long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark);
    public void updateInfo(long id, long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark);
    public KefuMember findById(long id);
    public void deleteById(long id);

    public long countByGroupId(long groupid);
    public List<KefuMember> queryAllByGroupid(long groupid);
    public RowPager<KefuMember> queryScrollPage(PageVo pageVo, long groupid, String name, Status status);
}

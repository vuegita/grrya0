package com.inso.modules.web.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.Tgsms;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;

import java.util.List;

public interface TgsmsDao {


    public void addTgsms(UserAttr userAttr, String rbtoken, TipsType type, String chatid, Status status, JSONObject remark);
    public void updateInfo(long id, String rbtoken, TipsType type, String chatid, Status status, JSONObject remark);


    public  List<Tgsms> findAgentid(long agentid ,long staffid);

    public  List<Tgsms> findByTypeAndUserid(long agentid , TipsType type);

    public void deleteById(long id);

    public Tgsms findById(long id);
    public RowPager<Tgsms> queryScrollPage(PageVo pageVo,  Status status , long agentid, long staffid);

}

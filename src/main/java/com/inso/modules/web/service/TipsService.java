package com.inso.modules.web.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;

import java.util.List;

public interface TipsService {

    public void addTips(UserAttr userAttr, String title, TipsType type, String content);
    public void updateInfo(long id, String title, TipsType type, String content, Status status, JSONObject remark);

    public  List<Tips> findAgentid(boolean purge ,long agentid);

    public  List<Tips> findByTypeAndUserid(boolean purge ,long agentid , TipsType type);
//    public List<Tips> queryAgentTipsList(boolean purge, UserAttr userAttr, StaffkefuType staffkefuType);
    /**
     * 后台调用
     * @param id
     * @return
     */
    public void deleteById(long id);

    public Tips findById(long id);
    public RowPager<Tips> queryScrollPage(PageVo pageVo, long userid, Status status , long agentid, long staffid);


}

package com.inso.modules.web.service.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.FeedBack;


public interface FeedBackDao {

    public void addFeedBack(UserAttr staffAttrInfo, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark);
    public void updateInfo(long id, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark);
    public List<FeedBack> findByUserAttr(UserAttr staffAttrInfo);

    public List<FeedBack> queryListByUserid(Status status, String createtime, long userid, int limit);

    public FeedBack findById(long id);
    public void deleteById(long id);

    public RowPager<FeedBack> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, FeedBackType feedBackType, Status status);
}

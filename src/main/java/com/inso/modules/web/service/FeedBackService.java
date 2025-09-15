package com.inso.modules.web.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.FeedBack;

public interface FeedBackService {
    public void addFeedBack(UserAttr staffAttrInfo, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark);
    public void updateInfo(long id, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark);
    public List<FeedBack> findByUserAttr(UserAttr staffAttrInfo);
    public FeedBack findById(long id);
    public void deleteById(long id);

    public RowPager<FeedBack> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, FeedBackType feedBackType, Status status);

    public List<FeedBack> queryListByUserid(boolean purge, long userid, Status status, int offset);
}

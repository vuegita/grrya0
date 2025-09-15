package com.inso.modules.game.service.dao;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.BusinessReport;

public interface BusinessReportDao {

    public void addReport(Date pdate, GameChildType childType, BusinessReport report, JSONObject remark);
    public void delete(Date pdate, GameChildType childType);
    public RowPager<BusinessReport> queryScrollPage(PageVo pageVo, String key);
}

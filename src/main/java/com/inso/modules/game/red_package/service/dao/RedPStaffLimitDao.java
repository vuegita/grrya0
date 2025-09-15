package com.inso.modules.game.red_package.service.dao;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.passport.user.model.UserAttr;

public interface RedPStaffLimitDao {

    public void addConfig(UserAttr staffAttrInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark);
    public void updateInfo(long id, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark);

    public RedPStaffLimit findById(long id);

    public RedPStaffLimit findByStaffId(long staffid);
    public void deleteById(long id);

    public RowPager<RedPStaffLimit> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status);

}

package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.passport.user.model.UserAttr;

public interface RedPStaffLimitService {

    public void addConfig(UserAttr staffAttrInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark);
    public void updateInfo(RedPStaffLimit limitInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark);

    public RedPStaffLimit findById(long id);
    public RedPStaffLimit findByStaffId(boolean purge, long staffid);
    public void deleteById(RedPStaffLimit limitInfo);

    public RowPager<RedPStaffLimit> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status);

}

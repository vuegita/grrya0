package com.inso.modules.passport.returnwater.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;

public interface ReturnWaterLogCountDao  {

    public void addLog(long userid, String username);
    public void updateCount(long userid, int level);


    public ReturnWaterLog findByUserid(long userid);
    public RowPager<ReturnWaterLog> queryScrollPage(PageVo pageVo, long userid);


}

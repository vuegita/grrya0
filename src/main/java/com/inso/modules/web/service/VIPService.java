package com.inso.modules.web.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.math.BigDecimal;
import java.util.List;

public interface VIPService {


    public void addVIPLevel(VIPType vipType, long level, String name, BigDecimal price);

    public void updateInfo(VIPInfo vipInfo, Status status, String name, BigDecimal price, long level);
    public List<VIPInfo> queryAllEnable(boolean purge, VIPType vipType);
    public List<VIPInfo> queryAll(VIPType vipType);

    public VIPInfo findById(boolean purge, long id);
    public VIPInfo findFree(boolean purge, VIPType vipType);
    public long findMaxLevel(VIPType vipType);
    public RowPager<VIPInfo> queryScrollPage(PageVo pageVo, VIPType vipType, Status status);


}

package com.inso.modules.web.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.math.BigDecimal;
import java.util.List;

public interface VIPDao {


    public void addVIPLevel(VIPType vipType, long level, String name, Status status, BigDecimal price);

    public void updateInfo(long id, Status status, String name, BigDecimal price, long level);
    public List<VIPInfo> queryAllStatus(VIPType vipType, Status status);

    public VIPInfo findById(long id);
    public VIPInfo findByLevel(VIPType vipType, long level);

    public long findMaxLevel(VIPType vipType);

    public RowPager<VIPInfo> queryScrollPage(PageVo pageVo, VIPType vipType, Status status);


}

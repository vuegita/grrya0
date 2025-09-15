package com.inso.modules.ad.mall.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallDeliveryInfo;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public interface MallDeliveryDao  {


    public void add(String orderno, Status status, String location, boolean isFinish, Date createtime);

    public void updateInfo(long id, Status status, String location);

    public MallDeliveryInfo findById(long id);
    public void updateStatus(String orderno, Status status);
    public void queryAll(Callback<MallDeliveryInfo> callback, DateTime fromTime, DateTime toTime, Status status);

    public List<MallDeliveryInfo> queryList(String orderno);
    public RowPager<MallDeliveryInfo> queryScrollPage(PageVo pageVo, String orderno, Status status, String trackno);

}

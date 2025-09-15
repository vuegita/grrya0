package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallDeliveryInfo;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;

import java.util.List;

public interface MallDeliveryService {


    public void add(String orderno, Status status, boolean isFinish, String location);
    public void batchAdd(String orderno, List<String> rsList);

    public void updateInfo(MallDeliveryInfo entity, Status status, String location);

    public MallDeliveryInfo findById(boolean purge, long id);
    public void updateStatus(String orderno, Status status);

    public void queryAll(Callback<MallDeliveryInfo> callback, DateTime fromTime, DateTime toTime, Status status);

    public List<MallDeliveryInfo> queryListByOrderno(boolean purge, String orderno);
    public RowPager<MallDeliveryInfo> queryScrollPage(PageVo pageVo, String orderno, Status status, String trackno);

}

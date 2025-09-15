package com.inso.modules.ad.core.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface EventOrderDao {

    public void addOrder(String orderno, AdMaterielInfo materielInfo, UserAttr userAttr, BigDecimal brokerage, long quanlity, BigDecimal totalAmount, MallCommodityInfo commodityInfo,
                         long addressid, String targetLocation, String buyerPhone, OrderTxStatus txStatus, String shopFrom, JSONObject remark);
    public void updateInfo(String orderno, OrderTxStatus status);
    public void updateShippingInfo(String orderno, OrderTxStatus shippingStatus, String shippingTrackno);

    public AdEventOrderInfo findById(String orderno);
    public AdEventOrderInfo findLatestOrderInfo(DateTime date, long userid, long materielid);
    public List<Long> queryLatestMaterielIds(DateTime date, long userid, int limit);
    public List<AdEventOrderInfo> queryByUser(DateTime fromTime, long userid, int limit);
    public BigDecimal findAllHistoryAmountByUser(long userid);

    public List<AdEventOrderInfo> queryByUserAndTxStatus(DateTime date, long userid, OrderTxStatus txStatus, int offset, int size);

    public void queryAll(DateTime fromTime, DateTime toTime, AdEventType eventType, Callback<AdEventOrderInfo> callback);
    public RowPager<AdEventOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status, AdEventType eventType, long materielid);

}

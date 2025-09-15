package com.inso.modules.ad.core.service;

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

public interface EventOrderService {

    public String createOrder(AdMaterielInfo materielInfo, UserAttr userAttr);
    public String createOrderByShop(AdMaterielInfo materielInfo, UserAttr userAttr, BigDecimal totalAmount, long quanlity, BigDecimal brokerage,
                                    MallCommodityInfo commodityInfo, long addressid, String buyerLocation, String buyerPhone, String shopFrom, JSONObject remark);

    public void updateInfo(AdEventOrderInfo logInfo, OrderTxStatus status);
    public void updateShippingInfo(AdEventOrderInfo orderInfo, OrderTxStatus shippingStatus);

    public AdEventOrderInfo findById(boolean purge, String orderno);

    /**
     * 获取最新ID
     * @param purge
     * @param userid
     * @param materielid
     * @return
     */
    public AdEventOrderInfo findLatestOrderInfo(boolean purge, long userid, long materielid);

    public List<AdEventOrderInfo> queryLatestByUser(boolean purge, long userid);

    public BigDecimal findAllHistoryAmountByUser(boolean purge, long userid);

    /**
     * 根据状态获取对应的订单记录
     * @param purge
     * @param userid
     * @param txStatus
     * @param pageVo
     * @return
     */
    public List<AdEventOrderInfo> queryByUserAndTxStatus(boolean purge, long userid, OrderTxStatus txStatus, PageVo pageVo);

    public List<Long> queryLatestMaterielIds(boolean purge, long userid);
    public void queryAll(DateTime fromTime, DateTime toTime, AdEventType eventType, Callback<AdEventOrderInfo> callback);
    public RowPager<AdEventOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status, AdEventType eventType, long materielid);

    public void clearUserPageCache(long userid);

}

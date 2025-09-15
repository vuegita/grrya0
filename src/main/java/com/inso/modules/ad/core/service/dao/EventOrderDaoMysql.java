package com.inso.modules.ad.core.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class EventOrderDaoMysql extends DaoSupport implements EventOrderDao {

    /**
     order_no           varchar(50) NOT NULL comment  '系统订单号',
     order_materiel_id  int(11) UNSIGNED NOT NULL comment '物料id',
     order_event_type   varchar(50) NOT NULL comment '事件类型=download|buy|like',

     order_userid       int(11) UNSIGNED NOT NULL comment '用户id',
     order_username     varchar(255) NOT NULL comment  '',
     order_agentid      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname    varchar(50) NOT NULL comment  '',
     order_staffid      int(11) NOT NULL DEFAULT 0,
     order_staffname    varchar(50) NOT NULL comment  '',

     order_price               decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额-销售单价',
     order_quantity            int(11) NOT NULL DEFAULT 1 comment '销售数量',
     order_brokerage           decimal(18,2) NOT NULL DEFAULT 0 comment '佣金',
     order_amount              decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额-销售总金额',
     order_status              varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',

     order_merchantid             int(11) NOT NULL DEFAULT 0,
     order_merchantname           varchar(50) NOT NULL DEFAULT '' comment  '所属商家',
     order_shipping_status        varchar(50) NOT NULL DEFAULT '' comment '物流状态,new（卖家待处理）-> pending(仓库处理中) -> waiting(发货中) -> realized(已收货)',
     order_shipping_trackno       varchar(255) NOT NULL DEFAULT '' comment '快递订单号',
     order_buyer_addressid        int(11) NOT NULL DEFAULT 0,
     order_buyer_location        varchar(500) NOT NULL DEFAULT 0, comment '送货地址',
     order_buyer_phone            varchar(100) NOT NULL DEFAULT '', comment '买家电话',

     order_createtime   datetime DEFAULT NULL comment '创建时间',
     order_remark       varchar(1024) NOT NULL DEFAULT '' COMMENT '',
     */
    private static final String TABLE = "inso_ad_event_order";

    @Override
    public void addOrder(String orderno, AdMaterielInfo materielInfo, UserAttr userAttr, BigDecimal brokerage, long quanlity, BigDecimal totalAmount, MallCommodityInfo commodityInfo,
                         long addressid, String buyerLocation, String buyerPhone, OrderTxStatus txStatus, String shopFrom, JSONObject remark)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_materiel_id", materielInfo.getId());
        keyvalue.put("order_event_type", materielInfo.getEventType());

        if(userAttr != null)
        {
            keyvalue.put("order_userid", userAttr.getUserid());
            keyvalue.put("order_username", userAttr.getUsername());
            keyvalue.put("order_agentid", userAttr.getAgentid());
            keyvalue.put("order_agentname", userAttr.getAgentname());
            keyvalue.put("order_staffid", userAttr.getDirectStaffid());
            keyvalue.put("order_staffname", userAttr.getDirectStaffname());
        }
        else
        {
            keyvalue.put("order_userid", 0);
            keyvalue.put("order_username", StringUtils.getEmpty());
            keyvalue.put("order_agentid", 0);
            keyvalue.put("order_agentname",StringUtils.getEmpty());
            keyvalue.put("order_staffid", 0);
            keyvalue.put("order_staffname", StringUtils.getEmpty());
        }

        keyvalue.put("order_price", materielInfo.getPrice());
        keyvalue.put("order_quantity", quanlity);
        keyvalue.put("order_brokerage", BigDecimalUtils.getNotNull(brokerage));
        keyvalue.put("order_amount", totalAmount);
        keyvalue.put("order_status", txStatus.getKey());

        if(commodityInfo != null)
        {
            keyvalue.put("order_merchantid", commodityInfo.getMerchantid());
            keyvalue.put("order_merchantname", commodityInfo.getMerchantname());
        }

        keyvalue.put("order_buyer_addressid", addressid);
        keyvalue.put("order_buyer_location", StringUtils.getNotEmpty(buyerLocation));
        keyvalue.put("order_buyer_phone", StringUtils.getNotEmpty(buyerPhone));
        keyvalue.put("order_shop_from", StringUtils.getNotEmpty(shopFrom));

        keyvalue.put("order_createtime", date);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toString());
        }
        else
        {
            keyvalue.put("order_remark", StringUtils.getEmpty());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(String orderno, OrderTxStatus status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_status", status.getKey());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateShippingInfo(String orderno, OrderTxStatus shippingStatus, String shippingTrackno)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_shipping_status", shippingStatus.getKey());

        if(!StringUtils.isEmpty(shippingTrackno))
        {
            setKeyValue.put("order_shipping_trackno", shippingTrackno);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AdEventOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, AdEventOrderInfo.class, orderno);
    }

    public List<AdEventOrderInfo> queryByUser(DateTime fromTime, long userid, int limit)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(TABLE);
        sql.append(" where order_createtime >= ? and order_userid = ? ");
        sql.append(" order by order_createtime desc limit ").append(limit);
        return mSlaveJdbcService.queryForList(sql.toString(), AdEventOrderInfo.class, fromTime.toDate(), userid);
    }

    public BigDecimal findAllHistoryAmountByUser(long userid)
    {
        String sql = "select sum(order_amount) from " + TABLE + " where order_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, BigDecimal.class, userid);
    }


    public List<AdEventOrderInfo> queryByUserAndTxStatus(DateTime date, long userid, OrderTxStatus txStatus, int offset, int size)
    {
       // String timeString = date.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        StringBuilder sql = new StringBuilder();
//        sql.append("select * from ").append(TABLE);
//        sql.append(" where order_createtime >= ? and order_userid = ? and order_status = ? ");
//        sql.append(" limit ").append(offset).append(",").append(size);
      //  return mSlaveJdbcService.queryForList(sql.toString(), AdEventOrderInfo.class, timeString, userid, txStatus.getKey());

        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_ad_materiel as B on A.order_materiel_id = B.materiel_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

            // 时间放前面
            whereSQLBuffer.append(" and order_createtime >= ? ");
            String timeString = date.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
            values.add(timeString);

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }

        String whereSQL = whereSQLBuffer.toString();
        StringBuilder select = new StringBuilder("select A.*, B.materiel_name as order_materiel_name, B.materiel_thumb as order_materiel_thumb, B.materiel_desc as order_materiel_desc, B.materiel_price as order_materiel_price, B.materiel_categoryid as order_materiel_categoryid  from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(offset).append(",").append(size);
        return mSlaveJdbcService.queryForList(select.toString(), AdEventOrderInfo.class, values.toArray());



    }

    public AdEventOrderInfo findLatestOrderInfo(DateTime date, long userid, long materielid)
    {
        String timeString = date.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(TABLE);
        sql.append(" where order_createtime >= ? and order_userid = ? and  order_materiel_id = ?");
        sql.append(" order by order_createtime desc limit 1");
        return mSlaveJdbcService.queryForObject(sql.toString(), AdEventOrderInfo.class, timeString, userid, materielid);
    }

    public List<Long> queryLatestMaterielIds(DateTime date, long userid, int limit)
    {
        String timeString = date.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct order_materiel_id from ").append(TABLE);
        sql.append(" where order_createtime >= ? and order_userid = ? ");
        sql.append(" limit ").append(limit);
        return mSlaveJdbcService.queryForList(sql.toString(), Long.class, timeString, userid);
    }

    public void queryAll(DateTime fromTime, DateTime toTime, AdEventType eventType, Callback<AdEventOrderInfo> callback)
    {
        String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        if(eventType != null)
        {
            String sql = "select * from " + TABLE + " where order_createtime between ? and ? and order_event_type = ?";
            mSlaveJdbcService.queryAll(callback, sql, AdEventOrderInfo.class, fromTimeString, toTimeString, eventType.getKey());
        }
        else
        {
            String sql = "select * from " + TABLE + " where order_createtime between ? and ?";
            mSlaveJdbcService.queryAll(callback, sql, AdEventOrderInfo.class, fromTimeString , toTimeString);
        }
    }

    @Override
    public RowPager<AdEventOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status, AdEventType eventType, long materielid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_ad_materiel as B on A.order_materiel_id = B.materiel_id ");

        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(sysOrderno))
        {
            values.add(sysOrderno);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else
        {
            if(!StringUtils.isEmpty(pageVo.getFromTime()))
            {
                // 时间放前面
                whereSQLBuffer.append(" and order_createtime between ? and ? ");
                values.add(pageVo.getFromTime());
                values.add(pageVo.getToTime());
            }

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
            }

            if(agentid > 0)
            {
                values.add(agentid);
                whereSQLBuffer.append(" and order_agentid = ? ");
            }

            if(staffid > 0)
            {
                values.add(staffid);
                whereSQLBuffer.append(" and order_staffid = ? ");
            }

            if(materielid > 0)
            {
                values.add(materielid);
                whereSQLBuffer.append(" and order_materiel_id = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
        }



        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*, B.materiel_name as order_materiel_name from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AdEventOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdEventOrderInfo.class, values.toArray());
        RowPager<AdEventOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

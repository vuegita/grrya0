package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.PurchaseOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class PurchaseOrderDaoMysql extends DaoSupport implements PurchaseOrderDao {

    /**
     order_no           varchar(50) NOT NULL comment  '系统订单号',

     order_userid       int(11) UNSIGNED NOT NULL comment '商家ID',
     order_username     varchar(255) NOT NULL comment  '',
     order_agentid      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname    varchar(50) NOT NULL comment  '',
     order_staffid      int(11) NOT NULL DEFAULT 0,
     order_staffname    varchar(50) NOT NULL comment  '',

     order_price              decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
     order_quantity           int(11) UNSIGNED NOT NULL comment '采购数量',
     order_total_amount       decimal(18,2) NOT NULL DEFAULT 0 comment '采购总价',
     order_real_amount        decimal(18,2) NOT NULL DEFAULT 0 comment '实际总额',

     order_materielid         int(11) UNSIGNED NOT NULL ,
     order_categoryid         int(11) UNSIGNED NOT NULL ,

     order_status             varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     order_createtime         datetime DEFAULT NULL comment '创建时间',
     order_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

     */
    private static final String TABLE = "inso_ad_mall_shop_purchase_order";

    @Override
    public void addOrder(String orderno, UserAttr userAttr, AdMaterielInfo materielInfo,
                         BigDecimal price, long quantity, OrderTxStatus txStatus,
                         BigDecimal totalAmount, BigDecimal realAmount, JSONObject jsonObject)
    {
        Date date = new Date();


        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_price", price);
        keyvalue.put("order_quantity", quantity);

        keyvalue.put("order_total_amount", totalAmount);
        keyvalue.put("order_real_amount", realAmount);

        keyvalue.put("order_materielid", materielInfo.getId());
        keyvalue.put("order_categoryid", materielInfo.getCategoryid());

        keyvalue.put("order_status", txStatus.getKey());
        keyvalue.put("order_createtime", date);

        if(jsonObject != null && !jsonObject.isEmpty())
        {
            keyvalue.put("order_remark", jsonObject.toJSONString());
        }
        else
        {
            keyvalue.put("order_remark", StringUtils.getEmpty());
        }


        persistent(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_status", status.getKey());

        if(!StringUtils.isEmpty(outTradeNo))
        {
            setKeyValue.put("order_out_trade_no", StringUtils.getNotEmpty(outTradeNo));
        }

        if(jsonObject != null)
        {
            setKeyValue.put("order_remark", jsonObject.toJSONString());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public PurchaseOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, PurchaseOrderInfo.class, orderno);
    }

    @Override
    public RowPager<PurchaseOrderInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, OrderTxStatus txStatus, long categoryid, String sysOrderno)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

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

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }

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

        if(categoryid > 0)
        {
            values.add(categoryid);
            whereSQLBuffer.append(" and order_categoryid = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<PurchaseOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), PurchaseOrderInfo.class, values.toArray());
        RowPager<PurchaseOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

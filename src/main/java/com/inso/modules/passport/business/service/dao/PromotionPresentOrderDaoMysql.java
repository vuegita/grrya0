package com.inso.modules.passport.business.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.PromotionOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class PromotionPresentOrderDaoMysql extends DaoSupport implements PromotionPresentOrderDao {

    private static final String TABLE = "inso_passport_user_promotion_present_order";

    /**
     *
     order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
     order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
     order_business_type          varchar(50) NOT NULL comment ')' ,

     order_userid                 int(11) NOT NULL,
     order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
     order_username               varchar(255) NOT NULL comment  '',

     order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname              varchar(255) NOT NULL comment  '',
     order_staffid                int(11) NOT NULL DEFAULT 0,
     order_staffname              varchar(255) NOT NULL comment  '',

     order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

     order_limit_rate1            decimal(25,8) NOT NULL DEFAULT 0 comment 'rate1',
     order_limit_rate2            decimal(25,8) NOT NULL DEFAULT 0 comment 'rate2',

     order_settle_status          varchar(20) NOT NULL  comment '',
     order_tips                   varchar(255) NOT NULL DEFAULT '' comment '',

     order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     order_amount                 decimal(25,8) NOT NULL comment '流水金额',
     order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
     order_createtime             datetime NOT NULL,
     order_remark                 varchar(3000) DEFAULT '',
     */

    public void addOrder(ICurrencyType currencyType, String orderno, BigDecimal rate1, BigDecimal rate2, PromotionOrderInfo.SettleMode settleStatus, String tips,
                         UserAttr userAttr, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_limit_rate1", rate1);
        keyvalue.put("order_limit_status1", OrderTxStatus.NEW.getKey());

        keyvalue.put("order_limit_rate2", rate2);
        keyvalue.put("order_limit_status2", OrderTxStatus.NEW.getKey());

        keyvalue.put("order_settle_mode", settleStatus.getKey());
        keyvalue.put("order_tips", StringUtils.getNotEmpty(tips));

        keyvalue.put("order_currency", currencyType.getKey());
        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));

        keyvalue.put("order_createtime", new Date());

//        if(remark != null && !remark.isEmpty())
//        {
//            keyvalue.put("order_remark", remark.toJSONString());
//        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, Status showStatus,
                               BigDecimal rate1, OrderTxStatus limit1TxStatus, BigDecimal rate2, OrderTxStatus limit2TxStatus, String tips)
    {
        LinkedHashMap set = Maps.newLinkedHashMap();
        if(txStatus != null)
        {
            set.put("order_status", txStatus.getKey());
        }

        if(showStatus != null)
        {
            set.put("order_show_status", showStatus.getKey());
        }

        if(rate1 != null)
        {
            set.put("order_limit_rate1", rate1);
        }

        if(limit1TxStatus != null)
        {
            set.put("order_limit_status1", limit1TxStatus.getKey());
        }

        if(rate2 != null)
        {
            set.put("order_limit_rate2", rate2);
        }

        if(limit2TxStatus != null)
        {
            set.put("order_limit_status2", limit2TxStatus.getKey());
        }

//        if(settleStatus != null)
//        {
//            set.put("order_settle_status", settleStatus.getKey());
//        }

        set.put("order_tips", StringUtils.getNotEmpty(tips));

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("order_no", orderno);

        update(TABLE, set, where);
    }

//    public void updateOutTradeNo(String orderno, String tradeNo)
//    {
//        String sql = "update " + TABLE + " set order_out_trade_no = ? where order_no = ?";
//        mWriterJdbcService.executeUpdate(sql, tradeNo, orderno);
//    }

    public PromotionOrderInfo findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, PromotionOrderInfo.class, orderno);
    }

//    public PromotionOrderInfo findByOutTradeNo(BusinessType businessType, String outTradeNo)
//    {
//        String sql = "select * from " + TABLE + " where order_out_trade_no = ? and order_business_code = ?";
//        return mSlaveJdbcService.queryForObject(sql, PromotionOrderInfo.class, outTradeNo, businessType.getCode());
//    }

//    public void queryAll(String startTimeString, String endTimeString, BusinessType businessType, Callback<PromotionOrderInfo> callback)
//    {
//        String sql = "select * from " + TABLE + " where order_createtime between ? and ? and order_business_code = ?";
//        mSlaveJdbcService.queryAll(callback, sql, PromotionOrderInfo.class, startTimeString, endTimeString, businessType.getCode());
//    }

    public List<PromotionOrderInfo> queryScrollPageByUser(long userid, DateTime dateTime, int limit)
    {
        String timeStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where order_createtime >= ? and order_userid = ? and (order_status = ? or order_status = ?) ");
        sql.append(" order by order_createtime desc ");
        sql.append(" limit ").append(limit);
        return mSlaveJdbcService.queryForList(sql.toString(), PromotionOrderInfo.class, timeStr, userid, OrderTxStatus.NEW.getKey(), OrderTxStatus.WAITING.getKey());
    }

    @Override
    public RowPager<PromotionOrderInfo> queryScrollPageByUser(PageVo pageVo, long userid, String systemNo, OrderTxStatus txStatus, long agentid, long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
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

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and order_userid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<PromotionOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), PromotionOrderInfo.class, values.toArray());
        RowPager<PromotionOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

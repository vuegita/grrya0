package com.inso.modules.passport.money.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class MoneyOrderDaoMysql extends DaoSupport implements MoneyOrderDao {

    private static final String TABLE = "inso_passport_user_money_order";

    /**
     *   um_order_no                    	varchar(30) NOT NULL comment '内部系统-订单号' ,
     *   um_order_out_trade_no    			varchar(30) NOT NULL comment  '内部系统-订单号, 业务订单号',
     *   um_order_userid	                int(11) NOT NULL,
     *   um_order_username    			    varchar(50) NOT NULL comment  '',
     *   um_order_type         			varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduction=系统扣款|refund=退款|bet=中奖' ,
     *   um_order_status               	varchar(20) NOT NULL  comment 'new=待支付 | realized=处理成功 | error=失败',
     *   um_order_balance            		decimal(18,2) NOT NULL comment '余额',
     *   um_order_amount             		decimal(18,2) NOT NULL comment '流水金额',
     *   um_order_feemoney					decimal(18,2) NOT NULL comment '手续费-提现才有',
     *   um_order_createtime       		datetime NOT NULL comment '和报表时间要一样',
     *   um_order_updatetime      			datetime DEFAULT NULL,
     *   um_order_remark             		varchar(1000) DEFAULT '',
     */

    public void addOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, BusinessType businessType, MoneyOrderType moneyOrderType, OrderTxStatus txStatus,
                         BigDecimal amount, BigDecimal feemoney, Date createtime, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("um_order_no", orderno);
        keyvalue.put("um_order_out_trade_no", outTradeNo);

        keyvalue.put("um_order_userid", userInfo.getId());
        keyvalue.put("um_order_username", userInfo.getName());

        keyvalue.put("um_order_agentid", userAttr.getAgentid());
        keyvalue.put("um_order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("um_order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("um_order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("um_order_fund_key", accountType.getKey());
        keyvalue.put("um_order_currency", currencyType.getKey());

        keyvalue.put("um_order_business_type", businessType.getKey());
        keyvalue.put("um_order_type", moneyOrderType.getKey());
        keyvalue.put("um_order_status", txStatus.getKey());

        keyvalue.put("um_order_amount", amount);

        if(feemoney == null)
        {
            feemoney = BigDecimal.ZERO;
        }
        keyvalue.put("um_order_feemoney", feemoney);

        keyvalue.put("um_order_createtime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("um_order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String outTradeNo, OrderTxStatus txStatus, BigDecimal balance)
    {
        Date uptime = new Date();
        if(balance != null)
        {
            String sql = "update " + TABLE + " set um_order_status = ?, um_order_balance = ?, um_order_updatetime = ? where um_order_out_trade_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), balance, uptime, outTradeNo);
        }
        else
        {
            String sql = "update " + TABLE + " set um_order_status = ?, um_order_updatetime = ? where um_order_out_trade_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), uptime, outTradeNo);
        }
    }

    public BigDecimal findDateTime(long userid, DateTime dateTime, ICurrencyType currencyType)
    {
        String time = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select um_order_balance from " + TABLE + " where um_order_createtime <= ? and um_order_userid = ? and um_order_currency = ? order by um_order_createtime desc limit 1";
        return mSlaveJdbcService.queryForObject(sql, BigDecimal.class, time, userid, currencyType.getKey());
    }

    public MoneyOrder findByTradeNo(String outTradeNo, MoneyOrderType moneyOrderType)
    {
        String sql = "select * from " + TABLE + " where um_order_out_trade_no = ? and um_order_type = ?";
        return mSlaveJdbcService.queryForObject(sql, MoneyOrder.class, outTradeNo, moneyOrderType.getKey());
    }

    public void queryAllMemberOrder(String startTime, String endTime, Callback<MoneyOrder> callback)
    {
        String sql = "select * from " + TABLE + " where um_order_createtime between ? and ?";
        mSlaveJdbcService.queryAll(callback, sql, MoneyOrder.class, startTime, endTime);
    }

    public RowPager<MoneyOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                String systemOrderno, String outTradeno,
                                                ICurrencyType currencyType,
                                                MoneyOrderType orderType, OrderTxStatus txStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(systemOrderno))
        {
            values.add(systemOrderno);
            whereSQLBuffer.append(" and um_order_no = ? ");
        }
        else if(!StringUtils.isEmpty(outTradeno))
        {
            values.add(outTradeno);
            whereSQLBuffer.append(" and um_order_out_trade_no = ? ");
        }
        else
        {
            // 时间放前面
            if(!StringUtils.isEmpty(pageVo.getFromTime())) {
                whereSQLBuffer.append(" and um_order_createtime between ? and ? ");
                values.add(pageVo.getFromTime());
                values.add(pageVo.getToTime());
            }

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and um_order_userid = ? ");
            }

            if(orderType != null)
            {
                values.add(orderType.getKey());
                whereSQLBuffer.append(" and um_order_type = ? ");
            }

            if(currencyType != null)
            {
                values.add(currencyType.getKey());
                whereSQLBuffer.append(" and um_order_currency = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and um_order_status = ? ");
            }

        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and um_order_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and um_order_staffid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_money_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select * from inso_passport_user_money_order ");
        select.append(whereSQL);
        select.append(" order by um_order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MoneyOrder> list = mSlaveJdbcService.queryForList(select.toString(), MoneyOrder.class, values.toArray());
        RowPager<MoneyOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public long countByUserid(long userid)
    {
        String sql = "select count(1) from " + TABLE + " where um_order_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, Long.class, userid);
    }

    public List<MoneyOrder> queryScrollPageByUser(PageVo pageVo, long userid)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where um_order_createtime between ? and ? and ub_order_userid = ?");
        sql.append(" order by ub_order_createtime desc ");
        sql.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        return mSlaveJdbcService.queryForList(sql.toString(), MoneyOrder.class, pageVo.getFromTime(), pageVo.getToTime(), userid);
    }

    public long countActive(DateTime fromTime, DateTime toTime)
    {
        BusinessType businessType = BusinessType.GAME_NEW_LOTTERY;
        String fromTimeStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select count(1) from ");
        sql.append("(");
        sql.append("select count(*) from inso_passport_user_money_order where um_order_createtime between ? and ? and um_order_business_type = ? GROUP BY um_order_userid");
        sql.append(") as t");
        return mSlaveJdbcService.queryForObject(sql.toString(), Long.class, fromTimeStr, toTimeStr, businessType.getKey());
    }
}

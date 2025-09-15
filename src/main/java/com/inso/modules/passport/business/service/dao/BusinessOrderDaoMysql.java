package com.inso.modules.passport.business.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserAttr;

@Repository
public class BusinessOrderDaoMysql extends DaoSupport implements BusinessOrderDao {

    private static final String TABLE = "inso_passport_user_business_order";

    /**
     *
     *   ub_order_no                    	varchar(30) NOT NULL comment '内部系统-订单号',
     *   ub_order_out_trade_no            	    varchar(50) NOT NULL DEFAULT '' comment '引用外部id,如果有',
     *   ub_order_userid	                int(11) NOT NULL,
     *   ub_order_username    			    varchar(50) NOT NULL comment  '',
     *   ub_order_business_code   			int(11) NOT NULL comment '业务类型',
     *   ub_order_type						varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduct=系统扣款|task_donate=任务赠送|first_donate=首充赠送)' ,
     *   ub_order_status               	varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     *   ub_order_amount             		decimal(18,2) NOT NULL comment '流水金额',
     *   ub_order_feemoney					decimal(18,2) NOT NULL comment '手续费-提现才有',
     *   ub_order_createtime       		datetime NOT NULL,
     *   ub_order_updatetime      			datetime DEFAULT NULL,
     *   ub_order_remark             		varchar(3000) DEFAULT '',
     */

    public void addOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, String outTradeNo, UserAttr userAttr, BusinessType businessType, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("ub_order_no", orderno);
        keyvalue.put("ub_order_out_trade_no", outTradeNo);

        keyvalue.put("ub_order_userid", userAttr.getUserid());
        keyvalue.put("ub_order_username", userAttr.getUsername());

        keyvalue.put("ub_order_agentid", userAttr.getAgentid());
        keyvalue.put("ub_order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("ub_order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("ub_order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("ub_order_fund_key", accountType.getKey());
        keyvalue.put("ub_order_currency", currencyType.getKey());

        keyvalue.put("ub_order_business_code", businessType.getCode());
        keyvalue.put("ub_order_business_name", businessType.getKey());
        keyvalue.put("ub_order_status", txStatus.getKey());

        keyvalue.put("ub_order_amount", amount);
        keyvalue.put("ub_order_feemoney", BigDecimalUtils.getNotNull(feemoney));

        keyvalue.put("ub_order_createtime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("ub_order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        checker = StringUtils.getNotEmpty(checker);

        Date uptime = new Date();
        if(remark == null || remark.isEmpty())
        {
            String sql = "update " + TABLE + " set ub_order_status = ?, ub_order_checker = ?, ub_order_updatetime = ? where ub_order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), checker, uptime, orderno);
        }
        else
        {
            String sql = "update " + TABLE + " set ub_order_status = ?, ub_order_checker = ?, ub_order_remark = ?, ub_order_updatetime = ? where ub_order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(),checker,  remark.toJSONString(), uptime, orderno);
        }
    }

    public void updateOutTradeNo(String orderno, String tradeNo)
    {
        String sql = "update " + TABLE + " set ub_order_out_trade_no = ? where ub_order_no = ?";
        mWriterJdbcService.executeUpdate(sql, tradeNo, orderno);
    }

    public BusinessOrder findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where ub_order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, BusinessOrder.class, orderno);
    }

    public BusinessOrder findByOutTradeNo(BusinessType businessType, String outTradeNo)
    {
        String sql = "select * from " + TABLE + " where ub_order_out_trade_no = ? and ub_order_business_code = ?";
        return mSlaveJdbcService.queryForObject(sql, BusinessOrder.class, outTradeNo, businessType.getCode());
    }

    public List<BusinessOrder> queryByAgent(DateTime fromTime, DateTime toTime, BusinessType[] arr, long agentid, UserInfo.UserType userType, long userid, int offset, int pageSize)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        // "select * from " + TABLE + " ub_order_createtime between ? and ? and ub_order_userid = ? "

        whereSQLBuffer.append("select * from ").append(TABLE);
        whereSQLBuffer.append(" where ub_order_createtime between ? and ? ");

        values.add(fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        values.add(toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));

        if(userType == UserInfo.UserType.AGENT)
        {
            whereSQLBuffer.append(" and ub_order_agentid = ?");
        }
        else
        {
            whereSQLBuffer.append(" and ub_order_staffid = ?");
        }
        values.add(agentid);

        if(userid > 0)
        {
            whereSQLBuffer.append(" and ub_order_userid = ?");
            values.add(userid);
        }

        boolean first = true;
        whereSQLBuffer.append(" and ( ");
        for(BusinessType businessType : arr)
        {
            values.add(businessType.getCode());
            if(first)
            {
                first = false;
            }
            else
            {
                whereSQLBuffer.append(" or ");
            }
            whereSQLBuffer.append(" ub_order_business_code = ? ");
        }
        whereSQLBuffer.append(" )");
        whereSQLBuffer.append(" limit ").append(offset).append(",").append(pageSize);
        return mSlaveJdbcService.queryForList(whereSQLBuffer.toString(), BusinessOrder.class, values.toArray());
    }

    public void queryAll(String startTimeString, String endTimeString, BusinessType businessType, Callback<BusinessOrder> callback)
    {
        String sql = "select * from " + TABLE + " where ub_order_createtime between ? and ? and ub_order_business_code = ?";
        mSlaveJdbcService.queryAll(callback, sql, BusinessOrder.class, startTimeString, endTimeString, businessType.getCode());
    }

    public List<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, BusinessType businessType, long pageStart, long pageSize)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where ub_order_createtime between ? and ? and ub_order_userid = ? and ub_order_business_code = ? ");

//        if(txStatus != null)
//        {
//            sql.append(" and ( ");
//            boolean first = true;
//            for(OrderTxStatus tmp : txStatus)
//            {
//                if(first)
//                {
//                    first = true;
//                }
//                else
//                {
//                    sql.append(" or ");
//                }
//                sql.append(" ub_order_status = ? ");
//            }
//            sql.append(" > ");
//        }

        sql.append(" order by ub_order_createtime desc ");
        sql.append(" limit ").append(pageStart).append(",").append(pageSize);
        return mSlaveJdbcService.queryForList(sql.toString(), BusinessOrder.class, pageVo.getFromTime(), pageVo.getToTime(), userid, businessType.getCode());
    }

    @Override
    public RowPager<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, String systemNo, String outTradeNo,
                                                         BusinessType[] businessTypeArray, ICurrencyType currencyType, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,long agentid,long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and ub_order_no = ? ");
        }
        else if(!StringUtils.isEmpty(outTradeNo))
        {
            values.add(outTradeNo);
            whereSQLBuffer.append(" and ub_order_out_trade_no = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and ub_order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            boolean first = true;
            whereSQLBuffer.append(" and ( ");
            for(BusinessType businessType : businessTypeArray)
            {
                values.add(businessType.getCode());
                if(first)
                {
                    first = false;
                }
                else
                {
                    whereSQLBuffer.append(" or ");
                }
                whereSQLBuffer.append(" ub_order_business_code = ? ");
            }
            whereSQLBuffer.append(" )");

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and ub_order_status = ? ");
            }
            else if(ignoreTxStatus != null)
            {
                values.add(ignoreTxStatus.getKey());
                whereSQLBuffer.append(" and ub_order_status != ? ");
            }

            if(currencyType != null)
            {
                values.add(currencyType.getKey());
                whereSQLBuffer.append(" and ub_order_currency = ? ");
            }
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and ub_order_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and ub_order_staffid = ? ");
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and ub_order_userid = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_business_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_passport_user_business_order ");
        select.append(whereSQL);
        select.append(" order by ub_order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<BusinessOrder> list = mSlaveJdbcService.queryForList(select.toString(), BusinessOrder.class, values.toArray());
        RowPager<BusinessOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

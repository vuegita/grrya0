package com.inso.modules.web.settle.service.dao;

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
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class SettleOrderDaoMysql extends DaoSupport implements SettleOrderDao {

    private static final String TABLE = "inso_web_settle_order";

    @Override
    public void addOrder(CryptoNetworkType networkType, ICurrencyType currencyType, SettleBusinessType businessType,
                         String orderno, String ouTradeNo, UserAttr userAttr, OrderTxStatus txStatus,
                         BigDecimal amount, BigDecimal feemoney, Date createtime , String account, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_out_trade_no", ouTradeNo);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", StringUtils.getNotEmpty(userAttr.getUsername()));
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_transfer_no", StringUtils.getEmpty());
        keyvalue.put("order_transfer_amount", BigDecimal.ZERO);
        keyvalue.put("order_settle_status", OrderTxStatus.WAITING.getKey());

        keyvalue.put("order_network_type", networkType.getKey());
        keyvalue.put("order_currency", currencyType.getKey());

        keyvalue.put("order_business_type", businessType.getKey());
        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));
        keyvalue.put("order_beneficiary_account", account);

        keyvalue.put("order_createtime", createtime);
        keyvalue.put("order_updatetime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, Date createtime, String checker, RemarkVO remark,OrderTxStatus settleStatus)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_status", txStatus.getKey());

//        if(!StringUtils.isEmpty(outTradeNo))
//        {
//            setKeyValue.put("order_out_trade_no", outTradeNo);
//        }

        if(createtime != null)
        {
            setKeyValue.put("order_createtime", createtime);
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }

        if(settleStatus != null )
        {
            setKeyValue.put("order_settle_status", settleStatus.getKey());
        }

        setKeyValue.put("order_checker", StringUtils.getNotEmpty(checker));

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(TABLE, setKeyValue, whereKeyValue);

    }

    public void updateTxStatus(String beneficiaryAccount, CryptoNetworkType networkType, CryptoCurrency currency,
                               OrderTxStatus txStatus, Date createtime, BigDecimal validAmount, JSONObject jsonObject,String transferNo, BigDecimal transferAmount )
    {

        DateTime endDateTime = new DateTime(createtime.getTime()).plusSeconds(1);
        DateTime beginDateTime = endDateTime.minusDays(150);

        String beginTimeStr = DateUtils.convertString(beginDateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeStr = DateUtils.convertString(endDateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        // and order_amount <= ?
        String remark = StringUtils.getEmpty();
        if(jsonObject != null && !jsonObject.isEmpty())
        {
            remark = jsonObject.toJSONString();
        }

        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(TABLE);
        sql.append(" set order_status = ?, order_createtime = ?, order_remark = ?,order_transfer_no = ?,order_transfer_amount = ? ");
        sql.append(" where order_beneficiary_account = ? and order_network_type = ? and order_currency = ? and order_status = ? and order_createtime between ? and ?");

//        StringBuffer select = new StringBuffer();
//        select.append("select count(1) from ").append(TABLE);
//        select.append(" where order_beneficiary_account = '" + beneficiaryAccount + "'");
//        select.append(" and order_network_type = '" + networkType.getKey() + "'");
//        select.append(" and order_currency = '" + currency.getKey() + "'");
//        select.append(" and order_status = '" + OrderTxStatus.WAITING.getKey() + "'");
//        select.append(" and order_createtime between '" + beginTimeStr + "'");
//        select.append(" and '" + endTimeStr +"' ");
//
//        count(select.toString());

        mWriterJdbcService.executeUpdate(sql.toString(), txStatus.getKey(), createtime, remark, transferNo , transferAmount,
                beneficiaryAccount, networkType.getKey(), currency.getKey(),
                OrderTxStatus.WAITING.getKey(),beginTimeStr, endTimeStr);
    }

    public BigDecimal findWithdrowAmountBytransferNo(String  transferNo )
    {
        String sql = "select sum(order_amount) from " + TABLE + " where order_transfer_no = ?";
        return mSlaveJdbcService.queryForObject(sql, BigDecimal.class, transferNo);
    }

    public long count(String sql)
    {
        try {
            long count = mSlaveJdbcService.count(sql);
            LOG.info("rs count = " + count + ", and sql = " + sql);
        } catch (Exception e) {
            LOG.error("handle count error:", e);
        }
        return -1;
    }

    public SettleOrderInfo findByOrderno(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, SettleOrderInfo.class, orderno);
    }

    public void queryAll(DateTime startTime, DateTime endTime, Callback<SettleOrderInfo> callback)
    {
        String startTimeStr = startTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeStr = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where order_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, SettleOrderInfo.class, startTimeStr, endTimeStr);
    }

    @Override
    public RowPager<SettleOrderInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                     ICurrencyType currencyType,
                                                     String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                     String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");
        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(!StringUtils.isEmpty(outTradeNo))
        {

            values.add(outTradeNo);
            whereSQLBuffer.append(" and order_out_trade_no = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
            }

            if(!StringUtils.isEmpty(transferNo))
            {
                values.add(transferNo);
                whereSQLBuffer.append(" and order_transfer_no = ? ");
            }

            if(settleStatus != null)
            {
                values.add(settleStatus.getKey());
                whereSQLBuffer.append(" and order_settle_status = ? ");
            }

            if(currencyType != null)
            {
                values.add(currencyType.getKey());
                whereSQLBuffer.append(" and order_currency = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
            else if(ignoreTxStatus != null)
            {
                values.add(ignoreTxStatus.getKey());
                whereSQLBuffer.append(" and order_status != ? ");
            }

            if(!StringUtils.isEmpty(beneficiaryAccount))
            {
                values.add(beneficiaryAccount);
                whereSQLBuffer.append(" and order_beneficiary_account = ? ");
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

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + TABLE + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from " + TABLE + " as A ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SettleOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), SettleOrderInfo.class, values.toArray());
        RowPager<SettleOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }




    @Override
    public RowPager<SettleOrderInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                     ICurrencyType currencyType,
                                                     String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                     String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus,long reportid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_web_settle_withdraw_order_report as B on B.or_orderno = A.order_no ");
        whereSQLBuffer.append(" left join inso_web_settle_withdraw_report as C on C.report_id = B.or_reportid ");
        whereSQLBuffer.append(" where 1 = 1 ");
        if(!StringUtils.isEmpty(systemNo))
        {
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

            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(!StringUtils.isEmpty(outTradeNo))
        {
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

            values.add(outTradeNo);
            whereSQLBuffer.append(" and order_out_trade_no = ? ");
        }
        else if(reportid > 0)
        {
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

            values.add(reportid);
            whereSQLBuffer.append(" and B.or_reportid = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

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

            if(!StringUtils.isEmpty(transferNo))
            {
                values.add(transferNo);
                whereSQLBuffer.append(" and order_transfer_no = ? ");
            }

            if(settleStatus != null)
            {
                values.add(settleStatus.getKey());
                whereSQLBuffer.append(" and order_settle_status = ? ");
            }

            if(currencyType != null)
            {
                values.add(currencyType.getKey());
                whereSQLBuffer.append(" and order_currency = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
            else if(ignoreTxStatus != null)
            {
                values.add(ignoreTxStatus.getKey());
                whereSQLBuffer.append(" and order_status != ? ");
            }

            if(!StringUtils.isEmpty(beneficiaryAccount))
            {
                values.add(beneficiaryAccount);
                whereSQLBuffer.append(" and order_beneficiary_account = ? ");
            }

        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + TABLE + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from " + TABLE + " as A ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SettleOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), SettleOrderInfo.class, values.toArray());
        RowPager<SettleOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

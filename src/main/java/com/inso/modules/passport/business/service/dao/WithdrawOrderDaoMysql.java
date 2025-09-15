package com.inso.modules.passport.business.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class WithdrawOrderDaoMysql extends DaoSupport implements WithdrawOrderDao {

    private static final String TABLE = "inso_passport_user_withdraw_order";

    @Override
    public void addOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, UserInfo userInfo, UserAttr userAttr, OrderTxStatus txStatus, PayProductType productType,
                         BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark , String account, String idcard, ChannelInfo channelInfo)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_pay_product_type", productType.getKey());

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        if(channelInfo != null)
        {
            keyvalue.put("order_channelid", channelInfo.getId());
            keyvalue.put("order_channelname", channelInfo.getName());
        }

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_fund_key", accountType.getKey());
        keyvalue.put("order_currency", currencyType.getKey());

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));
        keyvalue.put("order_beneficiary_account", account);
        keyvalue.put("order_beneficiary_idcard", idcard);


        keyvalue.put("order_createtime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark, long submitCount)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(txStatus != null)
        {
            setKeyValue.put("order_status", txStatus.getKey());
        }

        if(submitCount > 0)
        {
            setKeyValue.put("order_submit_count", submitCount);
        }

        if(!StringUtils.isEmpty(outTradeNo))
        {
            setKeyValue.put("order_out_trade_no", outTradeNo);
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }

        setKeyValue.put("order_checker", StringUtils.getNotEmpty(checker));
        setKeyValue.put("order_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(TABLE, setKeyValue, whereKeyValue);

//        Date uptime = new Date();
//        if(remark == null || remark.isEmpty())
//        {
//            String sql = "update " + TABLE + " set order_status = ?, order_checker = ?, order_updatetime = ? where order_no = ?";
//            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), checker, uptime, orderno);
//        }
//        else
//        {
//            String sql = "update " + TABLE + " set order_status = ?, order_checker = ?, order_remark = ?, order_updatetime = ? where order_no = ?";
//            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(),checker,  remark.toJSONString(), uptime, orderno);
//        }
    }

    public void updateOutTradeNo(String orderno, String tradeNo)
    {
        String sql = "update " + TABLE + " set order_out_trade_no = ? where order_no = ? and order_out_trade_no = '' ";
        mWriterJdbcService.executeUpdate(sql, tradeNo, orderno);
    }

    public WithdrawOrder findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, WithdrawOrder.class, orderno);
    }

    public WithdrawOrder findByOutTradeNo(String outTradeNo)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no = ?";
        return mSlaveJdbcService.queryForObject(sql, WithdrawOrder.class, outTradeNo);
    }

    public void queryAll(String startTimeString, String endTimeString, Callback<WithdrawOrder> callback)
    {
        String sql = "select * from " + TABLE + " where order_createtime between ? and ?";
        mSlaveJdbcService.queryAll(true, callback, sql, WithdrawOrder.class, startTimeString, endTimeString);
    }

    public void queryAllByUpdateTime(DateTime startTime, DateTime enTime, Callback<WithdrawOrder> callback)
    {
        String fromCreateTime = startTime.minusDays(7).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String startTimeString = startTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = enTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where order_createtime > ? and order_updatetime between ? and ?";
        mSlaveJdbcService.queryAll(callback, sql, WithdrawOrder.class, fromCreateTime, startTimeString, endTimeString);
    }

    public List<WithdrawOrder> queryScrollPageByUser(PageVo pageVo, long userid, OrderTxStatus[] txStatusArray, long offset, long pagesize)
    {
        List<Object> values = Lists.newArrayList();

        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where order_createtime between ? and ? and order_userid = ? ");

        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());
        values.add(userid);

        if(txStatusArray != null)
        {
            sql.append(" and ( ");
            boolean first = true;
            for(OrderTxStatus tmp : txStatusArray)
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    sql.append(" or ");
                }
                sql.append(" order_status = ? ");
                values.add(tmp.getKey());
            }
            sql.append(" ) ");
        }

        sql.append(" order by order_createtime desc ");
        sql.append(" limit ").append(offset).append(",").append(pagesize);
        return mSlaveJdbcService.queryForList(sql.toString(), WithdrawOrder.class, values.toArray());
    }

    @Override
    public RowPager<WithdrawOrder> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid,
                                                         String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                         String beneficiaryAccount,String beneficiaryIdcard)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.order_userid ");
        whereSQLBuffer.append(" and C.money_fund_key = A.order_fund_key and C.money_currency = A.order_currency ");
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

            if(!StringUtils.isEmpty(beneficiaryIdcard))
            {
                values.add(beneficiaryIdcard);
                whereSQLBuffer.append(" and order_beneficiary_idcard = ? ");
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
        String countsql = "select count(1) from inso_passport_user_withdraw_order as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*,C.money_balance as order_balance,C.money_total_recharge as order_total_recharge,(C.money_total_withdraw-C.money_total_refund) as order_total_withdraw from inso_passport_user_withdraw_order as A ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<WithdrawOrder> list = mSlaveJdbcService.queryForList(select.toString(), WithdrawOrder.class, values.toArray());
        RowPager<WithdrawOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    @Override
    public WithdrawOrder queryTotalWithdrawAmountScrollPage(PageVo pageVo, long userid, CryptoCurrency currencyType , OrderTxStatus txStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

            // 时间放前面
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
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


        String whereSQL = whereSQLBuffer.toString();

        StringBuilder select = new StringBuilder("select *,SUM(order_amount) as order_total_withdraw_amount  from inso_passport_user_withdraw_order ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());


        return  mSlaveJdbcService.queryForObject(select.toString(), WithdrawOrder.class, values.toArray());

    }

}

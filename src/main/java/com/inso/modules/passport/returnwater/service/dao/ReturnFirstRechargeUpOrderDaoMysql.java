package com.inso.modules.passport.returnwater.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ReturnFirstRechargeUpOrderDaoMysql extends DaoSupport implements ReturnFirstRechargeUpOrderDao {

    private static final String TABLE = "inso_passport_first_recharge_present_return_up_order";

    @Override
    public void addOrder(int level, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, FundAccountType accountType, ICurrencyType currencyType,
                         OrderTxStatus txStatus, BigDecimal amount, Date createtime, RemarkVO remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_out_trade_no", outTradeNo);

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        keyvalue.put("order_fund_key", accountType.getKey());
        keyvalue.put("order_currency", currencyType.getKey());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_from_level", level);

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);

        keyvalue.put("order_createtime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        checker = StringUtils.getNotEmpty(checker);

        Date uptime = new Date();
        if(remark == null || remark.isEmpty())
        {
            String sql = "update " + TABLE + " set order_status = ?, order_checker = ?, order_updatetime = ? where order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), checker, uptime, orderno);
        }
        else
        {
            String sql = "update " + TABLE + " set order_status = ?, order_checker = ?, order_remark = ?, order_updatetime = ? where order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(),checker,  remark.toJSONString(), uptime, orderno);
        }
    }

    public ReturnWaterOrder findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, ReturnWaterOrder.class, orderno);
    }

    public ReturnWaterOrder findByOutTradeNo(String outTradeNo)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no = ?";
        return mSlaveJdbcService.queryForObject(sql, ReturnWaterOrder.class, outTradeNo);
    }

    public void queryAll(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback)
    {
        String sql = null;
        if(StringUtils.isEmpty(startTimeString))
        {
            sql = "select * from " + TABLE;
            mSlaveJdbcService.queryAll(onlyEntity, callback, sql, ReturnWaterOrder.class);
        }
        else
        {
            sql = "select * from " + TABLE + " where order_createtime between ? and ?";
            mSlaveJdbcService.queryAll(onlyEntity, callback, sql, ReturnWaterOrder.class, startTimeString, endTimeString);
        }
    }

    public void statsAmountByTime(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("order_userid, order_username, order_from_level, sum(order_amount) order_amount ");
        sql.append(" from ").append(TABLE);
        sql.append(" where order_createtime between ? and ? and order_status = ?");
        sql.append(" group by order_userid, order_username, order_from_level ");
        mSlaveJdbcService.queryAll(onlyEntity, callback, sql.toString(), ReturnWaterOrder.class, startTimeString, endTimeString, OrderTxStatus.REALIZED.getKey());
    }

    public List<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where order_createtime between ? and ? and order_userid = ? ");

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
//                sql.append(" order_status = ? ");
//            }
//            sql.append(" > ");
//        }

        sql.append(" order by order_createtime desc ");
        sql.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        return mSlaveJdbcService.queryForList(sql.toString(), ReturnWaterOrder.class, pageVo.getFromTime(), pageVo.getToTime(), userid);
    }

    @Override
    public RowPager<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid,  String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

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
        String countsql = "select count(1) from " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ReturnWaterOrder> list = mSlaveJdbcService.queryForList(select.toString(), ReturnWaterOrder.class, values.toArray());
        RowPager<ReturnWaterOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.passport.business.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class RechargeOrderDaoMysql extends DaoSupport implements RechargeOrderDao {

    private static final String TABLE = "inso_passport_user_recharge_order";

    /**
     *
     *   order_no                    	varchar(30) NOT NULL comment '内部系统-订单号',
     *   order_out_trade_no            	    varchar(50) NOT NULL DEFAULT '' comment '引用外部id,如果有',
     *   order_userid	                int(11) NOT NULL,
     *   order_username    			    varchar(50) NOT NULL comment  '',
     *   order_business_code   			int(11) NOT NULL comment '业务类型',
     *   order_type						varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduct=系统扣款|task_donate=任务赠送|first_donate=首充赠送)' ,
     *   order_status               	varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     *   order_amount             		decimal(18,2) NOT NULL comment '流水金额',
     *   order_feemoney					decimal(18,2) NOT NULL comment '手续费-提现才有',
     *   order_createtime       		datetime NOT NULL,
     *   order_updatetime      			datetime DEFAULT NULL,
     *   order_remark             		varchar(3000) DEFAULT '',
     */

    @Override
    public void addOrder(ChannelInfo channelInfo, FundAccountType accountType, ICurrencyType currencyType, String orderno, UserInfo userInfo, UserAttr userAttr, OrderTxStatus txStatus, PayProductType productType, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_pay_product_type", productType.getKey());

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        keyvalue.put("order_channelid", channelInfo.getId());
        keyvalue.put("order_channelname", channelInfo.getName());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_fund_key", accountType.getKey());
        keyvalue.put("order_currency", currencyType.getKey());

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));

        keyvalue.put("order_createtime", createtime);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark)
    {

        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(txStatus != null)
        {
            setKeyValue.put("order_status", txStatus.getKey());
        }

        if(!StringUtils.isEmpty(outTradeNo))
        {
            setKeyValue.put("order_out_trade_no", outTradeNo);
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }

        if(!StringUtils.isEmpty(checker))
        {
            setKeyValue.put("order_checker", StringUtils.getNotEmpty(checker));
        }

        setKeyValue.put("order_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(TABLE, setKeyValue, whereKeyValue);

//        checker = StringUtils.getNotEmpty(checker);
//
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

    public void updateOutTradeNo(String orderno, OrderTxStatus txStatus, String tradeNo)
    {
        if(txStatus == OrderTxStatus.PENDING)
        {
            String sql = "update " + TABLE + " set order_status = ?, order_out_trade_no = ? where order_no = ? and order_out_trade_no = '' ";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), tradeNo, orderno);
        }
        else
        {
            String sql = "update " + TABLE + " set order_out_trade_no = ? where order_no = ? and order_out_trade_no = '' ";
            mWriterJdbcService.executeUpdate(sql, tradeNo, orderno);
        }
    }

    public void updateAmount(String orerno, BigDecimal amount)
    {
        String sql = "update " + TABLE + " set order_amount = ? where order_no = ? ";
        mWriterJdbcService.executeUpdate(sql, amount, orerno);
    }

    public RechargeOrder findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, RechargeOrder.class, orderno);
    }

    public RechargeOrder findByOutTradeNo(String outTradeNo)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no = ?";
        return mSlaveJdbcService.queryForObject(sql, RechargeOrder.class, outTradeNo);
    }

    public void queryAll(String startTimeString, String endTimeString, Callback<RechargeOrder> callback)
    {
        String sql = "select * from " + TABLE + " where order_createtime between ? and ?";
        mSlaveJdbcService.queryAll(true, callback, sql, RechargeOrder.class, startTimeString, endTimeString);
    }

    public List<RechargeOrder> queryScrollPageByUser(PageVo pageVo, long userid, OrderTxStatus[] txStatuseArray, long offset, long pagesize)
    {
        List<Object> values = Lists.newArrayList();

        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where order_createtime between ? and ? and order_userid = ? ");

        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());
        values.add(userid);

        if(txStatuseArray != null)
        {
            sql.append(" and ( ");
            boolean first = true;
            for(OrderTxStatus tmp : txStatuseArray)
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
        return mSlaveJdbcService.queryForList(sql.toString(), RechargeOrder.class, values.toArray());
    }

    @Override
    public RowPager<RechargeOrder> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, long channelid)
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

        if(channelid > 0)
        {
            values.add(channelid);
            whereSQLBuffer.append(" and order_channelid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_recharge_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_passport_user_recharge_order ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RechargeOrder> list = mSlaveJdbcService.queryForList(select.toString(), RechargeOrder.class, values.toArray());
        RowPager<RechargeOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    @Override
    public RowPager<RechargeOrder> queryScrollPageByUserOrderBy(PageVo pageVo, long userid, long agentid, long staffid, String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, String sortName, String sortOrder, long channelid) {
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


        if(channelid > 0)
        {
            values.add(channelid);
            whereSQLBuffer.append(" and order_channelid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_recharge_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_passport_user_recharge_order ");
        select.append(whereSQL);
        if(sortName!=null && sortOrder!=null){
            select.append(" order by "+" order_"+sortName +" "+sortOrder);
        }else{
            select.append(" order by order_createtime desc ");
        }
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RechargeOrder> list = mSlaveJdbcService.queryForList(select.toString(), RechargeOrder.class, values.toArray());
        RowPager<RechargeOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    /**
     * 首次充值列表
     * @param pageVo
     * @param userid
     * @return
     */
    public RowPager<RechargeOrder> queryFirstRechargeScrollPage(PageVo pageVo, long userid, long agentid,long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from inso_passport_user_attr as A  ");

        whereSQLBuffer.append(" inner join inso_passport_user_recharge_order as B on A.attr_first_recharge_orderno = B.order_no ");
        whereSQLBuffer.append(" inner join inso_passport_user as C on A.attr_userid = C.user_id and C.user_type= 'member' ");

        whereSQLBuffer.append(" where 1 = 1 ");


        if(userid > 0)
        {
            whereSQLBuffer.append(" and A.attr_userid = ? ");
            values.add(userid);
        }
        else
        {
            if(agentid > 0)
            {
                whereSQLBuffer.append(" and A.attr_agentid = ?");
                values.add(agentid);
            }

            if(staffid > 0)
            {
                whereSQLBuffer.append(" and A.attr_direct_staffid = ? ");
                values.add(staffid);
            }

            // 时间放前面
            whereSQLBuffer.append(" and A.attr_first_recharge_time between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

//            whereSQLBuffer.append(" and A.attr_first_recharge_orderno != ''  ");

        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.attr_parentname as order_parentname, A.attr_grantfathername as order_grantfathername, B.* ");
        select.append(whereSQL);
        select.append(" order by B.order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RechargeOrder> list = mSlaveJdbcService.queryForList(select.toString(), RechargeOrder.class, values.toArray());
        RowPager<RechargeOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

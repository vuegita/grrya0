package com.inso.modules.coin.core.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MutisignTransferOrderDaoMysql extends DaoSupport implements MutisignTransferOrderDao {

    /**
     order_no                    	varchar(30) NOT NULL comment '内部系统-订单号',
     order_out_trade_no       	    varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有hash',

     order_userid	                int(11) NOT NULL,
     order_username    			varchar(50) NOT NULL comment  '',

     order_address_in	            varchar(255) NOT NULL comment '输入地址',
     order_address_out	            varchar(255) NOT NULL comment '转出地址',

     order_amount             		decimal(18,2) NOT NULL comment '转账金额',
     order_feemoney                decimal(18,2) NOT NULL comment '手续费(矿工费)',

     order_type         			varchar(50) NOT NULL comment  '转账类型: 直接转账|授权转账',
     order_status               	varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',

     order_createtime       		datetime NOT NULL,
     order_updatetime      		datetime DEFAULT NULL,
     order_remark             		varchar(3000) DEFAULT '',
     */
    private static final String TABLE = "inso_coin_mutisign_transfer_order";

    @Override
    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         CryptoNetworkType networkType, CryptoCurrency currency,
                         String fromAddress, String toAddress, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark)
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

        keyvalue.put("order_network_type", networkType.getKey());
        keyvalue.put("order_currency_type", currency.getKey());
        keyvalue.put("order_from_address", fromAddress);
        keyvalue.put("order_to_address", toAddress);
        keyvalue.put("order_total_amount", totalAmount);
        keyvalue.put("order_feemoney", totalFeemoney);

        // 项目方
        keyvalue.put("order_to_project_amount", toProjectAmount);
        // 代理
        keyvalue.put("order_to_agent_amount", toAgentAmount);
        // 平台方
        keyvalue.put("order_to_platform_amount", toPlatformAmount);

        keyvalue.put("order_type", StringUtils.getEmpty());
        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_createtime", date);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }
        else
        {
            keyvalue.put("order_remark", StringUtils.getEmpty());
        }

        persistent(TABLE, keyvalue);
    }

    public void deleteByNo(String orderno)
    {
        String sql = "delete from " + TABLE + " where order_no = ?";
        mWriterJdbcService.executeUpdate(sql, orderno);
    }

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

    public MutiSignTransferOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, MutiSignTransferOrderInfo.class, orderno);
    }

    public void queryAll(Callback<MutiSignTransferOrderInfo> callback, DateTime from, DateTime toTime, OrderTxStatus txStatus)
    {
        String fromStr = from.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where order_createtime between ? and ? and order_status = ?";
        mSlaveJdbcService.queryAll(callback, sql, MutiSignTransferOrderInfo.class, fromStr, toStr, txStatus.getKey());
    }


    @Override
    public RowPager<MutiSignTransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status, CryptoCurrency currencyType, String sortOrder , String sortName)
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

            if(networkType != null)
            {
                values.add(networkType.getKey());
                whereSQLBuffer.append(" and order_ctr_network_type = ? ");
            }

            if(currencyType != null)
            {
                values.add(currencyType.getKey());
                whereSQLBuffer.append(" and order_currency_type = ? ");
            }


            if(status != null)
            {
                values.add(status.getKey());
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

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        if(sortName!=null && sortOrder!=null){

            if(sortName.equals("totalAmount")){
                select.append(" order by order_total_amount "+" "+sortOrder);
            }

        }else{
            select.append(" order by order_createtime desc ");
        }


        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MutiSignTransferOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), MutiSignTransferOrderInfo.class, values.toArray());
        RowPager<MutiSignTransferOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

package com.inso.modules.coin.withdraw.service.dao;

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
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.coin.withdraw.model.CoinWithdrawOrderInfo;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CoinWithdrawOrderDaoMysql extends DaoSupport implements CoinWithdrawOrderDao {

    /**
     order_no                    	varchar(30) NOT NULL comment '内部系统-订单号',
     order_out_trade_no       	    varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',

     order_channelid               int(11) UNSIGNED NOT NULL comment '提币通道ID',

     order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
     order_username                varchar(255) NOT NULL comment  '',
     order_agentid 	            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname               varchar(255) NOT NULL comment  '',
     order_staffid	                int(11) UNSIGNED NOT NULL DEFAULT 0,
     order_staffname               varchar(255) NOT NULL comment  '',

     order_business_key	        varchar(255) NOT NULL comment '',
     order_business_name	        varchar(255) NOT NULL comment '',

     order_network_type	        varchar(255) NOT NULL comment '所属网络',
     order_currency_type	        varchar(255) NOT NULL comment '所属代币',
     order_from_address	        varchar(255) NOT NULL comment '',
     order_to_address	            varchar(255) NOT NULL comment '',

     order_status               	varchar(20) NOT NULL  comment 'new=待支付 | realized=处理成功 | failed=失败',
     order_amount             		decimal(25,8) NOT NULL comment '流水金额',
     order_feemoney                decimal(25,8) NOT NULL comment '手续费',

     order_checker    			    varchar(50) NOT NULL DEFAULT '' comment  '审核人',
     order_createtime       		datetime NOT NULL,
     order_remark             		varchar(3000) DEFAULT '',
     */
    private static final String TABLE = "inso_coin_withdraw_order";

    @Override
    public void addOrder(String orderno, UserAttr userAttr,
                         CoinWithdrawChannel channelInfo,
                         OrderTxStatus txStatus,
                         BusinessType businessType, CryptoCurrency currency,
                         String toAddress, BigDecimal amount, BigDecimal feemoney)
    {
        Date date = new Date();

        feemoney = BigDecimalUtils.getNotNull(feemoney);

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_channelid", channelInfo.getId());

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_business_key", businessType.getKey());
        keyvalue.put("order_business_name", businessType.getKey());

        keyvalue.put("order_network_type", channelInfo.getNetworkType());
        keyvalue.put("order_currency_type", currency.getKey());
        keyvalue.put("order_from_address", channelInfo.getTriggerAddress());
        keyvalue.put("order_to_address", toAddress);

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", feemoney);

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_createtime", date);
        keyvalue.put("order_remark", StringUtils.getEmpty());

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

        setKeyValue.put("order_updatetime", new Date());
        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public CoinWithdrawOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, CoinWithdrawOrderInfo.class, orderno);
    }


    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<CoinWithdrawOrderInfo> callback)
    {
        String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        if(txStatus != null)
        {
            String sql = "select * from " + TABLE + " where order_createtime between ? and ? and order_status = ?";
            mSlaveJdbcService.queryAll(callback, sql, CoinWithdrawOrderInfo.class, fromTimeString , toTimeString, txStatus.getKey());
        }
        else
        {
            String sql = "select * from " + TABLE + " where order_createtime between ? and ?";
            mSlaveJdbcService.queryAll(callback, sql, CoinWithdrawOrderInfo.class, fromTimeString , toTimeString);
        }
    }

    @Override
    public RowPager<CoinWithdrawOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status)
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

            if(networkType != null)
            {
                values.add(networkType.getKey());
                whereSQLBuffer.append(" and order_network_type = ? ");
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

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<CoinWithdrawOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), CoinWithdrawOrderInfo.class, values.toArray());
        RowPager<CoinWithdrawOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

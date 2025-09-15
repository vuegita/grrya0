package com.inso.modules.coin.binance_activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class BAOrderDaoMysql extends DaoSupport implements BAOrderDao {

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
    private static final String TABLE = "inso_coin_binance_activity_mining_order";

    @Override
    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         ICurrencyType currency,
                         BAOrderInfo.OrderType orderType,
                         BigDecimal totalAmount, BigDecimal feemoney)
    {
        Date date = new Date();

        feemoney = BigDecimalUtils.getNotNull(feemoney);

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_currency_type", currency.getKey());

        keyvalue.put("order_amount", totalAmount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));

        keyvalue.put("order_type", orderType.getKey());
        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_createtime", date);
        keyvalue.put("order_remark", StringUtils.getEmpty());

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

    public BAOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, BAOrderInfo.class, orderno);
    }

    @Override
    public RowPager<BAOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoCurrency currency, OrderTxStatus status)
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

            if(currency != null)
            {
                values.add(currency.getKey());
                whereSQLBuffer.append(" and order_currency_type = ? ");
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
        List<BAOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), BAOrderInfo.class, values.toArray());
        RowPager<BAOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

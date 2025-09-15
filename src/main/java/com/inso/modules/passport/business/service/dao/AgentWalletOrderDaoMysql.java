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
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class AgentWalletOrderDaoMysql extends DaoSupport implements AgentWalletOrderDao {

    private static final String TABLE = "inso_passport_agent_wallet_order";

    @Override
    public void addOrder(String orderno, BusinessType businessType, String outTradeNo, UserInfo userInfo, ChannelInfo channelInfo, ICurrencyType currencyType,
                         BigDecimal amount, BigDecimal feemoney, OrderTxStatus txStatus, BigDecimal realAmount, RemarkVO remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_out_trade_no", outTradeNo);
        keyvalue.put("order_business_type", businessType.getKey());
        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        keyvalue.put("order_checker", StringUtils.getEmpty());

        if(channelInfo != null)
        {
            keyvalue.put("order_pay_product_type", channelInfo.getProductType());
            keyvalue.put("order_channelname", channelInfo.getName());
            keyvalue.put("order_channelid", channelInfo.getId());
        }

        keyvalue.put("order_currency", currencyType.getKey());
        keyvalue.put("order_amount", BigDecimalUtils.getNotNull(amount));
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));
        keyvalue.put("order_realmoney", BigDecimalUtils.getNotNull(realAmount));

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_createtime", new Date());
        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(txStatus != null)
        {
            setKeyValue.put("order_status", txStatus.getKey());
        }

        if(!StringUtils.isEmpty(checker))
        {
            setKeyValue.put("order_checker", StringUtils.getNotEmpty(checker));
        }

        setKeyValue.put("order_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AgentWalletOrderInfo findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, AgentWalletOrderInfo.class, orderno);
    }

    public AgentWalletOrderInfo findByOutTradeNo(String outTradeNo, BusinessType businessType)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no = ? and order_business_type = ?";
        return mSlaveJdbcService.queryForObject(sql, AgentWalletOrderInfo.class, outTradeNo, businessType.getKey());
    }

    public void queryAll(String startTimeString, String endTimeString, Callback<AgentWalletOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " where order_createtime between ? and ?";
        mSlaveJdbcService.queryAll(callback, sql, AgentWalletOrderInfo.class, startTimeString, endTimeString);
    }

    @Override
    public RowPager<AgentWalletOrderInfo> queryScrollPage(PageVo pageVo, long userid, String systemNo, String outTradeNo, OrderTxStatus txStatus, BusinessType businessType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE);
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

        if(txStatus != null)
        {
            values.add(txStatus.getKey());
            whereSQLBuffer.append(" and order_status = ? ");
        }

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        if(!StringUtils.isEmpty(outTradeNo))
        {
            values.add(outTradeNo);
            whereSQLBuffer.append(" and order_out_trade_no = ? ");
        }

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and order_business_type = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1)  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AgentWalletOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), AgentWalletOrderInfo.class, values.toArray());
        RowPager<AgentWalletOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

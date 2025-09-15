package com.inso.modules.passport.business.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class DayPresentOrderDaoMysql extends DaoSupport implements DayPresentOrderDao {

    private static final String TABLE = "inso_passport_user_day_present_order2";

    @Override
    public void addOrder(String tradeNo, ICurrencyType currencyType, String orderno, UserAttr userAttr, PresentBusinessType businessType, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, RemarkVO remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_out_trade_no", tradeNo);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_business_key", businessType.getKey());


        keyvalue.put("order_fund_key", FundAccountType.Spot.getKey());
        keyvalue.put("order_currency", currencyType.getKey());

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimalUtils.getNotNull(feemoney));

        keyvalue.put("order_createtime", new Date());

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        checker = StringUtils.getNotEmpty(checker);

        if(remark == null || remark.isEmpty())
        {
            String sql = "update " + TABLE + " set order_status = ?, order_checker = ? where order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), checker, orderno);
        }
        else
        {
            String sql = "update " + TABLE + " set order_status = ?, order_checker = ?, order_remark = ? where order_no = ?";
            mWriterJdbcService.executeUpdate(sql, txStatus.getKey(),checker,  remark.toJSONString(), orderno);
        }
    }

    public DayPresentOrder find(String outTradeNo)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no = ?";
        return mSlaveJdbcService.queryForObject(sql, DayPresentOrder.class, outTradeNo);
    }

    public List<DayPresentOrder> queryByOutTradeNo(String prefixOutTradeno)
    {
        String sql = "select * from " + TABLE + " where order_out_trade_no like '" + prefixOutTradeno + "%' ";
        return mSlaveJdbcService.queryForList(sql, DayPresentOrder.class);
    }

    @Override
    public RowPager<DayPresentOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, String systemNo, OrderTxStatus txStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
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

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<DayPresentOrder> list = mSlaveJdbcService.queryForList(select.toString(), DayPresentOrder.class, values.toArray());
        RowPager<DayPresentOrder> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

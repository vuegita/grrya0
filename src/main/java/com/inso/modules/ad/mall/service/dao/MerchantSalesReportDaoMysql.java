package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.SalesReport;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MerchantSalesReportDaoMysql extends DaoSupport implements MerchantSalesReportDao {

    private static String TABLE = "inso_ad_mall_merchant_sales_day";


    /**
     day_pdate                 date NOT NULL ,

     day_userid                int(11) NOT NULL DEFAULT 0,
     day_username              varchar(255) NOT NULL comment '商家',
     day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname             varchar(50) NOT NULL comment '所属代理',
     day_staffid               int(11) NOT NULL DEFAULT 0,
     day_staffname             varchar(50) NOT NULL,

     day_total_amount          decimal(18,2) NOT NULL DEFAULT 0 comment '销售总金额',
     day_total_count           int(11) NOT NULL DEFAULT 0 comment '销售总订单数',

     day_refund_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '退款总额',
     day_refund_count          int(11) NOT NULL DEFAULT 0 comment '退款订单个数',

     day_remark                varchar(1000) NOT NULL DEFAULT '' comment  '',
     */


    public void addReport(UserAttr userAttr, BigDecimal totalAmount, long totalCount, BigDecimal refundAmount, long refundCount, BigDecimal returnAmount, Date pdate)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_userid", userAttr.getUserid());
        keyvalue.put("day_username", userAttr.getUsername());

        keyvalue.put("day_agentid", userAttr.getAgentid());
        keyvalue.put("day_agentname", userAttr.getAgentname());

        keyvalue.put("day_staffid", userAttr.getDirectStaffid());
        keyvalue.put("day_staffname", userAttr.getDirectStaffname());

        keyvalue.put("day_total_amount", totalAmount);
        keyvalue.put("day_total_count", totalCount);

        keyvalue.put("day_return_amount", returnAmount);

        keyvalue.put("day_refund_amount", refundAmount);
        keyvalue.put("day_refund_count", refundCount);

        keyvalue.put("day_pdate", pdate);
        persistent(TABLE, keyvalue);
    }

    public void delete(long userid, Date pdate)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_userid = ? ";
        mWriterJdbcService.executeUpdate(sql, pdate, userid);
    }


    public RowPager<SalesReport> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE);
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and day_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and day_staffid = ? ");
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and day_userid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1)  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        // *, C.money_balance as day_balance 查询实时余额
        select.append("select *  ");

        select.append(whereSQL);
        select.append(" order by day_pdate desc ");

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SalesReport> list = mSlaveJdbcService.queryForList(select.toString(), SalesReport.class, values.toArray());
        RowPager<SalesReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }




}

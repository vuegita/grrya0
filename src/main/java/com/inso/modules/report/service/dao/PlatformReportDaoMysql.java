package com.inso.modules.report.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.PlatformReport;

@Repository
public class PlatformReportDaoMysql extends DaoSupport implements PlatformReportDao {

    private static String TABLE = "inso_report_platform_day";

    /**
     *  day_recharge      		decimal(18,2) NOT NULL DEFAULT 0 comment '充值金额',
     *   day_refund		        decimal(18,2) NOT NULL DEFAULT 0 comment '退款金额-保留字段',
     *   day_withdraw      		decimal(18,2) NOT NULL DEFAULT 0 comment '提现金额',
     *   day_business_recharge    	decimal(18,2) NOT NULL DEFAULT 0 comment '业务充值-如中奖',
     *   day_business_deduct      	decimal(18,2) NOT NULL DEFAULT 0 comment '业务扣款-如投注',
     *   day_platform_recharge     decimal(18,2) NOT NULL DEFAULT 0 comment '平台充值',
     *   day_platform_presentation decimal(18,2) NOT NULL DEFAULT 0 comment '平台赠送',
     *   day_platform_deduct       decimal(18,2) NOT NULL DEFAULT 0 comment '平台扣款',
     *   day_feemoney             	decimal(18,2) NOT NULL DEFAULT 0 comment '手续费-提现才有',
     */

    public void addReport(PlatformReport report)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", report.getPdate());
        keyvalue.put("day_fund_key", report.getFundKey());
        keyvalue.put("day_currency", report.getCurrency());

        keyvalue.put("day_recharge", report.getRecharge());
        keyvalue.put("day_refund", report.getRefund());
        keyvalue.put("day_withdraw", report.getWithdraw());
        keyvalue.put("day_business_recharge", report.getBusinessRecharge());
        keyvalue.put("day_business_deduct", report.getBusinessDeduct());
        keyvalue.put("day_business_feemoney", report.getBusinessFeemoney());

        keyvalue.put("day_platform_recharge", report.getPlatformRecharge());
        keyvalue.put("day_platform_presentation", report.getPlatformPresentation());
        keyvalue.put("day_platform_deduct", report.getPlatformDeduct());
        keyvalue.put("day_feemoney", report.getFeemoney());
        keyvalue.put("day_return_water", report.getReturnWater());

        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, FundAccountType accountType, ICurrencyType currencyType)
    {
        String timeStr = DateUtils.convertString(pdate, DateUtils.TYPE_YYYY_MM_DD);
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_fund_key = ? and day_currency = ? ";
        mWriterJdbcService.executeUpdate(sql, timeStr, accountType.getKey(), currencyType.getKey());
    }

    public void queryAll(Callback<PlatformReport> callback)
    {
        String sql = "select * from " + TABLE;
        mSlaveJdbcService.queryAll(callback, sql, PlatformReport.class);
    }


    public RowPager<PlatformReport> queryScrollPage(PageVo pageVo)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by day_pdate desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<PlatformReport> list = mSlaveJdbcService.queryForList(select.toString(), PlatformReport.class, values.toArray());
        RowPager<PlatformReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

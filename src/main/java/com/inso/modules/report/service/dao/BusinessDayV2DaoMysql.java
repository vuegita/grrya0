package com.inso.modules.report.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.report.model.BusinessV2Report;
import com.inso.modules.report.model.BusinessReportType;
import com.inso.modules.report.model.StatsDimensionType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class BusinessDayV2DaoMysql extends DaoSupport implements BusinessDayV2Dao {

    private static String TABLE = "inso_report_business_v2_day";

    /**
     day_pdate	 				date NOT NULL ,
     day_agentid          		int(11) NOT NULL comment 'userid',
     day_agentname    			varchar(100) NOT NULL comment  '',
     day_staffid	            int(11) NOT NULL DEFAULT 0,
     day_staffname    			varchar(100) NOT NULL comment  '',

     day_business_key	        varchar(255) NOT NULL comment '所属业务key',
     day_business_name	        varchar(255) NOT NULL comment '所属业务名称',
     day_business_externalid    varchar(255) NOT NULL comment '业务拓展ID',

     day_recharge_amount     	decimal(25,8) NOT NULL DEFAULT 0 comment '业务充值',
     day_deduct_amount        	decimal(25,8) NOT NULL DEFAULT 0 comment '业务扣款',
     day_return_amount          decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',
     day_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

     day_remark             	varchar(1000) NOT NULL DEFAULT '' comment '',
     */

    public void addReport(Date pdate, BusinessV2Report report, RemarkVO remarkVO)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", pdate);

        keyvalue.put("day_agentid", report.getAgentid());
        keyvalue.put("day_agentname", StringUtils.getNotEmpty(report.getAgentname()));
        keyvalue.put("day_staffid", report.getStaffid());
        keyvalue.put("day_staffname", StringUtils.getNotEmpty(report.getStaffname()));

        keyvalue.put("day_business_key", report.getBusinessKey());
        keyvalue.put("day_business_name", report.getBusinessName());
        keyvalue.put("day_business_externalid", report.getBusinessExternalid());

        keyvalue.put("day_dimension_type", report.getDimensionType());
        keyvalue.put("day_currency_type", report.getCurrencyType());

        keyvalue.put("day_recharge_amount", report.getRechargeAmount());
        keyvalue.put("day_total_recharge_count", report.getTotalRechargeCount());
        keyvalue.put("day_success_recharge_count", report.getSuccessRechargeCount());

        keyvalue.put("day_deduct_amount", report.getDeductAmount());
        keyvalue.put("day_total_deduct_count", report.getTotalDeductCount());
        keyvalue.put("day_success_deduct_count", report.getSuccessDeductCount());

        keyvalue.put("day_feemoney", report.getFeemoney());

        if(remarkVO != null && !remarkVO.isEmpty())
        {
            keyvalue.put("day_remark", remarkVO.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, long agentid, long staffid, BusinessReportType businessType, String businessExternalid)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_agentid = ? and day_staffid = ? and day_business_key = ? and day_business_externalid = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, agentid, staffid, businessType.getKey(), businessExternalid);
    }

    public RowPager<BusinessV2Report> queryScrollPage(PageVo pageVo, long agentid, long staffid, CryptoCurrency currencyType, StatsDimensionType dimensionType, BusinessReportType businessType, String businessExternalid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

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

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and day_business_key = ? ");
        }

        if(currencyType != null)
        {
            values.add(currencyType.getKey());
            whereSQLBuffer.append(" and day_currency_type = ? ");
        }

        if(!StringUtils.isEmpty(businessExternalid))
        {
            values.add(businessExternalid);
            whereSQLBuffer.append(" and day_business_externalid = ? ");
        }

        if(dimensionType != null)
        {
            values.add(dimensionType.getKey());
            whereSQLBuffer.append(" and day_dimension_type = ? ");
        }


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

        List<BusinessV2Report> list = mSlaveJdbcService.queryForList(select.toString(), BusinessV2Report.class, values.toArray());
        RowPager<BusinessV2Report> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.report.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.report.model.UserStatusV2Day;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.report.model.UserStatusDay;

@Repository
public class UserStatusDayDaoMysql extends DaoSupport implements UserStatusDayDao {

    private static String TABLE = "inso_report_passport_user_status_day";

    /**
     day_pdate	 				date NOT NULL ,
     day_agentid 	            int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_staffid	            int(11) NOT NULL DEFAULT 0,

     day_register_count   		int(11) NOT NULL DEFAULT 0 comment '注册人数',
     day_split_count   		int(11) NOT NULL DEFAULT 0 comment '分裂人数',

     day_total_recharge_count  int(11) NOT NULL DEFAULT 0 comment '充值总次数',
     day_real_recharge_count   int(11) NOT NULL DEFAULT 0 comment '实际成功充值次数',
     day_user_recharge_count   int(11) NOT NULL DEFAULT 0 comment '充值人数',
     day_total_recharge_amount decimal(18,2) NOT NULL DEFAULT 0 comment '充值总额',

     day_total_withdraw_count  int(11) NOT NULL DEFAULT 0 comment '提现总次数',
     day_real_withdraw_count   int(11) NOT NULL DEFAULT 0 comment '实际成功提现次数',
     day_user_withdraw_count   int(11) NOT NULL DEFAULT 0 comment '提现人数',
     day_total_withdraw_amount decimal(18,2) NOT NULL DEFAULT 0 comment '提现总额',
     */

    public void addReport(Date pdate, long agentid, String agentname, long staffid, String staffname, UserStatusDay report)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", pdate);

        keyvalue.put("day_agentid", agentid);
        keyvalue.put("day_agentname", StringUtils.getNotEmpty(agentname));

        keyvalue.put("day_staffid", staffid);
        keyvalue.put("day_staffname", StringUtils.getNotEmpty(staffname));

        keyvalue.put("day_register_count", report.getRegisterCount());
        keyvalue.put("day_split_count", report.getSplitCount());
        keyvalue.put("day_active_count", report.getActiveCount());

        keyvalue.put("day_total_recharge_count", report.getTotalRechargeCount());
        keyvalue.put("day_user_recharge_count", report.getUserRechargeCount());
        keyvalue.put("day_total_recharge_amount", report.getTotalRechargeAmount());

        keyvalue.put("day_total_withdraw_count", report.getTotalWithdrawCount());
        keyvalue.put("day_user_withdraw_count", report.getUserWithdrawCount());
        keyvalue.put("day_total_withdraw_amount", report.getTotalWithdrawAmount());
        keyvalue.put("day_total_withdraw_feemoney", report.getTotalWithdrawFeemoney());

        keyvalue.put("day_first_recharge_count", report.getFirstRechargeCount());
        keyvalue.put("day_first_recharge_amount", BigDecimalUtils.getNotNull(report.getFirstRechargeAmount()));

        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, long agentid, long staffid)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_agentid = ? and day_staffid = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, agentid, staffid);
    }

    public UserStatusDay querySubStatsInfoByAgent(long userid, DateTime dateTime)
    {
        String pdate = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        StringBuilder select = new StringBuilder();
        select.append("select day_staffid ");

        addSumColumn(select, "day_register_count");
        addSumColumn(select, "day_split_count");
        addSumColumn(select, "day_active_count");

        addSumColumn(select, "day_total_recharge_count");
        addSumColumn(select, "day_total_recharge_amount");
        addSumColumn(select, "day_user_recharge_count");

        addSumColumn(select, "day_total_withdraw_count");
        addSumColumn(select, "day_user_withdraw_count");
        addSumColumn(select, "day_total_withdraw_amount");
        addSumColumn(select, "day_total_withdraw_feemoney");

        select.append(" from ").append(TABLE);
        select.append(" where day_pdate >= ? and day_staffid = ? group by day_staffid ");

        return mWriterJdbcService.queryForObject(select.toString(), UserStatusDay.class, pdate, userid);
    }

    public List<UserStatusDay> queryListByAgent(long userid, DateTime dateTime, int limit)
    {
        String pdate = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        StringBuilder select = new StringBuilder();
        select.append("select * ");
        select.append(" from ").append(TABLE);
        select.append(" where day_pdate >= ? and day_staffid = ? limit " ).append(limit);
        return mWriterJdbcService.queryForList(select.toString(), UserStatusDay.class, pdate, userid);
    }

    public RowPager<UserStatusDay> queryScrollPage(PageVo pageVo,long agentid, long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid >= 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and day_agentid = ? ");
        }

        if(staffid >= 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and day_staffid = ? ");
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

        List<UserStatusDay> list = mSlaveJdbcService.queryForList(select.toString(), UserStatusDay.class, values.toArray());
        RowPager<UserStatusDay> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    private void addSumColumn(StringBuilder sql, String column)
    {
        sql.append(", sum(");
        sql.append(column);
        sql.append(") ");
        sql.append(column);

    }

}

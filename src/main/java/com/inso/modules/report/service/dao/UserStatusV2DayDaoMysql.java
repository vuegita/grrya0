package com.inso.modules.report.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusV2Day;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class UserStatusV2DayDaoMysql extends DaoSupport implements UserStatusV2DayDao {

    /**
     log_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     log_userid                int(11) NOT NULL comment 'userid-受益人',
     log_username              varchar(255) NOT NULL comment  '受益人用户名',

     log_agentid               int(11) NOT NULL,
     log_agentname             varchar(255) NOT NULL ,
     log_staffid               int(11) NOT NULL ,
     log_staffname             varchar(255) NOT NULL ,

     log_total_level1_count    int(11) NOT NULL DEFAULT 0 comment '1级人数总数',
     log_total_level2_count    int(11) NOT NULL DEFAULT 0 comment '2级人数总数',

     log_valid_level1_count    int(11) NOT NULL DEFAULT 0 comment '有效1级人数总数',
     log_valid_level2_count    int(11) NOT NULL DEFAULT 0 comment '有效2级人数总数',

     log_trade_amount_number   decimal(25,8) NOT NULL DEFAULT 0 comment 'number',
     log_trade_amount_small    decimal(25,8) NOT NULL DEFAULT 0 comment '小',
     log_trade_amount_big      decimal(25,8) NOT NULL DEFAULT 0 comment '大',
     log_trade_amount_odd      decimal(25,8) NOT NULL DEFAULT 0 comment '单',
     log_trade_amount_even     decimal(25,8) NOT NULL DEFAULT 0 comment '双',

     log_pdate                 date NOT NULL comment '日期',
     *
     *   log_pdate                 date NOT NULL comment '日期',
     */

    public static String TABLE = "inso_passport_report_user_status_v2_day";


    public void addLog(Date date, UserStatusV2Day statusV2Day)
    {
        LinkedHashMap<String, Object> keyValue = Maps.newLinkedHashMap();
        keyValue.put("log_pdate", date);

        keyValue.put("log_userid", statusV2Day.getUserid());
        keyValue.put("log_username", statusV2Day.getUsername());

        keyValue.put("log_agentid", statusV2Day.getAgentid());
        keyValue.put("log_agentname", statusV2Day.getAgentname());

        keyValue.put("log_staffid", statusV2Day.getStaffid());
        keyValue.put("log_staffname", statusV2Day.getStaffname());

        keyValue.put("log_total_lv1_active_count", statusV2Day.getTotalLv1ActiveCount());
        keyValue.put("log_total_lv1_member_balance", statusV2Day.getTotalLv1MemberBalance());

        keyValue.put("log_total_lv1_recharge_count", statusV2Day.getTotalLv1RechargeCount());
        keyValue.put("log_total_lv1_recharge_amount", statusV2Day.getTotalLv1RechargeAmount());

        keyValue.put("log_total_lv1_withdraw_count", statusV2Day.getTotalLv1WithdrawCount());
        keyValue.put("log_total_lv1_withdraw_amount", statusV2Day.getTotalLv1WithdrawAmount());
        keyValue.put("log_total_lv1_withdraw_feemoney", statusV2Day.getTotalLv1WithdrawFeemoney());

        keyValue.put("log_total_lv1_count", statusV2Day.getTotalLv1Count());
        keyValue.put("log_total_lv2_count", statusV2Day.getTotalLv2Count());

        keyValue.put("log_return_lv1_amount", statusV2Day.getReturnLv1Amount());
        keyValue.put("log_return_lv2_amount", statusV2Day.getReturnLv2Amount());

        keyValue.put("log_return_first_recharge_lv1_amount", statusV2Day.getReturnFirstRechargeLv1Amount());
        keyValue.put("log_return_first_recharge_lv2_amount", statusV2Day.getReturnFirstRechargeLv2Amount());

        keyValue.put("log_valid_lv1_count", statusV2Day.getValidLv1Count());
        keyValue.put("log_valid_lv2_count", statusV2Day.getValidLv2Count());

        keyValue.put("log_trade_lv1_volumn", BigDecimalUtils.getNotNull(statusV2Day.getTradeLv1Volumn()));
        keyValue.put("log_trade_lv2_volumn", BigDecimalUtils.getNotNull(statusV2Day.getTradeLv2Volumn()));

        keyValue.put("log_trade_amount_number", BigDecimalUtils.getNotNull(statusV2Day.getTradeAmountNumber()));
        keyValue.put("log_trade_amount_small", BigDecimalUtils.getNotNull(statusV2Day.getTradeAmountSmall()));
        keyValue.put("log_trade_amount_big", BigDecimalUtils.getNotNull(statusV2Day.getTradeAmountBig()));
        keyValue.put("log_trade_amount_odd", BigDecimalUtils.getNotNull(statusV2Day.getTradeAmountOdd()));
        keyValue.put("log_trade_amount_even", BigDecimalUtils.getNotNull(statusV2Day.getTradeAmountEven()));

        persistent(TABLE, keyValue);
    }

    public void delete(Date date, UserStatusV2Day statusV2Day)
    {
        String sql = "delete from " + TABLE + " where log_pdate = ? and log_userid = ?";
        mWriterJdbcService.executeUpdate(sql, date, statusV2Day.getUserid());
    }

    public UserStatusV2Day findByUserid(DateTime date, long userid)
    {
        String dateStr = date.toString(DateUtils.TYPE_YYYY_MM_DD);
        String sql = "select * from " + TABLE + " where log_pdate = ? and log_userid = ?";
        return mWriterJdbcService.queryForObject(sql, UserStatusV2Day.class, dateStr, userid);
    }

    public UserStatusV2Day queryByUser(DateTime fromTime, long userid)
    {
        String pdate = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        StringBuilder select = new StringBuilder();
        select.append("select log_userid ");

        addSumColumn(select, "log_total_lv1_active_count");
        addSumColumn(select, "log_total_lv1_member_balance");

        addSumColumn(select, "log_total_lv1_recharge_count");
        addSumColumn(select, "log_total_lv1_recharge_amount");

        addSumColumn(select, "log_total_lv1_withdraw_count");
        addSumColumn(select, "log_total_lv1_withdraw_amount");
        addSumColumn(select, "log_total_lv1_withdraw_feemoney");

        addSumColumn(select, "log_total_lv1_count");
        addSumColumn(select, "log_total_lv2_count");

        addSumColumn(select, "log_valid_lv1_count");
        addSumColumn(select, "log_valid_lv2_count");

        addSumColumn(select, "log_return_lv1_amount");
        addSumColumn(select, "log_return_lv2_amount");

        addSumColumn(select, "log_return_first_recharge_lv1_amount");
        addSumColumn(select, "log_return_first_recharge_lv2_amount");

        addSumColumn(select, "log_trade_lv1_volumn");
        addSumColumn(select, "log_trade_lv2_volumn");

        addSumColumn(select, "log_trade_amount_number");
        addSumColumn(select, "log_trade_amount_small");
        addSumColumn(select, "log_trade_amount_big");
        addSumColumn(select, "log_trade_amount_odd");
        addSumColumn(select, "log_trade_amount_even");

        select.append(" from ").append(TABLE);
        select.append(" where log_pdate >= ? and log_userid = ? group by log_userid ");

        return mWriterJdbcService.queryForObject(select.toString(), UserStatusV2Day.class, pdate, userid);
    }

    public List<UserStatusV2Day> queryListByUser(DateTime fromTime, long userid)
    {
        String pdate = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        StringBuilder select = new StringBuilder();
        select.append("select * ");
        select.append(" from ").append(TABLE);
        select.append(" where log_pdate >= ? and log_userid = ? order by log_pdate desc ");

        return mWriterJdbcService.queryForList(select.toString(), UserStatusV2Day.class, pdate, userid);
    }

    private void addSumColumn(StringBuilder sql, String column)
    {
        sql.append(", sum(");
        sql.append(column);
        sql.append(") ");
        sql.append(column);

    }

    @Override
    public RowPager<UserStatusV2Day> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from " + TABLE + " as A");
        whereSQLBuffer.append(" where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and log_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and log_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and log_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and log_staffid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by log_userid desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserStatusV2Day> list = mSlaveJdbcService.queryForList(select.toString(), UserStatusV2Day.class, values.toArray());
        RowPager<UserStatusV2Day> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

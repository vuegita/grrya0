package com.inso.modules.report.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.report.model.GameBusinessDay;

@Repository
public class GameBusinessDayDaoMysql extends DaoSupport implements GameBusinessDayDao {

    private static String TABLE = "inso_report_game_business_day";

    /**
     day_pdate	 				    date NOT NULL ,
     day_agentid 	                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname 	            varchar(50) NOT NULL comment '所属代理',
     day_staffid	                int(11) NOT NULL DEFAULT 0,
     day_staffname 	            varchar(50) NOT NULL,

     day_business_code             int(11) NOT NULL comment '业务唯一编码',
     day_business_name             varchar(50) NOT NULL comment  '业务名称',

     day_bet_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_bet_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_win_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_win_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_feemoney             	    decimal(18,2) DEFAULT 0 NOT NULL comment '业务手续费',
     */

    public void addReport(Date pdate, long agentid, String agentname, long staffid, String staffname, BusinessType businessType)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", pdate);

        keyvalue.put("day_agentid", agentid);
        keyvalue.put("day_agentname", StringUtils.getNotEmpty(agentname));

        keyvalue.put("day_staffid", staffid);
        keyvalue.put("day_staffname", StringUtils.getNotEmpty(staffname));

        keyvalue.put("day_business_code", businessType.getCode());
        keyvalue.put("day_business_name", businessType.getKey());

        persistent(TABLE, keyvalue);
    }

    public void updateReport(Date pdate, long agentid, long staffid, BusinessType businessType, BigDecimal betAmount, long betCount, BigDecimal feemoney, BigDecimal winAmount, long winCount)
    {
        StringBuilder sqlBuider = new StringBuilder("update " + TABLE + " set ");

        if(betAmount == null || betCount < 0)
        {
            betAmount = BigDecimal.ZERO;
            betCount = 0;

        }

        if(winAmount == null || winCount == 0)
        {
            winAmount = BigDecimal.ZERO;
            winCount = 0;
        }
        if(feemoney == null){
            feemoney = BigDecimal.ZERO;
        }

        sqlBuider.append(" day_bet_amount = day_bet_amount + ?, day_bet_count = day_bet_count + ?, day_feemoney = day_feemoney + ?, ");
        sqlBuider.append(" day_win_amount = day_win_amount + ?, day_win_count = day_win_count + ? ");

        sqlBuider.append(" where day_pdate = ? and day_agentid = ? and day_staffid = ? and day_business_code = ?");
        mWriterJdbcService.executeUpdate(sqlBuider.toString(), betAmount, betCount, feemoney, winAmount, winCount, pdate, agentid, staffid, businessType.getCode());
    }

    public void delete(Date pdate, long agentid, long staffid, BusinessType businessType)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_agentid = ? and day_staffid = ? and day_business_code = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, agentid, staffid, businessType.getCode());
    }

    public void queryAllStaff(String begintTime, String endTime, Callback<GameBusinessDay> callback)
    {
        String sql = "select * from " + TABLE + " where day_pdate between ? and ? and day_agentid != 0";
        mSlaveJdbcService.queryAll(callback, sql, GameBusinessDay.class, begintTime, endTime);
    }

    public RowPager<GameBusinessDay> queryScrollPage(PageVo pageVo,long agentid, long staffid, BusinessType businessType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        values.add(agentid);
        whereSQLBuffer.append(" and day_agentid = ? ");

        values.add(staffid);
        whereSQLBuffer.append(" and day_staffid = ? ");

        values.add(businessType.getCode());
        whereSQLBuffer.append(" and day_business_code = ? ");

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

        List<GameBusinessDay> list = mSlaveJdbcService.queryForList(select.toString(), GameBusinessDay.class, values.toArray());
        RowPager<GameBusinessDay> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

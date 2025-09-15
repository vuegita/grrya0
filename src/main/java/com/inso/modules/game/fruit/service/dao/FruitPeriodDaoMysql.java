package com.inso.modules.game.fruit.service.dao;

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
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;

@Repository
public class FruitPeriodDaoMysql extends DaoSupport implements FruitPeriodDao {

    private static final String TABLE = "inso_game_fruit_period";

    /**
     period_issue                  varchar(50) NOT NULL comment '期号',
     period_type            		varchar(20) NOT NULL comment '',
     period_gameid            		int(11) NOT NULL comment 'game 唯一id',
     period_total_bet_amount   	decimal(18,2) NOT NULL comment '投注总额',
     period_total_win_amount		decimal(18,2) NOT NULL comment '中奖总额',
     period_total_feemoney   		decimal(18,2) NOT NULL comment '手续费',
     period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
     period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',
     period_status     			varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
     period_open_mode      		varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
     period_open_result    		varchar(10) NOT NULL DEFAULT '' comment  '开奖结果',
     period_starttime       		datetime NOT NULL comment '开盘时间',
     period_endtime           		datetime NOT NULL comment '封盘时间',
     period_createtime       		datetime NOT NULL,
     period_updatetime      		datetime,
     period_remark             	varchar(3000) DEFAULT '',
     */

    public void add(FruitType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime)
    {

       // long openCardNum = ABCardHelper.randomPocker().getCode();

        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("period_issue", issue);
        keyvalue.put("period_gameid", gameid);

        keyvalue.put("period_type", type.getKey());

        keyvalue.put("period_total_bet_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_win_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_bet_count", BigDecimal.ZERO);
        keyvalue.put("period_total_win_count", BigDecimal.ZERO);
        keyvalue.put("period_total_feemoney", BigDecimal.ZERO);

        keyvalue.put("period_open_mode", mode.getKey());

        keyvalue.put("period_starttime", startTime);
        keyvalue.put("period_endtime", endTime);
        keyvalue.put("period_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("period_total_bet_amount", betAmount);
        setKeyValue.put("period_total_win_amount", winAmount);
        setKeyValue.put("period_total_feemoney", feeAmount);
        setKeyValue.put("period_total_bet_count", betCount);
        setKeyValue.put("period_total_win_count", winCount);

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("period_issue", issue);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateStatus(String issue, GamePeriodStatus status)
    {
        Date updateTime = new Date();
        String sql = "update " + TABLE + " set period_status= ?, period_updatetime = ? where period_issue = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), updateTime, issue);
    }

    public void updateOpenResult(String issue, FruitBetItemType openResult, GameOpenMode mode)
    {
        if(mode == null)
        {
            String sql = "update " + TABLE + " set period_open_result = ? where period_issue = ?";
            mWriterJdbcService.executeUpdate(sql, openResult.getKey(), issue);
        }
        else
        {
            String sql = "update " + TABLE + " set period_open_result = ?, period_open_mode = ? where period_issue = ?";
            mWriterJdbcService.executeUpdate(sql, openResult.getKey(), mode.getKey(), issue);
        }

    }

    public void updateOpenMode(String issue, GameOpenMode mode)
    {
        String sql = "update " + TABLE + " set period_open_mode = ? where period_issue = ?";
        mWriterJdbcService.executeUpdate(sql, mode.getKey(), issue);
    }

    public FruitPeriodInfo findByTime(String time, FruitType type)
    {
        String sql = "select * from " + TABLE + " where period_starttime <= ? and period_type = ? order by period_starttime desc limit 1";
        return mSlaveJdbcService.queryForObject(sql, FruitPeriodInfo.class, time, type.getKey());
    }

    public FruitPeriodInfo findByIssue(String issue)
    {
        String sql = "select * from " + TABLE + " where period_issue = ?";
        return mSlaveJdbcService.queryForObject(sql, FruitPeriodInfo.class, issue);
    }

    public List<FruitPeriodInfo> queryByTime(FruitType type, String beginTime, String endTime, int limit)
    {
        String sql = "select * from " + TABLE + " where period_starttime between ? and ? and period_type = ? order by period_starttime desc ";
        if(limit > 0)
        {
            sql += " limit " + limit;
        }
        return mSlaveJdbcService.queryForList(sql, FruitPeriodInfo.class, beginTime, endTime, type.getKey());
    }

    public long count(FruitType type, String startTimeString, String endTimeString)
    {
        String sql = "select count(1) from " + TABLE + " where period_starttime between ? and ? and period_type = ?";
        return mSlaveJdbcService.count(sql, startTimeString, endTimeString, type.getKey());
    }

    public void queryAll(FruitType type, String startTimeString, String endTimeString, Callback<FruitPeriodInfo> callback)
    {
        if(type == null)
        {
            String sql = "select * from " + TABLE + " where period_starttime between ? and ?";
            mSlaveJdbcService.queryAll(callback, sql, FruitPeriodInfo.class, startTimeString, endTimeString);
        }

        else
        {
            String sql = "select * from " + TABLE + " where period_starttime between ? and ? and period_type = ?";
            mSlaveJdbcService.queryAll(callback, sql, FruitPeriodInfo.class, startTimeString, endTimeString, type.getKey());
        }

    }

    public RowPager<FruitPeriodInfo> queryScrollPage(PageVo pageVo, String issue, FruitType type, GamePeriodStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(issue))
        {
            values.add(issue);
            whereSQLBuffer.append(" and period_issue = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and period_starttime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(type != null)
            {
                values.add(type.getKey());
                whereSQLBuffer.append(" and period_type = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and period_status = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_fruit_period " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_game_fruit_period ");
        select.append(whereSQL);
        select.append(" order by period_starttime asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<FruitPeriodInfo> list = mSlaveJdbcService.queryForList(select.toString(), FruitPeriodInfo.class, values.toArray());
        RowPager<FruitPeriodInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }
}

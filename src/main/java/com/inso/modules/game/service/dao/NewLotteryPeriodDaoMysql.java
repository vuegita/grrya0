package com.inso.modules.game.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class NewLotteryPeriodDaoMysql extends DaoSupport implements NewLotteryPeriodDao {

//    private static final String TABLE = "inso_game_lottery_period";

    /**
     *   period_issue                  varchar(50) NOT NULL comment '期号',
     *   period_gameid            		int(11) NOT NULL comment 'game 唯一id',
     *   period_total_bet_amount   	decimal(18,2) NOT NULL comment '投注总额',
     *   period_total_win_amount		decimal(18,2) NOT NULL comment '中奖总额',
     *   period_total_feemoney   		decimal(18,2) NOT NULL comment '手续费',
     *   period_total_bet_number       int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
     *   period_total_win_number       int(11) NOT NULL DEFAULT 0 comment 'total win order number',
     *   period_status     			varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
     *   period_open_mode      		varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
     *   period_open_result    		int(11) NOT NULL DEFAULT -1 comment  '开奖数字',
     *   period_starttime       		datetime NOT NULL comment '开盘时间',
     *   period_endtime           		datetime NOT NULL comment '封盘时间',
     *   period_createtime       		datetime NOT NULL,
     *   period_updatetime      		datetime,
     *   period_remark             	varchar(3000) DEFAULT '',
     */

    public void add(String showIssue, GameChildType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime)
    {

        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("period_issue", issue);
        keyvalue.put("period_show_issue", StringUtils.getNotEmpty(showIssue));

        keyvalue.put("period_gameid", gameid);

        keyvalue.put("period_type", type.getKey());

        keyvalue.put("period_total_bet_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_win_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_bet_count", BigDecimal.ZERO);
        keyvalue.put("period_total_win_count", BigDecimal.ZERO);
        keyvalue.put("period_total_feemoney", BigDecimal.ZERO);

//        keyvalue.put("period_status", "pending");
        keyvalue.put("period_open_mode", mode.getKey());
        keyvalue.put("period_starttime", startTime);
        keyvalue.put("period_endtime", endTime);
        keyvalue.put("period_createtime", date);

        persistent(type.getCategory().getPeriodTable(), keyvalue);
    }

    public void updateAmount(GameChildType gameType, String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount, BigDecimal winAmount2)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("period_total_bet_amount", betAmount);
        setKeyValue.put("period_total_win_amount", winAmount);
        setKeyValue.put("period_total_win_amount2", BigDecimalUtils.getNotNull(winAmount2));
        setKeyValue.put("period_total_feemoney", feeAmount);
        setKeyValue.put("period_total_bet_count", betCount);
        setKeyValue.put("period_total_win_count", winCount);

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("period_issue", issue);

        update(gameType.getCategory().getPeriodTable(), setKeyValue, whereKeyValue);
    }

    public void updateStatus(GameChildType gameType, String issue, GamePeriodStatus status)
    {
        Date updateTime = new Date();
        String sql = "update " + gameType.getCategory().getPeriodTable() + " set period_status= ?, period_updatetime = ? where period_issue = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), updateTime, issue);
    }

    public void updateOpenResult(GameChildType gameType, String issue, String referencePrice, String openResult, GameOpenMode mode, JSONObject jsonObject)
    {
        LinkedHashMap set = Maps.newLinkedHashMap();

        if(mode != null)
        {
            set.put("period_open_mode", mode.getKey());
        }

        if(!StringUtils.isEmpty(openResult))
        {
            set.put("period_open_result", openResult);
        }

        if(!StringUtils.isEmpty(referencePrice))
        {
            set.put("period_reference_external", referencePrice);
        }

        if(jsonObject != null)
        {
            set.put("period_remark", jsonObject.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("period_issue", issue);

        update(gameType.getCategory().getPeriodTable(), set, where);
    }

    public void updateOpenMode(GameChildType gameType, String issue, GameOpenMode mode)
    {
        String sql = "update " + gameType.getCategory().getPeriodTable() + " set period_open_mode = ? where period_issue = ?";
        mWriterJdbcService.executeUpdate(sql, mode.getKey(), issue);
    }

    public NewLotteryPeriodInfo findByTime(String time, GameChildType gameType)
    {
        String sql = "select * from " + gameType.getCategory().getPeriodTable() + " where period_starttime <= ? and period_type = ? order by period_starttime desc limit 1";
        return mSlaveJdbcService.queryForObject(sql, NewLotteryPeriodInfo.class, time, gameType.getKey());
    }

    public NewLotteryPeriodInfo findByIssue(GameChildType gameType, String issue)
    {
        String sql = "select * from " + gameType.getCategory().getPeriodTable() + " where period_issue = ?";
        return mSlaveJdbcService.queryForObject(sql, NewLotteryPeriodInfo.class, issue);
    }

    public List<NewLotteryPeriodInfo> queryByTime(GameChildType gameType, String beginTime, String endTime, int limit)
    {
        String sql = "select * from " + gameType.getCategory().getPeriodTable() + " where period_starttime between ? and ? and period_type = ? order by period_starttime desc ";
        if(limit > 0)
        {
            sql += " limit " + limit;
        }
        return mSlaveJdbcService.queryForList(sql, NewLotteryPeriodInfo.class, beginTime, endTime, gameType.getKey());
    }

    public long count(GameChildType type, String startTimeString, String endTimeString)
    {
        String sql = "select count(1) from " + type.getCategory().getPeriodTable() + " where period_starttime between ? and ? and period_type = ?";
        return mSlaveJdbcService.count(sql, startTimeString, endTimeString, type.getKey());
    }

    public void queryAll(GameChildType dbType, String startTimeString, String endTimeString, GameChildType whereType, Callback<NewLotteryPeriodInfo> callback)
    {
        if(whereType != null)
        {
            String sql = "select * from " + dbType.getCategory().getPeriodTable() + " where period_starttime between ? and ? and period_type = ?";
            mSlaveJdbcService.queryAll(true, callback, sql, NewLotteryPeriodInfo.class, startTimeString, endTimeString, whereType.getKey());
        }
        else
        {
            String sql = "select * from " + dbType.getCategory().getPeriodTable() + " where period_starttime between ? and ?";
            mSlaveJdbcService.queryAll(true, callback, sql, NewLotteryPeriodInfo.class, startTimeString, endTimeString);
        }

//        if(type == null)
//        {
//            String sql = "select * from " + type.getCategory().getPeriodTable() + " where period_starttime between ? and ?";
//            mSlaveJdbcService.queryAll(callback, sql, NewLotteryPeriodInfo.class, startTimeString, endTimeString);
//        }
//
//        else
//        {
//            String sql = "select * from " + type.getCategory().getPeriodTable() + " where period_starttime between ? and ? and period_type = ?";
//            mSlaveJdbcService.queryAll(callback, sql, NewLotteryPeriodInfo.class, startTimeString, endTimeString, type.getKey());
//        }

    }

    public RowPager<NewLotteryPeriodInfo> queryScrollPage(PageVo pageVo, String issue, GameChildType gameType, GamePeriodStatus status)
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

            if(gameType != null)
            {
                values.add(gameType.getKey());
                whereSQLBuffer.append(" and period_type = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and period_status = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + gameType.getCategory().getPeriodTable() + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from ").append(gameType.getCategory().getPeriodTable());
        select.append(whereSQL);
        select.append(" order by period_starttime asc ");  //desc
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<NewLotteryPeriodInfo> list = mSlaveJdbcService.queryForList(select.toString(), NewLotteryPeriodInfo.class, values.toArray());
        RowPager<NewLotteryPeriodInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }
}

package com.inso.modules.game.red_package.service.dao;

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
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;

@Repository
public class RedPPeriodDaoMysql extends DaoSupport implements RedPPeriodDao {

    private static final String TABLE = "inso_game_red_package_period";

    /**
     period_userid            int(11) NOT NULL,
     period_username 		   varchar(50) NOT NULL ,

     period_total_bet_amount  decimal(18,2) NOT NULL DEFAULT 0 comment '投注总额',
     period_total_win_amount  decimal(18,2) NOT NULL DEFAULT 0 comment '中奖总额',
     period_total_feemoney    decimal(18,2) NOT NULL DEFAULT 0 comment '手续费',
     period_total_bet_count   int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
     period_total_win_count   int(11) NOT NULL DEFAULT 0 comment 'total win order number',
     period_open_result       varchar(50) DEFAULT '' comment '开奖结果',
     period_open_mode         varchar(50) DEFAULT '' comment '开奖模式',

     period_total_amount      decimal(18,2) NOT NULL comment '红包总金额',
     period_min_amount        decimal(18,2) NOT NULL comment '红包最小金额-最少值为1',
     period_max_amount        decimal(18,2) NOT NULL comment '红包最大金额',
     period_total_count       int(11) NOT NULL DEFAULT 0 comment '红包总个数',
     period_complete_count    int(11) NOT NULL DEFAULT 0 comment '红包已领取个数',
     period_complete_amount   decimal(18,2) NOT NULL DEFAULT 0 comment '红包已领取总金额',
     period_rp_type              varchar(20) NOT NULL comment '红包类型',
     period_status            varchar(20) NOT NULL,

     period_createtime        datetime DEFAULT NULL ,
     period_endtime           datetime DEFAULT NULL ,
     period_remark            varchar(1000) NOT NULL,
     */

    public long add(RedPCreatorType creatorType, RedPType type, String orderno, long userid, String username, long agentId,
                    BigDecimal totalAmount, long totalCount, BigDecimal minAmount, BigDecimal maxAmount,
                    Date startTime, Date endTime, RemarkVO remark)
    {

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("period_orderno", orderno);

        keyvalue.put("period_userid", userid);
        keyvalue.put("period_username", StringUtils.getNotEmpty(username));

        keyvalue.put("period_agentid", agentId);

        keyvalue.put("period_total_amount", totalAmount);
        keyvalue.put("period_min_amount", minAmount);
        keyvalue.put("period_max_amount", maxAmount);
        keyvalue.put("period_total_count", totalCount);

        keyvalue.put("period_rp_type", type.getKey());
        keyvalue.put("period_creator_type", creatorType.getKey());

        keyvalue.put("period_total_bet_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_win_amount", BigDecimal.ZERO);
        keyvalue.put("period_total_feemoney", BigDecimal.ZERO);
        keyvalue.put("period_total_bet_count", BigDecimal.ZERO);
        keyvalue.put("period_total_win_count", BigDecimal.ZERO);

        keyvalue.put("period_status", GamePeriodStatus.WAITING.getKey());

        keyvalue.put("period_createtime", startTime);
        keyvalue.put("period_endtime", endTime);

        if(remark != null)
        {
            keyvalue.put("period_remark", remark.toJSONString());
        }
        else
        {
            keyvalue.put("period_remark", StringUtils.getEmpty());
        }

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateAmount(long id, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(betAmount != null)
        {
            setKeyValue.put("period_total_bet_amount", betAmount);
            setKeyValue.put("period_total_bet_count", betCount);
        }

        if(winAmount != null)
        {
            setKeyValue.put("period_total_win_amount", winAmount);
            setKeyValue.put("period_total_win_count", winCount);
        }

        setKeyValue.put("period_total_feemoney", feeAmount);

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("period_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateStatus(long id, GamePeriodStatus status)
    {
//        Date updateTime = new Date();
        String sql = "update " + TABLE + " set period_status= ? where period_id = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), id);
    }

    public void updateOpenResult(long id, long openResult, GameOpenMode mode)
    {
        if(mode == null)
        {
            String sql = "update " + TABLE + " set period_open_result = ? where period_id = ?";
            mWriterJdbcService.executeUpdate(sql, openResult, id);
        }
        else
        {
            String sql = "update " + TABLE + " set period_open_result = ?, period_open_mode = ? where period_id = ?";
            mWriterJdbcService.executeUpdate(sql, openResult, mode.getKey(), id);
        }
    }

    public RedPPeriodInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where period_id = ?";
        return mSlaveJdbcService.queryForObject(sql, RedPPeriodInfo.class, id);
    }

    public List<RedPPeriodInfo> queryByTime(RedPType type, String beginTime, String endTime, int limit)
    {
        String sql = "select * from " + TABLE + " where period_createtime between ? and ? and period_rp_type = ? order by period_createtime desc ";
        if(limit > 0)
        {
            sql += " limit " + limit;
        }
        return mSlaveJdbcService.queryForList(sql, RedPPeriodInfo.class, beginTime, endTime, type.getKey());
    }

    public void queryAll(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback)
    {
        if(type == null)
        {
            String sql = "select * from " + TABLE + " where period_createtime between ? and ?";
            mSlaveJdbcService.queryAll(callback, sql, RedPPeriodInfo.class, startTimeString, endTimeString);
        }

        else
        {
            String sql = "select * from " + TABLE + " where period_createtime between ? and ? and period_rp_type = ?";
            mSlaveJdbcService.queryAll(callback, sql, RedPPeriodInfo.class, startTimeString, endTimeString, type.getKey());
        }
    }

    public void queryAllByUpdateTime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback)
    {
        if(type == null)
        {
            String sql = "select * from " + TABLE + " where period_endtime between ? and ?";
            mSlaveJdbcService.queryAll(callback, sql, RedPPeriodInfo.class, startTimeString, endTimeString);
        }

        else
        {
            String sql = "select * from " + TABLE + " where period_endtime between ? and ? and period_rp_type = ?";
            mSlaveJdbcService.queryAll(callback, sql, RedPPeriodInfo.class, startTimeString, endTimeString, type.getKey());
        }
    }

    public RowPager<RedPPeriodInfo> queryScrollPage(PageVo pageVo, long id, long userid, RedPType type, GamePeriodStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append(" and period_id = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and period_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            // 0 表示系统
            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and period_userid = ? ");
            }

            if(type != null)
            {
                values.add(type.getKey());
                whereSQLBuffer.append(" and period_rp_type = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and period_status = ? ");
            }

        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_red_package_period " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_game_red_package_period ");
        select.append(whereSQL);
        select.append(" order by period_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RedPPeriodInfo> list = mSlaveJdbcService.queryForList(select.toString(), RedPPeriodInfo.class, values.toArray());
        RowPager<RedPPeriodInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}


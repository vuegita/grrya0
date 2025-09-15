package com.inso.modules.game.red_package.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.red_package.model.RedPBetOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class RedPBetOrderDaoMysql extends DaoSupport implements RedPBetOrderDao {

    private static final String TABLE = "inso_game_red_package_bet_order";

    public void addOrder(String orderno, long rpid, RedPType lotteryType, UserInfo userInfo, long agentid, String betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal totalBetAmount, BigDecimal feemoney, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_rpid", rpid);
        keyvalue.put("order_rp_type", lotteryType.getKey());

        keyvalue.put("order_bet_item", betItem);

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());
        keyvalue.put("order_agentid", agentid);

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_basic_amount", basicAmount);
        keyvalue.put("order_bet_count", betCount);
        keyvalue.put("order_bet_amount", totalBetAmount);

        if(feemoney != null)
        {
            keyvalue.put("order_feemoney", feemoney);
        }

        keyvalue.put("order_createtime", new Date());
        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, BigDecimal feemoney, JSONObject remark)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(openResult >= 0)
        {
            setKeyValue.put("order_open_result", openResult);
        }
        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }
        if(winmoney != null)
        {
            setKeyValue.put("order_win_amount", winmoney);
        }
        if(feemoney != null)
        {
            setKeyValue.put("order_feemoney", feemoney);
        }
        setKeyValue.put("order_status", txStatus.getKey());
        setKeyValue.put("order_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public RedPBetOrderInfo findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, RedPBetOrderInfo.class, orderno);
    }

    public List<RedPBetOrderInfo> queryListByUserid(RedPType type, String createtime, long userid, int limit)
    {
        String sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? and order_rp_type = ? order by order_createtime desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, RedPBetOrderInfo.class, createtime, userid, type.getKey());
    }

    public void queryAllByIssue(long rpid, Callback<RedPBetOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " where order_rpid = ?";
        mSlaveJdbcService.queryAll(callback, sql, RedPBetOrderInfo.class, rpid);
    }

    public RowPager<RedPBetOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long rpid, OrderTxStatus txStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(rpid > 0)
        {
            values.add(rpid);
            whereSQLBuffer.append(" and order_rpid = ? ");
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

            if(lotteryType != null)
            {
                values.add(lotteryType.getKey());
                whereSQLBuffer.append(" and order_type = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_red_package_bet_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_game_red_package_bet_order ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RedPBetOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), RedPBetOrderInfo.class, values.toArray());
        RowPager<RedPBetOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPBetOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " as A inner join inso_passport_user as B on A.order_userid=user_id and B.user_type = 'member' where order_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, RedPBetOrderInfo.class, startTimeString, endTimeString);
    }
}

package com.inso.modules.game.rg.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.modules.report.model.GameBusinessDay;
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
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class LotteryOrderDaoMysql extends DaoSupport implements LotteryOrderDao {

    private static final String TABLE = "inso_game_lottery_order";

    public void addOrder(String orderno, String issue, LotteryRGType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal totalBetAmount, BigDecimal feemoney, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_issue", issue);
        keyvalue.put("order_type", lotteryType.getKey());
        keyvalue.put("order_bet_item", betItem);

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

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
        if(openResult != -1)
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

    public LotteryOrderInfo findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, LotteryOrderInfo.class, orderno);
    }

    public List<LotteryOrderInfo> queryListByUserid(String createtime, long userid, LotteryRGType rgType, int limit)
    {
        String sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? and (order_status = 'realized' or order_status = 'failed') and order_type = ? order by order_createtime desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, LotteryOrderInfo.class, createtime, userid, rgType.getKey());
    }

    public void queryAllByIssue(String issue, Callback<LotteryOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " where order_issue = ?";
        mSlaveJdbcService.queryAll(callback, sql, LotteryOrderInfo.class, issue);
    }

    public RowPager<LotteryOrderInfo> queryScrollPage(PageVo pageVo, LotteryRGType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.order_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(systemNo))
        {
            values.add(systemNo);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(!StringUtils.isEmpty(issue))
        {
            values.add(issue);
            whereSQLBuffer.append(" and order_issue = ? ");

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

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and order_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and order_staffid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_lottery_order as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*, B.attr_direct_staffname as order_staffname ,C.money_balance as order_balance,C.money_total_recharge as order_total_recharge,(C.money_total_withdraw-C.money_total_refund) as order_total_withdraw  from inso_game_lottery_order as A");
        select.append(" left join inso_passport_user_attr as B on B.attr_userid=A.order_userid ");
        select.append(whereSQL);

        if(sortName!=null && sortOrder!=null){

            if(sortName.equals("betAmount")){
                select.append(" order by "+" order_bet_amount" +" "+sortOrder);
            }

        }else{
            select.append(" order by order_createtime desc ");
        }
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<LotteryOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), LotteryOrderInfo.class, values.toArray());
        RowPager<LotteryOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAllMember(String startTimeString, String endTimeString, Callback<LotteryOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " as A inner join inso_passport_user as B on A.order_userid=user_id and B.user_type = 'member' where order_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, LotteryOrderInfo.class, startTimeString, endTimeString);
    }

    public void queryAllMemberByTime(String startTimeString, String endTimeString, Callback<GameBusinessDay> callback)
    {
        String sql = "select * from " + TABLE + " as A inner join inso_passport_user as B on A.order_userid=user_id and B.user_type = 'member' where order_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, GameBusinessDay.class, startTimeString, endTimeString);
    }
}

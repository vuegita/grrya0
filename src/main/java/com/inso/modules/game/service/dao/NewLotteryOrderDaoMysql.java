package com.inso.modules.game.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class NewLotteryOrderDaoMysql extends DaoSupport implements NewLotteryOrderDao {


    public void addOrder(String orderno, String issue, GameChildType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, OrderTxStatus txStatus,
                         BigDecimal totalBetAmount, int totalBetCount, BigDecimal singleBetAmount, BigDecimal feemoney, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_issue", StringUtils.getNotEmpty(issue));
        keyvalue.put("order_lottery_type", lotteryType.getKey());
        keyvalue.put("order_bet_item", betItem);

        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());
        keyvalue.put("order_usertype", userInfo.getType());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", StringUtils.getNotEmpty(userAttr.getAgentname()));
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", StringUtils.getNotEmpty(userAttr.getDirectStaffname()));

        keyvalue.put("order_status", txStatus.getKey());


        keyvalue.put("order_single_bet_amount", singleBetAmount);
        keyvalue.put("order_total_bet_count", totalBetCount);
        keyvalue.put("order_total_bet_amount", totalBetAmount);

        if(feemoney != null)
        {
            keyvalue.put("order_feemoney", feemoney);
        }

        keyvalue.put("order_createtime", new Date());
        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }

        persistent(lotteryType.getCategory().getOrderTable(), keyvalue);
    }

    public void updateTxStatus(GameChildType lotteryType, String orderno, OrderTxStatus txStatus, String openResult,
                               BigDecimal winmoney, String betItem, BigDecimal feemoney, JSONObject remark, NewLotteryPeriodInfo periodInfo)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(openResult))
        {
            setKeyValue.put("order_open_result", openResult);
        }
        if(!StringUtils.isEmpty(betItem))
        {
            setKeyValue.put("order_bet_item", betItem);
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



        if(periodInfo != null && !StringUtils.isEmpty(periodInfo.getReferenceExternal()))
        {
            String referenceExt = periodInfo.getReferenceExternal();
            String referenceSeed1 = periodInfo.getReferenceSeed1();
            setKeyValue.put("order_reference_ext", referenceExt);
            setKeyValue.put("order_reference_seed1", StringUtils.getNotEmpty(referenceSeed1));
        }


        setKeyValue.put("order_status", txStatus.getKey());
        setKeyValue.put("order_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);

        update(lotteryType.getCategory().getOrderTable(), setKeyValue, whereKeyValue);
    }

    public NewLotteryOrderInfo findByNo(GameChildType lotteryType, String orderno)
    {
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, NewLotteryOrderInfo.class, orderno);
    }

    public void updateCashoutItem(GameChildType lotteryType, String orderno, String betItem)
    {
        String sql = "update " + lotteryType.getCategory().getOrderTable() + " set order_bet_item = ? where order_no = ?";
        mWriterJdbcService.executeUpdate(sql, betItem, orderno);
    }
    public NewLotteryOrderInfo findByIssueAndUser(GameChildType lotteryType, String issue, long userid)
    {
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " where order_issue = ? and order_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, NewLotteryOrderInfo.class, issue, userid);
    }

    public List<NewLotteryOrderInfo> queryListByUserid(DateTime fromTime, long userid, GameChildType rgType, int limit)
    {
        String time = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        // order_createtime >= ? and
        String sql = "select * from " + rgType.getCategory().getOrderTable() + " where order_userid = ? and (order_status = 'realized' or order_status = 'failed') and order_lottery_type = ? order by order_createtime desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, NewLotteryOrderInfo.class, userid, rgType.getKey());
    }

    public void queryAllByIssue(GameChildType lotteryType, String issue, Callback<NewLotteryOrderInfo> callback)
    {
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " where order_issue = ?";
        mSlaveJdbcService.queryAll(callback, sql, NewLotteryOrderInfo.class, issue);
    }

    public void queryAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<NewLotteryOrderInfo> callback)
    {
        String fromStr = from.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " where order_createtime between ? and ? and order_status = ?";
        mSlaveJdbcService.queryAll(callback, sql, NewLotteryOrderInfo.class, fromStr, toStr, txStatus.getKey());
    }

    public void statsAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback)
    {
        String fromStr = from.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select order_status, count(1) as order_total_record_count, ");
        sql.append(" order_agentname, order_agentid, order_staffname, order_staffid ");
        addSumColumn(sql, "order_total_bet_amount");
//        addSumColumn(sql, "order_total_bet_count");
        addSumColumn(sql, "order_win_amount");
        addSumColumn(sql, "order_feemoney");
        sql.append(" from ").append(lotteryType.getCategory().getOrderTable());
        sql.append(" where order_createtime between ? and ? and order_usertype='member' and (order_status = ? or order_status = ?) ");
        sql.append(" group by order_agentname, order_agentid, order_staffname, order_staffid, order_status ");
        mSlaveJdbcService.queryAll(true, callback, sql.toString(), NewLotteryOrderInfo.class, fromStr, toStr, OrderTxStatus.REALIZED.getKey(), OrderTxStatus.FAILED.getKey());
    }

    public void queryAllPendingByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback)
    {
        String fromStr = from.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " where order_createtime between ? and ? ";
        sql += "and (order_status = ? or order_status = ? or order_status = ?)";
        mSlaveJdbcService.queryAll(callback, sql, NewLotteryOrderInfo.class, fromStr, toStr, OrderTxStatus.NEW.getKey(), OrderTxStatus.PENDING.getKey(), OrderTxStatus.WAITING.getKey());
    }

    public RowPager<NewLotteryOrderInfo> queryScrollPage(PageVo pageVo, GameChildType gameType,
                                                         long userid, long agentid, long staffid, GameChildType tbGameType,
                                                         String systemNo, String issue, OrderTxStatus txStatus, String sortName, String sortOrder)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.order_userid and money_currency = ? ");
        values.add(ICurrencyType.getSupportCurrency().getKey());
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

            if(gameType != null)
            {
                values.add(gameType.getKey());
                whereSQLBuffer.append(" and order_lottery_type = ? ");
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
        String countsql = "select count(1) from " + tbGameType.getCategory().getOrderTable() + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*, B.attr_direct_staffname as order_staffname");
        select.append(", C.money_balance as order_balance,C.money_total_recharge as order_total_recharge,(C.money_total_withdraw-C.money_total_refund) as order_total_withdraw ");
        select.append(" from ").append(tbGameType.getCategory().getOrderTable()).append( " as A ");
        select.append(" left join inso_passport_user_attr as B on B.attr_userid=A.order_userid ");
        select.append(whereSQL);

        if(sortName!=null && sortOrder!=null){

            if(sortName.equals("betAmount")){
                select.append(" order by  order_total_bet_amount "+ sortOrder);
            }

        }else{
            select.append(" order by order_createtime desc ");
        }
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<NewLotteryOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), NewLotteryOrderInfo.class, values.toArray());
        RowPager<NewLotteryOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAllMember(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<NewLotteryOrderInfo> callback)
    {
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " as A where order_createtime between ? and ? and order_usertype='member' ";
        mSlaveJdbcService.queryAll(callback, sql, NewLotteryOrderInfo.class, startTimeString, endTimeString);
    }

    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback)
    {
        String sql = "select * from " + lotteryType.getCategory().getOrderTable() + " as A where order_createtime between ? and ? and order_usertype='member' ";
        mSlaveJdbcService.queryAll(true, callback, sql, GameBusinessDay.class, startTimeString, endTimeString);
    }

    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback)
    {
        String fromStr = from.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder sql = new StringBuilder();
        sql.append("select order_status, count(1) as order_total_record_count, ");
        sql.append(" order_agentname, order_agentid, order_staffname, order_staffid ");
        addSumColumn(sql, "order_total_bet_amount");
        addSumColumn(sql, "order_win_amount");
        addSumColumn(sql, "order_feemoney");
        sql.append(" from ").append(lotteryType.getCategory().getOrderTable());
        sql.append(" where order_createtime between ? and ? and order_usertype='member' and (order_status = ? or order_status = ?) ");
        sql.append(" group by order_agentname, order_agentid, order_staffname, order_staffid, order_status ");
        mSlaveJdbcService.queryAll(true, callback, sql.toString(), GameBusinessDay.class, fromStr, toStr, OrderTxStatus.REALIZED.getKey(), OrderTxStatus.FAILED.getKey());
    }


    private void addSumColumn(StringBuilder sql, String column)
    {
        sql.append(", sum(");
        sql.append(column);
        sql.append(") ");
        sql.append(column);

    }
}

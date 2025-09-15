package com.inso.modules.game.fm.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTime;
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
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class FMOrderDaoMysql extends DaoSupport implements FMOrderDao {

    private static final String TABLE = "inso_game_financial_management_order";

    /**
     *
     order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
     order_fmid          	        int(11) NOT NULL comment '理解产品id',

     order_userid	                int(11) NOT NULL,
     order_username    			varchar(50) NOT NULL comment  '',
     order_agentid 	            int(11) NOT NULL comment '所属代理id',

     order_status               	varchar(20) NOT NULL  comment '',

     order_buy_amount              decimal(18,2) NOT NULL DEFAULT 0 comment '认购金额',
     order_return_expected_amount  decimal(18,2) NOT NULL comment '预期收益金额',
     order_return_real_amount      decimal(18,2) NOT NULL comment '实际收益金额',

     order_createtime       		datetime NOT NULL,
     order_updatetime      		datetime DEFAULT NULL,
     order_remark             		varchar(1000) DEFAULT '',

     order_fm_type               	varchar(20) NOT NULL comment '产品类型',
     order_staffid	                int(11) NOT NULL DEFAULT 0,

     order_return_real_rate   	    decimal(18,2) NOT NULL comment '实际收益率',
     order_feemoney                decimal(18,2) NOT NULL DEFAULT 0 comment '手续费',

     order_endtime      	     	datetime NOT NULL comment '赎回时间',
     */

    public void addOrder(String orderno, long rpid, UserInfo userInfo, UserAttr userAttr, OrderTxStatus txStatus,
                         BigDecimal buyAmount, BigDecimal return_expected_amount,BigDecimal order_return_real_rate, long timeHorizon)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_fmid", rpid);
        keyvalue.put("order_userid", userInfo.getId());
        keyvalue.put("order_username", userInfo.getName());

        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());

        keyvalue.put("order_status", txStatus.getKey());

        keyvalue.put("order_buy_amount", buyAmount);
        keyvalue.put("order_return_expected_amount", return_expected_amount);

        keyvalue.put("order_fm_type", FMType.SIMPLE.getKey());

        keyvalue.put("order_return_real_rate", order_return_real_rate);

        DateTime dateTime = new DateTime();
       // DateTime endTime = dateTime.plusDays((int)timeHorizon + 2);
        DateTime endTime = dateTime.plusDays((int)timeHorizon);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String nowStr = sdf.format(endTime.toDate());
//        Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, nowStr);

        keyvalue.put("order_createtime", dateTime.toDate());
        keyvalue.put("order_endtime", endTime.toDate());

        persistent(TABLE, keyvalue);
    }

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, BigDecimal return_real_amount, BigDecimal feemoney, JSONObject remark)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }
        if(return_real_amount != null)
        {
            setKeyValue.put("order_return_real_amount", return_real_amount);
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

    public FMOrderInfo findByNo(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, FMOrderInfo.class, orderno);
    }

    @Override
    public FMOrderInfo findByNoAndUserid(long issue, long userid) {
        String sql = "select order_fmid,order_userid,SUM(order_buy_amount) AS order_buy_amount from " + TABLE + " where order_fmid = ? and order_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, FMOrderInfo.class, issue,userid);
    }

    public List<FMOrderInfo> queryListByUserid(String createtime, long userid,OrderTxStatus Status, int limit)
    {
        String sql ="";
        if(Status != null)
        {

            if(Status==OrderTxStatus.WAITING){
                sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? and order_status = '"+Status.getKey()+"' order by order_createtime desc limit " + limit;
            }else if(Status==OrderTxStatus.FAILED){
                sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? and order_status = '"+Status.getKey()+"' order by order_updatetime desc limit " + limit;
            }else if(Status==OrderTxStatus.REALIZED){
                sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? and order_status = '"+Status.getKey()+"' order by order_endtime desc limit " + limit;
            }

        }else{
            sql = "select * from " + TABLE + " where order_createtime >= ? and order_userid = ? order by order_createtime desc limit " + limit;
        }

        return mSlaveJdbcService.queryForList(sql, FMOrderInfo.class, createtime, userid);
    }

    public void queryAllByIssue(long issue, Callback<FMOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " where order_fmid = ?";
        mSlaveJdbcService.queryAll(callback, sql, FMOrderInfo.class, issue);
    }

    public RowPager<FMOrderInfo> queryScrollPage(PageVo pageVo, FMType fmType, long userid, String systemNo, long rpid, OrderTxStatus txStatus,long agentid,long staffid)
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
            whereSQLBuffer.append(" and order_fmid = ? ");
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

            if(fmType != null)
            {
                values.add(fmType.getKey());
                whereSQLBuffer.append(" and order_fm_type = ? ");
            }

            if(txStatus != null)
            {
                values.add(txStatus.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_financial_management_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*,B.attr_direct_staffname as order_staffname from inso_game_financial_management_order as A ");
        select.append(" left join inso_passport_user_attr as B on B.attr_userid=A.order_userid ");

        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<FMOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), FMOrderInfo.class, values.toArray());
        RowPager<FMOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAllMember(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " as A inner join inso_passport_user as B on A.order_userid=user_id and B.user_type = 'member' where order_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, FMOrderInfo.class, startTimeString, endTimeString);
    }

    public void queryAllByEndtime(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback)
    {
        String sql = "select * from " + TABLE + " where order_endtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, FMOrderInfo.class, startTimeString, endTimeString);
    }
}

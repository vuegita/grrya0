package com.inso.modules.game.task_checkin.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.task_checkin.model.TaskCheckinOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;

@Repository
public class TaskCheckinOrderDaoMysql extends DaoSupport implements TaskCheckinOrderDao {


    private static String TABLE = "inso_game_task_checkin_order";

    /**
     *   order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
     *   order_userid	                int(11) NOT NULL,
     *   order_username    			varchar(50) NOT NULL comment  '',
     *   order_agentid 	            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     *   order_amount              	decimal(18,2) NOT NULL comment '赠送总额-从配置读取',
     *   order_status               	varchar(20) NOT NULL  comment '',
     *   order_pdate            		date NOT NULL comment '签到日期',
     *   order_createtime       		datetime NOT NULL comment '签到时间',
     * @param userid
     * @param username
     */

    public void add(String orderno, long userid, String username, UserAttr userAttr, BigDecimal amount, OrderTxStatus txStatus)
    {
        Date date = new Date();
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_userid", userid);
        keyvalue.put("order_username", username);
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_amount", amount);
        keyvalue.put("order_status", txStatus.getKey());
        keyvalue.put("order_pdate", date);
        keyvalue.put("order_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateStatus(String orderno, OrderTxStatus txStatus)
    {
        String sql = "update " + TABLE + " set order_status = ? where order_no = ?";
        mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), orderno);
    }

//    public ReturnWaterLog findByUserid(long userid)
//    {
//        String sql = "select * from " + TABLE + " where log_userid = ? ";
//        return mSlaveJdbcService.queryForObject(sql, ReturnWaterLog.class, userid);
//    }

    public List<TaskCheckinOrderInfo> queryScrollPageByUser(String startTime, String endTime, long userid, long offset, long pagesize)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TABLE);
        sql.append(" where order_pdate between ? and ? and order_userid = ? ");

        sql.append(" order by order_pdate desc ");
        sql.append(" limit ").append(offset).append(",").append(pagesize);
        return mSlaveJdbcService.queryForList(sql.toString(), TaskCheckinOrderInfo.class, startTime,endTime, userid);
    }

    @Override
    public RowPager<TaskCheckinOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long userid, long agentid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" ");


        whereSQLBuffer.append(" where 1 = 1");

        if(!StringUtils.isEmpty(orderno))
        {
            values.add(orderno);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and order_agentid = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and order_pdate between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
            }
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_task_checkin_order " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_game_task_checkin_order ");
        select.append(whereSQL);
        select.append(" order by order_pdate desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<TaskCheckinOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), TaskCheckinOrderInfo.class, values.toArray());
        RowPager<TaskCheckinOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

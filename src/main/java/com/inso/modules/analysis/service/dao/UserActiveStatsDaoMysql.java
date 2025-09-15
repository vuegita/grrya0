package com.inso.modules.analysis.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class UserActiveStatsDaoMysql extends DaoSupport implements UserActiveStatsDao{

    private static String TABLE = "inso_report_data_analysis_user_active_stats_day";

    /**
     day_pdate	 				date NOT NULL ,
     day_hour   	         	int(11) NOT NULL DEFAULT 0 comment '24小时制',

     day_userid	            int(11) NOT NULL DEFAULT 0,
     day_username 	            varchar(50) NOT NULL,
     day_agentid 	            int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname 	        varchar(50) NOT NULL comment '所属代理',
     day_staffid	            int(11) NOT NULL DEFAULT 0,
     day_staffname 	        varchar(50) NOT NULL,

     day_online_duration   		int(11) NOT NULL DEFAULT 0 comment '停留应用总时长',
     day_stay_rg_duration       int(11) NOT NULL DEFAULT 0 comment '停留RG游戏总时长',
     day_stay_ab_duration       int(11) NOT NULL DEFAULT 0 comment '停留AB游戏总时长',
     day_stay_fruit_duration    int(11) NOT NULL DEFAULT 0 comment '停留水果机游戏总时长',
     day_stay_fm_duration       int(11) NOT NULL DEFAULT 0 comment '停留理财总时长',
     day_remark         	    varchar(1000) NOT NULL DEFAULT '' comment  '',
     */

    public void addReport(Date pdate, UserActiveStatsInfo report)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", pdate);
        keyvalue.put("day_hours", report.getHours());

        keyvalue.put("day_userid", report.getUserid());
        keyvalue.put("day_username", StringUtils.getNotEmpty(report.getUsername()));

        keyvalue.put("day_agentid", report.getAgentid());
        keyvalue.put("day_agentname", StringUtils.getNotEmpty(report.getAgentname()));

        keyvalue.put("day_staffid", report.getStaffid());
        keyvalue.put("day_staffname", StringUtils.getNotEmpty(report.getStaffname()));

        keyvalue.put("day_online_duration", report.getOnlineDuration());

        keyvalue.put("day_stay_rg_duration", report.getStayRgDuration());
        keyvalue.put("day_stay_ab_duration", report.getStayAbDuration());
        keyvalue.put("day_stay_fruit_duration", report.getStayFruitDuration());
        keyvalue.put("day_stay_fm_duration", report.getStayFmDuration());

        keyvalue.put("day_remark", StringUtils.getNotEmpty(report.getRemark()));

        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, long userid, long hour)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_userid = ? and day_hours = ? ";
        mWriterJdbcService.executeUpdate(sql, pdate, userid, hour);
    }

    public RowPager<UserActiveStatsInfo> queryScrollPage(PageVo pageVo, UserInfo.UserType userType, long agentid, long staffid, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and day_agentid = ? ");
        }

        if(userType == UserInfo.UserType.STAFF)
        {
            whereSQLBuffer.append(" and day_staffid = 0 ");
        }
        else if(userType == UserInfo.UserType.MEMBER)
        {
            whereSQLBuffer.append(" and day_staffid > 0 ");
        }

//        else if(staffid > 0)
//        {
//            values.add(staffid);
//            whereSQLBuffer.append(" and day_staffid = ? ");
//        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and day_userid = ? ");
        }

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
        select.append(" order by day_pdate desc, day_hours desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<UserActiveStatsInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserActiveStatsInfo.class, values.toArray());
        RowPager<UserActiveStatsInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

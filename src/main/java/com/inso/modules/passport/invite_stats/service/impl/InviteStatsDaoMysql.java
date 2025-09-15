package com.inso.modules.passport.invite_stats.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.invite_stats.model.InviteStatsInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.UserAttrDaoMysql;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class InviteStatsDaoMysql extends DaoSupport implements InviteStatsDao {

    private static final String TABLE = "inso_passport_invite_stats_day";

    public void add(Date date, String key, UserInfo userInfo)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        keyValues.put("day_key", key);

        if(userInfo != null)
        {
            keyValues.put("day_userid", userInfo.getId());
            keyValues.put("day_username", userInfo.getName());
        }
        else
        {
            keyValues.put("day_userid", 0);
            keyValues.put("day_username", StringUtils.getEmpty());
        }

        keyValues.put("day_total_count", 0);
        keyValues.put("day_pdate", date);
        persistent(TABLE, keyValues);
    }

    public void updateInfo(Date pdate, String key, long totalCount)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(TABLE);
        sql.append(" set day_total_count = ?");
        sql.append(" where day_pdate = ? and day_key = ? ");

        String pdateStr = DateUtils.convertString(pdate, DateUtils.TYPE_YYYY_MM_DD);

        mWriterJdbcService.executeUpdate(sql.toString(), totalCount, pdateStr, key);
    }

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<InviteStatsInfo> callback)
    {
        String fromStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where day_pdate between ? and ? ";
        mSlaveJdbcService.queryAll(true, callback, sql, InviteStatsInfo.class, fromStr, toStr);
    }


    public RowPager<InviteStatsInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String key, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from " + TABLE + " as A");

        boolean joinAgent = agentid > 0 || staffid > 0;
        if(joinAgent)
        {
            whereSQLBuffer.append(" inner join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.day_userid = B.attr_userid ");
        }
        whereSQLBuffer.append(" where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(key != null)
        {
            values.add(key);
            whereSQLBuffer.append(" and A.day_key = ? ");
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.day_userid = ? ");
        }

        if(joinAgent)
        {
            if(agentid > 0)
            {
                values.add(agentid);
                whereSQLBuffer.append(" and B.attr_agentid = ?");
            }

            if(staffid > 0)
            {
                values.add(staffid);
                whereSQLBuffer.append(" and B.attr_direct_staffid = ?");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        if(joinAgent)
        {
            select.append(", B.attr_direct_staffname day_staffname, B.attr_agentname day_agentname ");
        }

        select.append(whereSQL);
        select.append(" order by day_id  desc ");//A.day_userid
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<InviteStatsInfo> list = mSlaveJdbcService.queryForList(select.toString(), InviteStatsInfo.class, values.toArray());
        RowPager<InviteStatsInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

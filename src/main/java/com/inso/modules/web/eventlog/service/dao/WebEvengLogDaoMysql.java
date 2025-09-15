package com.inso.modules.web.eventlog.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.eventlog.model.WebEventLogInfo;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class WebEvengLogDaoMysql extends DaoSupport implements WebEvengLogDao {


    // 系统事件日志
    private static final String TABLE_ADMIN = "inso_web_event_log";

    // 会员事件日志
    private static final String TABLE_MEMBER = "inso_web_event_member_log";

    public void addLog(WebEventLogType logType, String title, String content, String ip, String userAgent, UserInfo agentInfo, String operator)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();

        keyValues.put("log_type", logType.getKey());
        keyValues.put("log_title", StringUtils.getNotEmpty(title));
        keyValues.put("log_content", StringUtils.getNotEmpty(content));

        keyValues.put("log_ip", StringUtils.getNotEmpty(ip));
        keyValues.put("log_useragent", StringUtils.getNotEmpty(userAgent));

        if(agentInfo != null)
        {
            keyValues.put("log_agentid", agentInfo.getId());
            keyValues.put("log_agentname", agentInfo.getName());
        }
        else
        {
            keyValues.put("log_agentid", 0);
            keyValues.put("log_agentname", StringUtils.getEmpty());
        }


        keyValues.put("log_operator", operator);

        keyValues.put("log_createtime", new Date());
        keyValues.put("log_remark", StringUtils.getEmpty());

        persistent(getTable(logType), keyValues);
    }


    private String getTable(WebEventLogType logType)
    {
        if(logType == null || logType.isAdmin())
        {
            return TABLE_ADMIN;
        }
        return TABLE_MEMBER;
    }

    public RowPager<WebEventLogInfo> queryScrollPage(PageVo pageVo, WebEventLogType eventLogType, long agentid, WebEventLogType tbTable, String operator, String ignoreOperator)
    {
        String tableName = getTable(tbTable);
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

//        // 时间放前面
        whereSQLBuffer.append(" and log_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(eventLogType != null)
        {
            values.add(eventLogType.getKey());
            whereSQLBuffer.append(" and log_type = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and log_agentid = ? ");
        }

        if(!StringUtils.isEmpty(operator))
        {
            values.add(operator);
            whereSQLBuffer.append(" and log_operator = ? ");
        }

        if(!StringUtils.isEmpty(ignoreOperator))
        {
            values.add(ignoreOperator);
            whereSQLBuffer.append(" and log_operator != ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + tableName + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());
        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ");
        select.append(tableName);
        select.append(whereSQL);
        select.append(" order by log_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<WebEventLogInfo> list = mSlaveJdbcService.queryForList(select.toString(), WebEventLogInfo.class, values.toArray());
        RowPager<WebEventLogInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

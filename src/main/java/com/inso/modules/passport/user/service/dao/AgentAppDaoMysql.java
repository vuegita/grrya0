package com.inso.modules.passport.user.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class AgentAppDaoMysql extends DaoSupport implements AgentAppDao {

    /**
     app_id 						int(11) NOT NULL AUTO_INCREMENT,
     app_agentid 				    int(11) NOT NULL comment '商户ID',
     app_agentname                 varchar(50) NOT NULL comment '商户用户名',
     app_access_key				varchar(255) NOT NULL,
     app_access_secret	  		    varchar(255) NOT NULL,
     app_notify_url	            varchar(200) DEFAULT '' comment '回调地址',
     app_createtime  				datetime DEFAULT NULL ,
     */
    private static final String TABLE = "inso_passport_user_agent_app";

    @Override
    public void add(UserInfo userInfo, String accessKey, String secret, String approveNotifyUrl, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("app_agentid", userInfo.getId());
        keyvalue.put("app_agentname", userInfo.getName());
        keyvalue.put("app_access_key", accessKey);
        keyvalue.put("app_access_secret", secret);
        keyvalue.put("app_approve_notify_url", approveNotifyUrl);
        keyvalue.put("app_status", status.getKey());
        keyvalue.put("app_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long agentid, String approveNotifyUrl, String secret, Status status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("app_status", status.getKey());
        }

        if(!StringUtils.isEmpty(approveNotifyUrl))
        {
            setKeyValue.put("app_approve_notify_url", approveNotifyUrl);
        }

        if(!StringUtils.isEmpty(secret))
        {
            setKeyValue.put("app_access_secret", secret);
        }


        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("app_agentid", agentid);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AgentAppInfo findByAgentId(long agentid)
    {
        StringBuilder sql = new StringBuilder("select * from " + TABLE);
        sql.append(" where app_agentid = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), AgentAppInfo.class, agentid);
    }


    @Override
    public RowPager<AgentAppInfo> queryScrollPage(PageVo pageVo, long agentid, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and app_agentid = ? ");
        }
        else
        {
            // 时间放前面
//            whereSQLBuffer.append(" and app_createtime between ? and ? ");
//            values.add(pageVo.getFromTime());
//            values.add(pageVo.getToTime());

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and app_status = ? ");
            }

        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        select.append("select * from ");
        select.append(whereSQL);
        select.append(" order by app_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AgentAppInfo> list = mSlaveJdbcService.queryForList(select.toString(), AgentAppInfo.class, values.toArray());
        RowPager<AgentAppInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

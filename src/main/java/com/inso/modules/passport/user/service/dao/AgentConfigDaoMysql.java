package com.inso.modules.passport.user.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class AgentConfigDaoMysql extends DaoSupport implements AgentConfigDao {

    /**
     config_id 						int(11) NOT NULL AUTO_INCREMENT,
     config_agentid 				    int(11) NOT NULL comment '',
     config_agentname                  varchar(50) NOT NULL comment '',
     config_type    	  		        varchar(255) NOT NULL,
     config_key    	  		        varchar(255) NOT NULL,
     config_value    	  		        varchar(255) NOT NULL,
     config_status    	  		        varchar(255) NOT NULL,
     config_createtime  				datetime DEFAULT NULL ,
     */
    public static final String TABLE = "inso_passport_user_agent_config";

    @Override
    public long add(UserInfo agentInfo, AgentConfigInfo.AgentConfigType type, String value, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("config_agentid", agentInfo.getId());
        keyvalue.put("config_agentname", agentInfo.getName());

        keyvalue.put("config_type", type.getKey());
        keyvalue.put("config_value", StringUtils.getNotEmpty(value));

        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, String value, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();


        if(value != null)
        {
            setKeyValue.put("config_value", value);
        }

        if(status != null)
        {
            setKeyValue.put("config_status", status.getKey());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("config_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AgentConfigInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, AgentConfigInfo.class, id);
    }

    public AgentConfigInfo findByAgentId(long agentid, AgentConfigInfo.AgentConfigType type)
    {
        String sql = "select * from " + TABLE + "  where config_agentid = ? and config_type = ?";
        return mSlaveJdbcService.queryForObject(sql, AgentConfigInfo.class, agentid, type.getKey());
    }

    @Override
    public RowPager<AgentConfigInfo> queryScrollPage(PageVo pageVo, long agentid, AgentConfigInfo.AgentConfigType type, Status status)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and config_agentid = ? ");
        }

        if(type != null)
        {
            values.add(type.getKey());
            whereSQLBuffer.append(" and config_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and config_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by config_id asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AgentConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), AgentConfigInfo.class, values.toArray());
        RowPager<AgentConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

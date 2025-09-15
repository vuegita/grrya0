package com.inso.modules.passport.domain.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class AgentDomainDaoMysql extends DaoSupport implements AgentDomainDao {

    /**
     domain_id               int(11) NOT NULL AUTO_INCREMENT ,
     domain_url              varchar(255) NOT NULL comment '域名,如 https://www.baidu.com',
     domain_agentid          int(11) NOT NULL,
     domain_agentname        varchar(255) NOT NULL ,
     domain_stafffid         varchar(255) NOT NULL ,
     domain_staffname        varchar(255) NOT NULL ,
     domain_status           varchar(20) NOT NULL,
     domain_createtime       datetime DEFAULT NULL ,
     */
    private static final String TABLE = "inso_passport_agent_domain";

    @Override
    public void add(UserAttr userAttr, String url, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("domain_url", url);

        keyvalue.put("domain_agentid", userAttr.getAgentid());
        keyvalue.put("domain_agentname", userAttr.getAgentname());
        keyvalue.put("domain_staffid", userAttr.getUserid());
        keyvalue.put("domain_staffname", userAttr.getUsername());
        keyvalue.put("domain_status", status.getKey());
        keyvalue.put("domain_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, String url, Status status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("domain_status", status.getKey());
        }

        if(!StringUtils.isEmpty(url))
        {
            setKeyValue.put("domain_url", url);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("domain_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AgentDomainInfo findByid(long id)
    {
        StringBuilder sql = new StringBuilder("select * from " + TABLE);
        sql.append(" where domain_id = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), AgentDomainInfo.class, id);
    }

    public AgentDomainInfo findByUrl(String url)
    {
        StringBuilder sql = new StringBuilder("select * from " + TABLE);
        sql.append(" where domain_url = ? ");
        return mSlaveJdbcService.queryForObject(sql.toString(), AgentDomainInfo.class, url);
    }

    public void deleteInfo(long id)
    {
        String sql = "delete from " + TABLE + " where domain_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    @Override
    public RowPager<AgentDomainInfo> queryScrollPage(PageVo pageVo, long agentid, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and domain_agentid = ? ");
        }
        else
        {
            // 时间放前面
//            whereSQLBuffer.append(" and domain_createtime between ? and ? ");
//            values.add(pageVo.getFromTime());
//            values.add(pageVo.getToTime());

//            if(status != null)
//            {
//                values.add(status.getKey());
//                whereSQLBuffer.append(" and domain_status = ? ");
//            }

        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and domain_status = ? ");
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
        select.append(" order by domain_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AgentDomainInfo> list = mSlaveJdbcService.queryForList(select.toString(), AgentDomainInfo.class, values.toArray());
        RowPager<AgentDomainInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

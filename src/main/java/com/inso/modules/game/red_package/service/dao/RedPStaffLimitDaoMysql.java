package com.inso.modules.game.red_package.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.passport.user.model.UserAttr;

@Repository
public class RedPStaffLimitDaoMysql extends DaoSupport implements RedPStaffLimitDao {

    private static final String TABLE = "inso_game_red_package_staff_limit";

    /**
     *
     config_agentid       		    	int(11)  NOT NULL ,
     config_agentname    			    varchar(50) NOT NULL ,
     config_staffid       			    int(11)  NOT NULL ,
     config_staffname    		        varchar(50) NOT NULL ,

     config_max_money_of_day           decimal(18,2) NOT NULL comment '每天最大金额',
     config_max_count_of_day           int(11) NOT NULL comment '每天发送金额次数',

     config_createtime                 datetime NOT NULL,
     config_status                     varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     config_remark 			        varchar(500) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    public void addConfig(UserAttr staffAttrInfo, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();

        keyValues.put("limit_agentid", staffAttrInfo.getAgentid());
        keyValues.put("limit_agentname", staffAttrInfo.getAgentname());
        keyValues.put("limit_staffid", staffAttrInfo.getUserid());  //getDirectStaffid()
        keyValues.put("limit_staffname", staffAttrInfo.getUsername());  //getDirectStaffname()

        keyValues.put("limit_max_money_of_single", maxMoneyOfSingle);
        keyValues.put("limit_max_money_of_day", maxMoneyOfDay);
        keyValues.put("limit_max_count_of_day", maxCountOfDay);
        keyValues.put("limit_createtime", new Date());

        keyValues.put("limit_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("limit_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, BigDecimal maxMoneyOfSingle, BigDecimal maxMoneyOfDay, long maxCountOfDay, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(maxMoneyOfDay != null)
        {
            setKeyValue.put("limit_max_money_of_single", maxMoneyOfSingle);
        }

        if(maxMoneyOfDay != null)
        {
            setKeyValue.put("limit_max_money_of_day", maxMoneyOfDay);
        }

        setKeyValue.put("limit_max_count_of_day", maxCountOfDay);

        if(status != null)
        {
            setKeyValue.put("limit_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("limit_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("limit_id", id);

        update(TABLE, setKeyValue, where);
    }

    public RedPStaffLimit findById(long id)
    {
        String sql = "select * from " + TABLE + " where limit_id = ?";
        return mSlaveJdbcService.queryForObject(sql, RedPStaffLimit.class, id);
    }

    public RedPStaffLimit findByStaffId(long staffid)
    {
        String sql = "select * from " + TABLE + " where limit_staffid = ?";
        return mSlaveJdbcService.queryForObject(sql, RedPStaffLimit.class, staffid);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where limit_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public RowPager<RedPStaffLimit> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        // 时间放前面
//        whereSQLBuffer.append(" and limit_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and limit_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and limit_staffid = ? ");
        }


        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and limit_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());
        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by limit_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<RedPStaffLimit> list = mSlaveJdbcService.queryForList(select.toString(), RedPStaffLimit.class, values.toArray());
        RowPager<RedPStaffLimit> rowPage = new RowPager<>(list.size(), list);
        return rowPage;
    }


}

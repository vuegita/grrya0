package com.inso.modules.web.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.modules.web.model.StaffkefuType;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.StaffKefu;

@Repository
public class StaffKefuDaoMysql extends DaoSupport implements StaffKefuDao {

    private static final String TABLE = "inso_web_staff_kefu";

    public void addKefu(UserAttr staffAttrInfo, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();

        keyValues.put("kefu_agentid", staffAttrInfo.getAgentid());
        keyValues.put("kefu_agentname", staffAttrInfo.getAgentname());
        keyValues.put("kefu_staffid", staffAttrInfo.getUserid());  //taffAttrInfo.getDirectStaffid()
        keyValues.put("kefu_staffname", staffAttrInfo.getUsername());  //staffAttrInfo.getDirectStaffname()

        keyValues.put("kefu_title", title);
        keyValues.put("kefu_describe", describe);
        keyValues.put("kefu_icon", StringUtils.getNotEmpty(icon));
        keyValues.put("kefu_whatsapp", whatsapp);
        keyValues.put("kefu_telegram", telegram);
        keyValues.put("kefu_createtime", new Date());
        keyValues.put("kefu_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("kefu_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(title))
        {
            setKeyValue.put("kefu_title", title);
        }

        if(!StringUtils.isEmpty(describe))
        {
            setKeyValue.put("kefu_describe", describe);
        }

        if(!StringUtils.isEmpty(icon))
        {
            setKeyValue.put("kefu_icon", icon);
        }

        if(!StringUtils.isEmpty(whatsapp))
        {
            setKeyValue.put("kefu_whatsapp", whatsapp);
        }

        if(!StringUtils.isEmpty(telegram))
        {
            setKeyValue.put("kefu_telegram", telegram);
        }

        if(status != null)
        {
            setKeyValue.put("kefu_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("kefu_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("kefu_id", id);

        update(TABLE, setKeyValue, where);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where kefu_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

//    public StaffKefu findById(UserAttr staffAttrInfo)
//    {
//        String sql = "select * from " + TABLE + " where kefu_agentid = ? and kefu_staffid = ?";
//        return mSlaveJdbcService.queryForObject(sql, StaffKefu.class, staffAttrInfo.getAgentid(), staffAttrInfo.getDirectStaffid());
//    }

    public  List<StaffKefu> findById(UserAttr staffAttrInfo, StaffkefuType staffkefuType)
    {
        if(staffkefuType!=null){
            String sql = "select * from " + TABLE + " where kefu_agentid = ? and kefu_staffid = ? and kefu_describe = ?";
            return mSlaveJdbcService.queryForList(sql, StaffKefu.class, staffAttrInfo.getAgentid(), staffAttrInfo.getUserid(),staffkefuType.getKey());
        }else{
            String sql = "select * from " + TABLE + " where kefu_agentid = ? and kefu_staffid = ?";
            return mSlaveJdbcService.queryForList(sql, StaffKefu.class, staffAttrInfo.getAgentid(), staffAttrInfo.getDirectStaffid());
        }

    }


    public StaffKefu findById(long id)
    {
        String sql = "select * from " + TABLE + " where kefu_id = ?";
        return mSlaveJdbcService.queryForObject(sql, StaffKefu.class, id);
    }

    public RowPager<StaffKefu> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

//        // 时间放前面
//        whereSQLBuffer.append(" and card_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and kefu_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and kefu_staffid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and kefu_status = ? ");
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
        select.append(" order by kefu_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<StaffKefu> list = mSlaveJdbcService.queryForList(select.toString(), StaffKefu.class, values.toArray());
        RowPager<StaffKefu> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

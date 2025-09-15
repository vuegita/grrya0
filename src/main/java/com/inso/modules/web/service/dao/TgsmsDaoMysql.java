package com.inso.modules.web.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.model.Tgsms;
import com.inso.modules.web.model.TipsType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class TgsmsDaoMysql extends DaoSupport implements TgsmsDao {

    private static final String TABLE = "inso_web_agent_tgsms";


    public void addTgsms(UserAttr userAttr, String rbtoken, TipsType type, String chatid, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        if(UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userAttr.getUserType())){
            keyValues.put("tgsms_agentid", userAttr.getUserid());
            keyValues.put("tgsms_agentname", userAttr.getUsername());
            keyValues.put("tgsms_staffid", userAttr.getUserid());
            keyValues.put("tgsms_staffname", userAttr.getUsername());
        }else if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(userAttr.getUserType())){
            keyValues.put("tgsms_agentid", userAttr.getAgentid());
            keyValues.put("tgsms_agentname", userAttr.getAgentname());
            keyValues.put("tgsms_staffid", userAttr.getUserid());
            keyValues.put("tgsms_staffname", userAttr.getUsername());

        }else{
            keyValues.put("tgsms_agentid", userAttr.getAgentid());
            keyValues.put("tgsms_agentname", userAttr.getAgentname());
            keyValues.put("tgsms_staffid", userAttr.getDirectStaffid());
            keyValues.put("tgsms_staffname", userAttr.getDirectStaffname());
        }


//        keyValues.put("tgsms_agentid", userAttr.getUserid());
//        keyValues.put("tgsms_agentname", userAttr.getUsername());

        keyValues.put("tgsms_rbtoken", rbtoken);
        keyValues.put("tgsms_type", type.getKey());
        keyValues.put("tgsms_chatid", chatid);

        keyValues.put("tgsms_createtime", new Date());
        keyValues.put("tgsms_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("tgsms_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, String rbtoken, TipsType type, String chatid, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(rbtoken))
        {
            setKeyValue.put("tgsms_rbtoken", rbtoken);
        }

        if(type != null)
        {
            setKeyValue.put("tgsms_type", type.getKey());
        }

        if(!StringUtils.isEmpty(chatid))
        {
            setKeyValue.put("tgsms_chatid", chatid);
        }


        if(status != null)
        {
            setKeyValue.put("tgsms_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("tgsms_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("tgsms_id", id);

        update(TABLE, setKeyValue, where);
    }

//    public StaffKefu findById(UserAttr staffAttrInfo)
//    {
//        String sql = "select * from " + TABLE + " where kefu_agentid = ? and kefu_staffid = ?";
//        return mSlaveJdbcService.queryForObject(sql, StaffKefu.class, staffAttrInfo.getAgentid(), staffAttrInfo.getDirectStaffid());
//    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where tgsms_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public  List<Tgsms> findAgentid(long agentid ,long staffid)
    {
        if(staffid > 0){
            String sql = "select * from " + TABLE + " where tgsms_staffid = ? ";
            return mSlaveJdbcService.queryForList(sql, Tgsms.class, staffid);
        }else{
            String sql = "select * from " + TABLE + " where tgsms_agentid = ? ";
            return mSlaveJdbcService.queryForList(sql, Tgsms.class, agentid);
        }

    }

    public  List<Tgsms> findByTypeAndUserid(long agentid , TipsType type)
    {
        String sql = "select * from " + TABLE + " where tgsms_agentid = ? and tgsms_type = ?";
        return mSlaveJdbcService.queryForList(sql, Tgsms.class, agentid,type.getKey());

    }


    public Tgsms findById(long id)
    {
        String sql = "select * from " + TABLE + " where tgsms_id = ?";
        return mSlaveJdbcService.queryForObject(sql, Tgsms.class, id);
    }

    public RowPager<Tgsms> queryScrollPage(PageVo pageVo, Status status , long agentid, long staffid)
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
            whereSQLBuffer.append(" and tgsms_agentid= ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and tgsms_staffid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and tgsms_status = ? ");
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
        select.append(" order by tgsms_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<Tgsms> list = mSlaveJdbcService.queryForList(select.toString(), Tgsms.class, values.toArray());
        RowPager<Tgsms> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

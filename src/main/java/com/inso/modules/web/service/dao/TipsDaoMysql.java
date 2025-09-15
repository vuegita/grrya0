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
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class TipsDaoMysql extends DaoSupport implements TipsDao {

    private static final String TABLE = "inso_web_agent_tips";


    public void addTips(UserAttr userAttr, String title, TipsType type, String content, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        if(UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userAttr.getUserType())){
            keyValues.put("tips_belong_agentid", userAttr.getUserid());
            keyValues.put("tips_belong_agentname", userAttr.getUsername());
            keyValues.put("tips_staffid", userAttr.getUserid());
            keyValues.put("tips_staffname", userAttr.getUsername());
        }else if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(userAttr.getUserType())){
            keyValues.put("tips_belong_agentid", userAttr.getAgentid());
            keyValues.put("tips_belong_agentname", userAttr.getAgentname());
            keyValues.put("tips_staffid", userAttr.getUserid());
            keyValues.put("tips_staffname", userAttr.getUsername());

        }else{
            keyValues.put("tips_belong_agentid", userAttr.getAgentid());
            keyValues.put("tips_belong_agentname", userAttr.getAgentname());
            keyValues.put("tips_staffid", userAttr.getDirectStaffid());
            keyValues.put("tips_staffname", userAttr.getDirectStaffname());
        }


        keyValues.put("tips_agentid", userAttr.getUserid());
        keyValues.put("tips_agentname", userAttr.getUsername());

        keyValues.put("tips_title", title);
        keyValues.put("tips_type", type.getKey());
        keyValues.put("tips_content", content);

        keyValues.put("tips_createtime", new Date());
        keyValues.put("tips_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("tips_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, String title, TipsType type, String content, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(title))
        {
            setKeyValue.put("tips_title", title);
        }

        if(type != null)
        {
            setKeyValue.put("tips_type", type.getKey());
        }

        if(!StringUtils.isEmpty(content))
        {
            setKeyValue.put("tips_content", content);
        }


        if(status != null)
        {
            setKeyValue.put("tips_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("tips_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("tips_id", id);

        update(TABLE, setKeyValue, where);
    }

//    public StaffKefu findById(UserAttr staffAttrInfo)
//    {
//        String sql = "select * from " + TABLE + " where kefu_agentid = ? and kefu_staffid = ?";
//        return mSlaveJdbcService.queryForObject(sql, StaffKefu.class, staffAttrInfo.getAgentid(), staffAttrInfo.getDirectStaffid());
//    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where tips_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public  List<Tips> findAgentid(long agentid)
    {
        String sql = "select * from " + TABLE + " where tips_agentid = ? ";
        return mSlaveJdbcService.queryForList(sql, Tips.class, agentid);

    }

    public  List<Tips> findByTypeAndUserid(long agentid , TipsType type)
    {
        String sql = "select * from " + TABLE + " where tips_agentid = ? and tips_type = ?";
        return mSlaveJdbcService.queryForList(sql, Tips.class, agentid,type.getKey());

    }


    public Tips findById(long id)
    {
        String sql = "select * from " + TABLE + " where tips_id = ?";
        return mSlaveJdbcService.queryForObject(sql, Tips.class, id);
    }

    public RowPager<Tips> queryScrollPage(PageVo pageVo, long userid, Status status , long agentid, long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

//        // 时间放前面
//        whereSQLBuffer.append(" and card_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(userid >= 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and tips_agentid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and tips_belong_agentid= ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and tips_staffid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and tips_status = ? ");
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
        select.append(" order by tips_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<Tips> list = mSlaveJdbcService.queryForList(select.toString(), Tips.class, values.toArray());
        RowPager<Tips> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

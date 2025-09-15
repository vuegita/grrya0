package com.inso.modules.web.service.dao;

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
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.KefuMember;

@Repository
public class KefuMemberDaoMysql extends DaoSupport implements KefuMemberDao {

    private static final String TABLE = "inso_web_kefu_member";

    public void addMember(long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        keyValues.put("member_groupid", groupid);
        keyValues.put("member_name", name);
        keyValues.put("member_title", title);
        keyValues.put("member_describe", describe);
        keyValues.put("member_icon", StringUtils.getNotEmpty(icon));
        keyValues.put("member_whatsapp", whatsapp);
        keyValues.put("member_telegram", telegram);
        keyValues.put("member_createtime", new Date());
        keyValues.put("member_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("member_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("member_name", name);
        }

        if(!StringUtils.isEmpty(describe))
        {
            setKeyValue.put("member_describe", describe);
        }

        if(!StringUtils.isEmpty(icon))
        {
            setKeyValue.put("member_icon", icon);
        }

        if(!StringUtils.isEmpty(whatsapp))
        {
            setKeyValue.put("member_whatsapp", whatsapp);
        }

        if(!StringUtils.isEmpty(telegram))
        {
            setKeyValue.put("member_telegram", telegram);
        }

        if(groupid > 0)
        {
            setKeyValue.put("member_groupid", groupid);
        }

        if(status != null)
        {
            setKeyValue.put("member_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("member_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("member_id", id);

        update(TABLE, setKeyValue, where);
    }

    public KefuMember findById(long id)
    {
        String sql = "select * from " + TABLE + " where member_id = ?";
        return mSlaveJdbcService.queryForObject(sql, KefuMember.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where member_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public long countByGroupId(long groupid)
    {
        String sql = "select count(1) from " + TABLE + " where member_groupid = ?";
        return mSlaveJdbcService.count(sql, groupid);
    }

    public List<KefuMember> queryAllByGroupid(long groupid)
    {
        String sql = "select * from " + TABLE + " where member_groupid = ?";
        return mSlaveJdbcService.queryForList(sql, KefuMember.class, groupid);
    }

    public RowPager<KefuMember> queryScrollPage(PageVo pageVo, long groupid, String name, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

//        // 时间放前面
//        whereSQLBuffer.append(" and card_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(!StringUtils.isEmpty(name))
        {
            values.add(name);
            whereSQLBuffer.append(" and member_name = ? ");
        }

        if(groupid > 0)
        {
            values.add(groupid);
            whereSQLBuffer.append(" and member_groupid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and member_status = ? ");
        }



        String whereSQL = whereSQLBuffer.toString();
//        String countsql = "select count(1) from  " + TABLE + whereSQL;
//        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select A.*, B.group_name as member_group_name from inso_web_kefu_member as A ");
        select.append(" left join inso_web_kefu_group as B on A.member_groupid = B.group_id ");
        select.append(whereSQL);
        select.append(" order by member_createtime desc ");
//        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<KefuMember> list = mSlaveJdbcService.queryForList(select.toString(), KefuMember.class, values.toArray());
        RowPager<KefuMember> rowPage = new RowPager<>(list.size(), list);
        return rowPage;
    }


}

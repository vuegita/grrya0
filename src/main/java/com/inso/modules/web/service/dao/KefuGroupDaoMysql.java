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
import com.inso.modules.web.model.KefuGroup;

@Repository
public class KefuGroupDaoMysql extends DaoSupport implements KefuGroupDao {

    private static final String TABLE = "inso_web_kefu_group";

    public void addGroup(String name, String describe, String icon, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        keyValues.put("group_name", name);
        keyValues.put("group_describe", describe);
        keyValues.put("group_icon", StringUtils.getNotEmpty(icon));
        keyValues.put("group_createtime", new Date());
        keyValues.put("group_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("group_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long groupid, String name, String describe, String icon, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("group_name", name);
        }

        if(!StringUtils.isEmpty(describe))
        {
            setKeyValue.put("group_describe", describe);
        }

        if(!StringUtils.isEmpty(icon))
        {
            setKeyValue.put("group_icon", icon);
        }

        setKeyValue.put("group_status", status.getKey());

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("group_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("group_id", groupid);

        update(TABLE, setKeyValue, where);
    }

    public KefuGroup findById(long id)
    {
        String sql = "select * from " + TABLE + " where group_id = ?";
        return mSlaveJdbcService.queryForObject(sql, KefuGroup.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where group_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public List<KefuGroup> queryAll()
    {
        String sql = "select * from " + TABLE;
        return mSlaveJdbcService.queryForList(sql, KefuGroup.class);
    }

    public RowPager<KefuGroup> queryScrollPage(PageVo pageVo, String name, Status status)
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
            whereSQLBuffer.append(" and group_name = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and group_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
//        String countsql = "select count(1) from  " + TABLE + whereSQL;
//        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select * from ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by group_createtime desc ");
//        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<KefuGroup> list = mSlaveJdbcService.queryForList(select.toString(), KefuGroup.class, values.toArray());
        RowPager<KefuGroup> rowPage = new RowPager<>(list.size(), list);
        return rowPage;
    }


}

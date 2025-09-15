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
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.FeedBack;

@Repository
public class FeedBackDaoMysql extends DaoSupport implements FeedBackDao {

    private static final String TABLE = "inso_web_feedback";

    /**
     *
     feedback_id       			    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     feedback_agentid       			int(11)  NOT NULL ,
     feedback_agentname    			varchar(50) NOT NULL ,
     feedback_staffid       			int(11)  NOT NULL ,
     feedback_staffname    			varchar(50) NOT NULL ,

     feedback_userid       			int(11)  NOT NULL comment '会员id',
     feedback_username    			    varchar(50) NOT NULL comment '会员用户名',

     feedback_type   			        varchar(20) NOT NULL comment '问题类型',
     feedback_title   			        varchar(50) NOT NULL,
     feedback_content   			    varchar(255) NOT NULL,
     feedback_reply   			        varchar(255) NOT NULL comment '回复内容',
     feedback_createtime  		        datetime NOT NULL,
     feedback_status                   varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'waiting|finish',
     feedback_remark 			        varchar(500) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    public void addFeedBack(UserAttr staffAttrInfo, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();

        keyValues.put("feedback_agentid", staffAttrInfo.getAgentid());
        keyValues.put("feedback_agentname", staffAttrInfo.getAgentname());
        keyValues.put("feedback_staffid", staffAttrInfo.getDirectStaffid());
        keyValues.put("feedback_staffname", staffAttrInfo.getDirectStaffname());
        keyValues.put("feedback_userid", staffAttrInfo.getUserid());
        keyValues.put(" feedback_username", staffAttrInfo.getUsername());

        keyValues.put("feedback_title", title);
        keyValues.put("feedback_type", feedBackType.getKey());
        keyValues.put("feedback_content", content);
        keyValues.put("feedback_reply", reply);

        keyValues.put("feedback_createtime", new Date());
        keyValues.put("feedback_status", status.getKey());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("feedback_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(title))
        {
            setKeyValue.put("feedback_title", title);
        }

        if(feedBackType != null)
        {
            setKeyValue.put("feedback_type", feedBackType.getKey());
        }

        if(!StringUtils.isEmpty(content))
        {
            setKeyValue.put("feedback_content", content);
        }

        if(!StringUtils.isEmpty(reply))
        {
            setKeyValue.put("feedback_reply", reply);
        }

        if(status != null)
        {
            setKeyValue.put("feedback_status", status.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("feedback_remark", remark.toJSONString());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("feedback_id", id);

        update(TABLE, setKeyValue, where);
    }

    public List<FeedBack> findByUserAttr(UserAttr staffAttrInfo)
    {
        String sql = "select * from " + TABLE + " where feedback_agentid = ? and feedback_staffid = ?and feedback_id = ?";
        return mSlaveJdbcService.queryForList(sql, FeedBack.class, staffAttrInfo.getAgentid(), staffAttrInfo.getDirectStaffid(),staffAttrInfo.getUserid());
    }

    @Override
    public List<FeedBack> queryListByUserid(Status status, String createtime, long userid, int limit) {
        String sql = "select * from " + TABLE + " where feedback_createtime >= ? and feedback_userid = ? and feedback_status = ? order by feedback_createtime desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, FeedBack.class, createtime, userid, status.getKey());
    }

    public FeedBack findById(long id)
    {
        String sql = "select * from " + TABLE + " where feedback_id = ?";
        return mSlaveJdbcService.queryForObject(sql, FeedBack.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where feedback_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public RowPager<FeedBack> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, FeedBackType feedBackType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and feedback_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and feedback_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and feedback_staffid = ? ");
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and feedback_userid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and feedback_status = ? ");
        }

        if(feedBackType != null)
        {
            values.add(feedBackType.getKey());
            whereSQLBuffer.append(" and feedback_type = ? ");
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
        select.append(" order by feedback_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<FeedBack> list = mSlaveJdbcService.queryForList(select.toString(), FeedBack.class, values.toArray());
        RowPager<FeedBack> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

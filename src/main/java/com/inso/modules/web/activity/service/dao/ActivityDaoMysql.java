package com.inso.modules.web.activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.dao.TeamBuyingGroupDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ActivityDaoMysql extends DaoSupport implements ActivityDao {

    /**
     activity_id                               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     activity_title                            varchar(255) NOT NULL DEFAULT '' comment  '',

     activity_agentid                          int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     activity_agentname                        varchar(255) NOT NULL DEFAULT '' comment  '',
     activity_staffid                          int(11) NOT NULL DEFAULT 0,
     activity_staffname                        varchar(255) NOT NULL DEFAULT '' comment  '',

     activity_business_type                    varchar(255) NOT NULL comment  '',
     activity_currency_type                    varchar(255) NOT NULL comment  '',

     activity_limit_min_invite_count           int(11) NOT NULL DEFAULT 0 comment '最低邀请人数',
     activity_limit_min_inves_amount        decimal(25,8) NOT NULL DEFAULT 0 comment '最低投资金额',

     activity_basic_present_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '邀请完成基础赠送',
     activity_extra_present_rate               decimal(25,8) NOT NULL DEFAULT 0 comment '通过比例额外赠送',

     activity_finish_invite_count              int(11) NOT NULL DEFAULT 0 comment '统计-总完成人数',
     activity_finish_inves_amount           decimal(25,8) NOT NULL DEFAULT 0 comment '统计-总完成充值金额',
     activity_finish_present_amount            decimal(25,8) NOT NULL DEFAULT 0 comment '统计-赠送总金额',

     activity_status                           varchar(20) NOT NULL comment 'waiting' comment '状态',
     activity_createtime                       datetime NOT NULL comment '创建时间',
     activity_begintime                        datetime NOT NULL comment '开始时间',
     activity_endtime                          datetime NOT NULL comment '结束时间',
     activity_remark                           varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_web_activity";

    @Override
    public long add(String title, UserAttr userAttr, ActivityBusinessType businessType, ICurrencyType currencyType,
                    BigDecimal limitMinInvesAmount, long limitMinInviteCount, BigDecimal basicPresentAmount, String extraPresentTier,
                    OrderTxStatus txStatus, DateTime beginTime, DateTime endTime)
    {
        DateTime date = new DateTime();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("activity_title", StringUtils.getNotEmpty(title));

        if(userAttr != null)
        {
            keyvalue.put("activity_agentid", userAttr.getAgentid());
            keyvalue.put("activity_agentname", userAttr.getAgentname());
            keyvalue.put("activity_staffid", userAttr.getDirectStaffid());
            keyvalue.put("activity_staffname", userAttr.getDirectStaffname());
        }

        keyvalue.put("activity_business_type", businessType.getKey());
        keyvalue.put("activity_currency_type", currencyType.getKey());

        keyvalue.put("activity_limit_min_invite_count", limitMinInviteCount);
        keyvalue.put("activity_limit_min_inves_amount", limitMinInvesAmount);

        keyvalue.put("activity_basic_present_amount", basicPresentAmount);
        keyvalue.put("activity_extra_present_tier", StringUtils.getNotEmpty(extraPresentTier));

        keyvalue.put("activity_finish_invite_count", 0);
        keyvalue.put("activity_finish_inves_amount", BigDecimal.ZERO);
        keyvalue.put("activity_finish_present_amount", BigDecimal.ZERO);

        keyvalue.put("activity_status", txStatus.getKey());
        keyvalue.put("activity_createtime", date.toDate());
        keyvalue.put("activity_begintime", beginTime.toDate());
        keyvalue.put("activity_endtime", endTime.toDate());
        keyvalue.put("activity_remark", StringUtils.getEmpty());

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, String title,
                           long finishInviteCount, long finishInvesCount,
                           BigDecimal finishInvesAmount, BigDecimal finishPresentAmount,
                           OrderTxStatus txStatus, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(title))
        {
            setKeyValue.put("activity_title", title);
        }

        if(finishInviteCount >= 0)
        {
            setKeyValue.put("activity_finish_invite_count", finishInviteCount);
        }

        if(finishInvesCount >= 0)
        {
            setKeyValue.put("activity_finish_inves_count", finishInvesCount);
        }

        if(finishInvesAmount != null)
        {
            setKeyValue.put("activity_finish_inves_amount", finishInvesAmount);
        }

        if(finishPresentAmount != null)
        {
            setKeyValue.put("activity_finish_present_amount", finishPresentAmount);
        }

        if(txStatus != null)
        {
            setKeyValue.put("activity_status", txStatus.getKey());
        }

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("activity_remark", remark.toString());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("activity_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public ActivityInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where activity_id = ?";
        return mSlaveJdbcService.queryForObject(sql, ActivityInfo.class, id);
    }

    public ActivityInfo findLatest(DateTime dateTime, ActivityBusinessType businessType)
    {
        String time = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where activity_createtime >= ? and activity_business_type = ? order by activity_createtime desc ";
        return mSlaveJdbcService.queryForObject(sql, ActivityInfo.class, time, businessType.getKey());
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where activity_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    @Override
    public RowPager<ActivityInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ActivityBusinessType businessType, OrderTxStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and activity_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and activity_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and activity_staffid = ? ");
        }

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and activity_business_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and activity_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        select.append(" order by activity_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ActivityInfo> list = mSlaveJdbcService.queryForList(select.toString(), ActivityInfo.class, values.toArray());
        RowPager<ActivityInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<ActivityInfo> callback)
    {
        String from = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String to = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where activity_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, ActivityInfo.class, from, to);
    }


}

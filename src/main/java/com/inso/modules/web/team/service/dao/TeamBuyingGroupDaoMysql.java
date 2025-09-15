package com.inso.modules.web.team.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class TeamBuyingGroupDaoMysql extends DaoSupport implements TeamBuyingGroupDao {

    /**
     group_id                             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     group_business_type                  varchar(255) NOT NULL comment  '',
     group_config_id                      int(11) NOT NULL DEFAULT 0 comment '',

     group_agentid                        int(11) NOT NULL comment '',
     group_agentname                      varchar(255) NOT NULL comment  '',
     group_staffid                        int(11) NOT NULL comment '',
     group_staffname                      varchar(255) NOT NULL comment  '',
     group_userid                         int(11) NOT NULL comment '',
     group_username                       varchar(255) NOT NULL comment  '',

     group_need_invite_count              int(11) NOT NULL comment '',
     group_has_invite_count               int(11) NOT NULL comment '',
     group_return_rate                    decimal(25,8) NOT NULL DEFAULT 0 comment '返回比例',

     group_status                         varchar(20) NOT NULL DEFAULT 'waiting' comment '完成状态',
     group_createtime                     datetime NOT NULL comment '创建时间',
     group_endtime                        datetime NOT NULL comment '结束时间',
     group_remark                         varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_web_team_buying_group";

    @Override
    public long add(TeamConfigInfo configInfo, UserAttr userAttr, OrderTxStatus txStatus, BigDecimal realInvesAmount)
    {
        DateTime date = new DateTime();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("group_business_type", configInfo.getBusinessType());
        keyvalue.put("group_config_id", configInfo.getId());

        keyvalue.put("group_userid", userAttr.getUserid());
        keyvalue.put("group_username", userAttr.getUsername());
        keyvalue.put("group_agentid", userAttr.getAgentid());
        keyvalue.put("group_agentname", userAttr.getAgentname());
        keyvalue.put("group_staffid", userAttr.getDirectStaffid());
        keyvalue.put("group_staffname", userAttr.getDirectStaffname());

        keyvalue.put("group_need_inves_amount", configInfo.getLimitMinAmount());
        keyvalue.put("group_real_inves_amount", realInvesAmount);

        keyvalue.put("group_need_invite_count", configInfo.getLimitMinInviteCount());
        keyvalue.put("group_has_invite_count", 0);

        keyvalue.put("group_return_creator_rate", configInfo.getReturnCreatorRate());
        keyvalue.put("group_return_join_rate", configInfo.getReturnJoinRate());

        keyvalue.put("group_currency_type", configInfo.getCurrencyType());

        keyvalue.put("group_status", txStatus.getKey());
        keyvalue.put("group_createtime", date.toDate());
        keyvalue.put("group_endtime", date.plusDays(1).toDate());
        keyvalue.put("group_remark", StringUtils.getEmpty());

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(long id, long hasInviteCount, OrderTxStatus txStatus)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(txStatus != null)
        {
            setKeyValue.put("group_status", txStatus.getKey());
        }

        if(hasInviteCount >= 0)
        {
            setKeyValue.put("group_has_invite_count", hasInviteCount);
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("group_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public TeamBuyGroupInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where group_id = ?";
        return mSlaveJdbcService.queryForObject(sql, TeamBuyGroupInfo.class, id);
    }

    public TeamBuyGroupInfo findLatest(long userid, TeamBusinessType businessType)
    {
        DateTime dateTime = new DateTime().minusDays(1);
        String time = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where group_createtime >= ? and group_userid = ? and group_business_type = ? order by group_createtime desc ";
        return mSlaveJdbcService.queryForObject(sql, TeamBuyGroupInfo.class, time, userid, businessType.getKey());
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where group_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }


    @Override
    public RowPager<TeamBuyGroupInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and group_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and group_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and group_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and group_staffid = ? ");
        }

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and group_business_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and group_status = ? ");
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
        select.append(" order by group_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<TeamBuyGroupInfo> list = mSlaveJdbcService.queryForList(select.toString(), TeamBuyGroupInfo.class, values.toArray());
        RowPager<TeamBuyGroupInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<TeamBuyGroupInfo> queryListByUser(DateTime fromTime, DateTime toTime, long userid, TeamBusinessType businessType, int limit)
    {
        String from = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String to = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where group_createtime between ? and ? and group_userid = ? and group_business_type = ? order by group_id desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, TeamBuyGroupInfo.class, from, to, userid, businessType.getKey());
    }

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<TeamBuyGroupInfo> callback)
    {
        String from = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String to = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where group_createtime between ? and ? ";
        mSlaveJdbcService.queryAll(callback, sql, TeamBuyGroupInfo.class, from, to);
    }


}

package com.inso.modules.web.team.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class TeamBuyRecordDaoMysql extends DaoSupport implements TeamBuyRecordDao {

    /**
     record_id                             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_groupid                     int(11) UNSIGNED NOT NULL comment '参加活动的领取ID' ,
     record_userid                         int(11) NOT NULL comment '',
     record_username                       varchar(255) NOT NULL comment  '',

     record_agentid                        int(11) NOT NULL comment '',
     record_agentname                      varchar(255) NOT NULL comment  '',
     record_staffid                        int(11) NOT NULL comment '',
     record_staffname                      varchar(255) NOT NULL comment  '',

     record_status                         varchar(20) NOT NULL comment 'waiting' comment '状态',
     record_createtime                     datetime NOT NULL comment '创建时间',
     record_endtime                        datetime NOT NULL comment '结束时间',
     record_remark                         varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_web_team_buying_group_record";

    @Override
    public long add(long groupid, TeamBusinessType businessType, BigDecimal realInvesAmount, UserAttr userAttr, ICurrencyType currencyType, OrderTxStatus txStatus)
    {
        DateTime date = new DateTime();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("record_groupid", groupid);
        keyvalue.put("record_business_type", businessType.getKey());
        keyvalue.put("record_currency_type", currencyType.getKey());

        keyvalue.put("record_real_inves_amount", realInvesAmount);


        keyvalue.put("record_userid", userAttr.getUserid());
        keyvalue.put("record_username", userAttr.getUsername());
        keyvalue.put("record_agentid", userAttr.getAgentid());
        keyvalue.put("record_agentname", userAttr.getAgentname());
        keyvalue.put("record_staffid", userAttr.getDirectStaffid());
        keyvalue.put("record_staffname", userAttr.getDirectStaffname());

        keyvalue.put("record_status", txStatus.getKey());
        keyvalue.put("record_createtime", date.toDate());
        keyvalue.put("record_endtime", date.plusDays(1).toDate());
        keyvalue.put("record_remark", StringUtils.getEmpty());

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(long id, BigDecimal realInvesAmount, OrderTxStatus txStatus)
    {
        String sql = "update " + TABLE + " set record_status = ?, record_real_inves_amount = ? where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, txStatus.getKey(), realInvesAmount, id);
    }

    public TeamBuyRecordInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where record_id = ?";
        return mSlaveJdbcService.queryForObject(sql, TeamBuyRecordInfo.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    @Override
    public RowPager<TeamBuyRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and record_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and record_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and record_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and record_staffid = ? ");
        }


        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and record_status = ? ");
        }

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and record_business_type = ? ");
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
        select.append(" order by record_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<TeamBuyRecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), TeamBuyRecordInfo.class, values.toArray());
        RowPager<TeamBuyRecordInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<TeamBuyRecordInfo> queryListByUser(DateTime fromTime, DateTime toTime, long userid, TeamBusinessType businessType, int limit)
    {
        String from = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String to = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String sql = "select * from " + TABLE + " where record_createtime between ? and ? and record_userid = ? and record_business_type = ? limit " + limit;
        return mSlaveJdbcService.queryForList(sql, TeamBuyRecordInfo.class, from, to, userid, businessType.getKey());
    }

    public List<TeamBuyRecordInfo> queryListByGroup(long groupid)
    {
        String sql = "select * from " + TABLE + " where record_groupid = ?" ;
        return mSlaveJdbcService.queryForList(sql, TeamBuyRecordInfo.class, groupid);
    }



}

package com.inso.modules.web.team.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamConfigInfo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class TeamBuyingConfigDaoMysql extends DaoSupport implements TeamBuyingConfigDao {

    /**
     config_id                           int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     config_agentid                      int(11) NOT NULL DEFAULT 0 comment '',
     config_agentname                    varchar(255) NOT NULL comment  '',

     config_business_type                varchar(255) NOT NULL comment  '',
     config_level                        int(11) NOT NULL DEFAULT 1 comment '当前团队等级',
     config_currency_type                varchar(255) NOT NULL comment  '',

     config_limit_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '需要最低投资多少金额',
     config_limit_min_invite_count       int(11) NOT NULL comment '需要邀请总人数',

     config_return_rate                  decimal(25,8) NOT NULL DEFAULT 0 comment '返回比例',

     config_status                       varchar(20) NOT NULL comment '状态',
     config_createtime                   datetime NOT NULL comment '创建时间',
     config_remark                       varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    public static final String TABLE = "inso_web_team_buying_level_config";

    @Override
    public long add(UserInfo agentInfo, TeamBusinessType businessType, ICurrencyType currency, BigDecimal limitValidRechargeAmount, long level, BigDecimal limitMinAmount, long limitMinInviteCount,
                    String returnCreatorRate, Status status, BigDecimal returnJoinRate)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        if(agentInfo != null)
        {
            keyvalue.put("config_agentid", agentInfo.getId());
            keyvalue.put("config_agentname", agentInfo.getName());
        }
        else
        {
            keyvalue.put("config_agentid", 0);
            keyvalue.put("config_agentname", StringUtils.getEmpty());
        }

        keyvalue.put("config_business_type", businessType.getKey());
        keyvalue.put("config_currency_type", currency.getKey());
        keyvalue.put("config_level", level);


        keyvalue.put("config_limit_balance_amount", BigDecimalUtils.getNotNull(limitValidRechargeAmount));
        keyvalue.put("config_limit_min_amount", limitMinAmount);
        keyvalue.put("config_limit_min_invite_count", limitMinInviteCount);
        keyvalue.put("config_return_creator_rate", returnCreatorRate);
        keyvalue.put("config_return_join_rate", returnJoinRate);

        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, String returnCreatorRate, BigDecimal returnJoinRate, BigDecimal minAmount, long limitInviteCount, BigDecimal limitValidRechargeAmount, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(returnCreatorRate != null)
        {
            setKeyValue.put("config_return_creator_rate", returnCreatorRate);
        }

        if(returnJoinRate != null)
        {
            setKeyValue.put("config_return_join_rate", returnJoinRate);
        }

        if(limitValidRechargeAmount != null)
        {
            setKeyValue.put("config_limit_balance_amount", limitValidRechargeAmount);
        }

        if(limitInviteCount > 0)
        {
            setKeyValue.put("config_limit_min_invite_count", limitInviteCount);
        }

        if(minAmount != null)
        {
            setKeyValue.put("config_limit_min_amount", minAmount);
        }

        if(status != null)
        {
            setKeyValue.put("config_status", status.getKey());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("config_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public TeamConfigInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, TeamConfigInfo.class, id);
    }


    @Override
    public RowPager<TeamConfigInfo> queryScrollPage(PageVo pageVo, long agentid, TeamBusinessType businessType, ICurrencyType currency, Status status)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and config_agentid = ? ");
        }

        if(currency != null)
        {
            values.add(currency.getKey());
            whereSQLBuffer.append(" and config_currency_type = ? ");
        }

        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and config_business_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and config_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by config_id asc, config_level asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<TeamConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), TeamConfigInfo.class, values.toArray());
        RowPager<TeamConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<TeamConfigInfo> getList(long agentid, TeamBusinessType businessType)
    {
        String sql = "select * from " + TABLE + " where config_agentid = ? and config_business_type = ? and config_status = ? order by config_level asc ";
        return mSlaveJdbcService.queryForList(sql, TeamConfigInfo.class, agentid, businessType.getKey(), Status.ENABLE.getKey());
    }



}

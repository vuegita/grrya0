package com.inso.modules.passport.user.service.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.SystemFollowType;
import com.inso.modules.passport.user.model.UserInfo;

@Repository
public class SystemFollowDaoMysql extends DaoSupport implements SystemFollowDao {


    private static String TABLE = "inso_passport_user_system_follow";


    public void add(long userid, long agentid,long staffid, SystemFollowType type, String remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("follow_userid", userid);
        keyvalue.put("follow_agentid", agentid);
        keyvalue.put("follow_staffid", staffid);
        keyvalue.put("follow_type", type.getKey());
        keyvalue.put("follow_remark", StringUtils.getNotEmpty(remark));
        persistent(TABLE, keyvalue);
    }

    public void delete(long userid)
    {
        String sql = "delete from " + TABLE + " where follow_userid = ?";
        mWriterJdbcService.executeUpdate(sql, userid);
    }


    @Override
    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, SystemFollowType type)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("");

        whereSQLBuffer.append(" where 1 = 1");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and follow_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and follow_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and follow_staffid = ? ");
        }

        if(type!= null)
        {
            values.add(type.getKey());
            whereSQLBuffer.append(" and follow_type = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_system_follow " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }
//        C.money_balance as user_balance, C.money_freeze as user_freeze
        StringBuilder select = new StringBuilder("select A.follow_remark as user_remark, B.* ");
        select.append(" , D.account_address as user_coin_address ,D.account_network_type as user_network_type ");
        select.append(" from inso_passport_user_system_follow as A ");
        select.append(" inner join inso_passport_user as B on A.follow_userid = B.user_id ");
//        select.append(" left join inso_passport_user_money as C on A.follow_userid=C.money_userid ");
        select.append(" left join inso_coin_user_third_account2 as D on A.follow_userid=D.account_userid ");

        select.append(whereSQL);
        select.append(" order by A.follow_userid desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserInfo.class, values.toArray());
        RowPager<UserInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

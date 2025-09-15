package com.inso.modules.passport.user.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class UserVIPDaoMysql extends DaoSupport implements UserVIPDao {

    /**
     uv_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     uv_userid       		int(11) UNSIGNED NOT NULL ,
     uv_vip_type       	varchar(50) NOT NULL comment 'vip 类型,检举类型',
     uv_vipid       		int(11) UNSIGNED NOT NULL ,
     uv_status             varchar(50) NOT NULL comment 'enable|disable',
     uv_expires_time       datetime DEFAULT NULL comment '过期时间-保留参数',
     uv_createtime         datetime DEFAULT NULL comment '时间',
     */
    private static final String TABLE = "inso_passport_user_vip";

    @Override
    public void add(UserAttr userAttr, VIPInfo vipInfo, Status status)
    {
        Date date = new Date();
        DateTime dateTime = new DateTime(date);
        DateTime monthTime = dateTime.plusMonths(3);

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("uv_userid", userAttr.getUserid());
        keyvalue.put("uv_username", userAttr.getUsername());
        keyvalue.put("uv_agentid", userAttr.getAgentid());
        keyvalue.put("uv_agentname", userAttr.getAgentname());
        keyvalue.put("uv_staffid", userAttr.getDirectStaffid());
        keyvalue.put("uv_staffname", userAttr.getDirectStaffname());

        keyvalue.put("uv_vip_type", vipInfo.getType());
        keyvalue.put("uv_vipid", vipInfo.getId());
        keyvalue.put("uv_status", status.getKey());
        keyvalue.put("uv_begintime", date);
        keyvalue.put("uv_expirestime", monthTime.toDate());
        keyvalue.put("uv_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, VIPInfo vipInfo, Date expiresTime)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("uv_status", status.getKey());
        }

        if(expiresTime != null)
        {
            setKeyValue.put("uv_expirestime", expiresTime);
        }

        if(vipInfo != null)
        {
            //setKeyValue.put("uv_vip_type", vipInfo.getType());
            setKeyValue.put("uv_vipid", vipInfo.getId());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("uv_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public UserVIPInfo findById(long id)
    {
        StringBuilder sql = new StringBuilder("select A.*, B.vip_name as uv_vip_name, B.vip_level as uv_vip_level from " + TABLE).append(" as A");
        sql.append(" left join inso_web_vip as B on A.uv_vipid = B.vip_id ");
        sql.append(" where uv_id = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), UserVIPInfo.class, id);
    }

    public UserVIPInfo findByUserId(long userid, VIPType vipType)
    {
        StringBuilder sql = new StringBuilder("select A.*, B.vip_name as uv_vip_name, B.vip_level as uv_vip_level from " + TABLE).append(" as A");
        sql.append(" left join inso_web_vip as B on A.uv_vipid = B.vip_id ");
        sql.append(" where uv_userid = ? and uv_vip_type = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), UserVIPInfo.class, userid, vipType.getKey());
    }

    @Override
    public RowPager<UserVIPInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, VIPType vipType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_web_vip as B on A.uv_vipid = B.vip_id ");
        whereSQLBuffer.append(" left join inso_ad_vip_limit_withdrawl as C on A.uv_userid = C.withdrawl_userid ");
        whereSQLBuffer.append("inner join inso_passport_user_money as D on D.money_userid = A.uv_userid and D.money_currency = ?");
        whereSQLBuffer.append(" where 1 = 1 ");

        values.add(ICurrencyType.getSupportCurrency().getKey());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and uv_userid = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and uv_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and uv_status = ? ");
            }

            if(agentid > 0)
            {
                values.add(agentid);
                whereSQLBuffer.append(" and uv_agentid = ? ");
            }

            if(staffid > 0)
            {
                values.add(staffid);
                whereSQLBuffer.append(" and uv_staffid = ? ");
            }

            if(vipType != null)
            {
                values.add(vipType.getKey());
                whereSQLBuffer.append(" and uv_vip_type = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        select.append("select A.*,(D.money_total_withdraw-D.money_total_refund) as uv_total_withdraw ,D.money_total_recharge as uv_total_recharge , D.money_balance as uv_balance, B.vip_name as uv_vip_name, B.vip_level as uv_vip_level, C.withdrawl_amount as uv_withdrawl_amount from ");
        select.append(whereSQL);
        select.append(" order by uv_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserVIPInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserVIPInfo.class, values.toArray());
        RowPager<UserVIPInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

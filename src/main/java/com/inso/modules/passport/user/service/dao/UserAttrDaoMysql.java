package com.inso.modules.passport.user.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.report.service.dao.UserReportDaoMysql;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserLevel;

@Repository
public class UserAttrDaoMysql extends DaoSupport implements UserAttrDao {

    public static final String TABLE = "inso_passport_user_attr";

    public void addAttr(long userid, String username, String directStaffName, long directStaffid)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("attr_userid", userid);
        keyvalue.put("attr_username", username);
        keyvalue.put("attr_direct_staffname", StringUtils.getNotEmpty(directStaffName));
        keyvalue.put("attr_direct_staffid", directStaffid);
        keyvalue.put("attr_level", UserLevel.NORMAL.getKey());
        persistent(TABLE, keyvalue);
    }

    public void updateStaff(long userid, String staffName, long staffid, String agentName, long agentId)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(staffName))
        {
            setKeyValue.put("attr_direct_staffid", staffid);
            setKeyValue.put("attr_direct_staffname", staffName);
        }
        if(!StringUtils.isEmpty(agentName))
        {
            setKeyValue.put("attr_agentid", agentId);
            setKeyValue.put("attr_agentname", agentName);
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("attr_userid", userid);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    /**
     * 绑定祖先信息
     * @param userid
     * @param staffName
     * @param staffid
     * @param parentName
     * @param parentid
     * @param grantFather
     * @param grantFatherid
     */
    public void bindAncestorInfo(long userid, String staffName, long staffid, String parentName, long parentid, String grantFather, long grantFatherid, String agentName, long agentId)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(staffid > 0 && !StringUtils.isEmpty(staffName))
        {
            setKeyValue.put("attr_direct_staffid", staffid);
            setKeyValue.put("attr_direct_staffname", staffName);
        }

        if(parentid > 0 && !StringUtils.isEmpty(parentName))
        {
            setKeyValue.put("attr_parentid", parentid);
            setKeyValue.put("attr_parentname", parentName);
        }

        if(grantFatherid > 0 && !StringUtils.isEmpty(grantFather) )
        {
            setKeyValue.put("attr_grantfatherid", grantFatherid);
            setKeyValue.put("attr_grantfathername", grantFather);
        }

        if(agentId > 0 && !StringUtils.isEmpty(agentName))
        {
            setKeyValue.put("attr_agentid", agentId);
            setKeyValue.put("attr_agentname", agentName);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("attr_userid", userid);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateLevelAndRemark(long userid, UserLevel level, String remark)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(level != null)
        {
            setKeyValue.put("attr_level", level.getKey());
        }

        if(!StringUtils.isEmpty(remark))
        {
            setKeyValue.put("attr_remark", remark);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("attr_userid", userid);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateReturn(long userid, BigDecimal returnLv1Rate, BigDecimal returnLv2Rate, Status returnLevelStatus, BigDecimal receivLv1Rate, BigDecimal receivLv2Rate)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(returnLv1Rate != null)
        {
            setKeyValue.put("attr_return_lv1_rate", returnLv1Rate);
        }
        if(returnLv2Rate != null)
        {
            setKeyValue.put("attr_return_lv2_rate", returnLv2Rate);
        }

        if(returnLevelStatus != null)
        {
            setKeyValue.put("attr_return_level_status", returnLevelStatus.getKey());
        }

        if(receivLv1Rate != null)
        {
            setKeyValue.put("attr_receiv_lv1_rate", receivLv1Rate);
        }
        if(receivLv2Rate != null)
        {
            setKeyValue.put("attr_receiv_lv2_rate", receivLv2Rate);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("attr_userid", userid);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateFirstRechargeOrderno(long userid, String orderno, BigDecimal amount)
    {
        Date date = new Date();
        String sql = "update " + TABLE + " set attr_first_recharge_orderno = ?, attr_first_recharge_time = ?, attr_first_recharge_amount = ? where attr_userid = ?";
        mWriterJdbcService.executeUpdate(sql, orderno, date, amount, userid);
    }

    public void updateInviteFriendTotalAmount(long userid, BigDecimal amount)
    {
        String sql = "update " + TABLE + " set attr_invite_friend_total_amount = attr_invite_friend_total_amount + ? where attr_userid = ?";
        mWriterJdbcService.executeUpdate(sql, amount, userid);
    }


    public UserAttr find(long userid)
    {
        String sql = "select A.*, B.user_type as attr_user_type from " + TABLE + " as A left join inso_passport_user as B on A.attr_userid = B.user_id where attr_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, UserAttr.class, userid);
    }

    @Override
    public void queryAllMember(Date startTime, Date endTime, Callback<UserAttr> callback)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("select A.*,B.user_createtime as attr_regtime,C.money_balance as attr_balance from " + TABLE + " as A ");
        buffer.append(" inner join inso_passport_user as B on B.user_id = A.attr_userid ");

        //
        buffer.append(" inner join inso_passport_user_money as C on C.money_userid = A.attr_userid and C.money_currency = ? ");
        if(startTime != null && endTime != null)
        {
            buffer.append(" where B.user_createtime between ? and ? and B.user_type = 'member'");
            mSlaveJdbcService.queryAll(true, callback, buffer.toString(), UserAttr.class, ICurrencyType.getSupportCurrency().getKey(), startTime, endTime);
        }
        else
        {
            buffer.append(" where B.user_type = 'member'");
            mSlaveJdbcService.queryAll(true, callback, buffer.toString(), UserAttr.class, ICurrencyType.getSupportCurrency().getKey());
        }
    }

    public void queryAllMemberByUserReport(DateTime startTime, DateTime endTime, Callback<UserAttr> callback)
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("select A.*,B.user_createtime as attr_regtime,C.money_balance as attr_balance from " + TABLE + " as A ");
        buffer.append(" inner join inso_passport_user as B on B.user_id = A.attr_userid ");
        //
        buffer.append(" inner join inso_passport_user_money as C on C.money_userid = A.attr_userid and C.money_currency = ? ");
        buffer.append(" inner join " + UserReportDaoMysql.TABLE + " as D on D.day_userid = A.attr_userid ");

        buffer.append(" where D.day_pdate between ? and ? and B.user_type = 'member'");

        String startStr = startTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endStr = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        mSlaveJdbcService.queryAll(callback, buffer.toString(), UserAttr.class, ICurrencyType.getSupportCurrency().getKey(), startStr, endStr);
    }


    @Override
    public RowPager<UserAttr> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_passport_user as B on B.user_id = A.attr_userid and B.user_type = 'member' ");
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.attr_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and B.user_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and attr_userid = ? ");
        }
        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and attr_agentid = ? ");
        }
        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and attr_direct_staffid = ? ");
        }
        if(parentid > 0)
        {
            values.add(parentid);
            whereSQLBuffer.append(" and attr_parentid = ? ");
        }
        if(grantid > 0)
        {
            values.add(grantid);
            whereSQLBuffer.append(" and attr_grantfatherid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_attr as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select A.*, C.money_freeze as attr_freeze, C.money_code_amount as attr_code_amount, C.money_balance as attr_balance from inso_passport_user_attr as A ");
        select.append(whereSQL);
        select.append(" order by attr_userid desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserAttr> list = mSlaveJdbcService.queryForList(select.toString(), UserAttr.class, values.toArray());
        RowPager<UserAttr> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    @Override
    public UserAttr queryTotalRechargeAndwithdrawById( long id)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.attr_userid ");
        whereSQLBuffer.append(" where ");
        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append("  attr_parentid = ? ");
        }
        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append(" or attr_grantfatherid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();


        StringBuilder select = new StringBuilder("select  SUM(C.money_balance) as attr_balance, SUM(C.money_total_recharge) as attr_total_recharge,SUM(C.money_total_withdraw) as attr_total_withdraw,SUM(C.money_total_refund)  as attr_total_refund ");
        select.append(" from inso_passport_user_attr as A ");
        select.append(whereSQL);

        return  mSlaveJdbcService.queryForObject(select.toString(), UserAttr.class, values.toArray());
    }

    @Override
    public UserAttr queryTotalRechargeByParentid(long id)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.attr_userid ");
        whereSQLBuffer.append(" where ");
        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append("  attr_parentid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();


        StringBuilder select = new StringBuilder("select  SUM(C.money_balance) as attr_balance, SUM(C.money_total_recharge) as attr_total_recharge,SUM(C.money_total_withdraw) as attr_total_withdraw,SUM(C.money_total_refund)  as attr_total_refund ");
        select.append(" from inso_passport_user_attr as A ");
        select.append(whereSQL);

        return  mSlaveJdbcService.queryForObject(select.toString(), UserAttr.class, values.toArray());
    }

    @Override
    public RowPager<UserAttr> queryScrollPageOrderBy(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName, String sortOrder , String userName, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_passport_user as B on B.user_id = A.attr_userid  and (B.user_type = 'member' or B.user_type = 'test') ");
        whereSQLBuffer.append("inner join inso_passport_user as E on E.user_id = A.attr_agentid  and (B.user_type = 'member' or B.user_type = 'test') ");
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.attr_userid ");

        whereSQLBuffer.append("left join inso_passport_return_water_log_count as D on D.log_userid = A.attr_userid ");

//        whereSQLBuffer.append("left join (select day_userid,SUM(day_recharge) as day_recharge,SUM(day_withdraw) as day_withdraw,SUM(day_refund) as day_refund  from inso_report_passport_user_day group by day_username) as D on D.day_userid = A.attr_userid ");
        whereSQLBuffer.append(" where 1 = 1");

        // 时间放前面
        if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and B.user_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and attr_userid = ? ");
        }
        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and attr_agentid = ? ");
        }
        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and attr_direct_staffid = ? ");
        }
        if(parentid > 0)
        {
            values.add(parentid);
            whereSQLBuffer.append(" and attr_parentid = ? ");
        }
        if(grantid > 0)
        {
            values.add(grantid);
            whereSQLBuffer.append(" and attr_grantfatherid = ? ");
        }

        if(userName != null && ValidatorUtils.checkSqlUsername(userName)){
            //values.add(userName);
            whereSQLBuffer.append(" and attr_username like '%"+userName+"%'  ");
        }

        if(ristMoney != null)
        {
            // 禁用的就不会显示了
            values.add(Status.ENABLE.getKey());
            whereSQLBuffer.append(" and B.user_status = ? ");

            // 普通会员-
            values.add(MemberSubType.SIMPLE.getKey());
            whereSQLBuffer.append(" and B.user_sub_type = ? ");

            values.add(ristMoney);
            whereSQLBuffer.append(" and (C.money_total_withdraw - C.money_total_refund - C.money_total_recharge) >= ? ");
        }

        if(status!= null){
            values.add(status.getKey());
            whereSQLBuffer.append(" and E.user_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_attr as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select A.*, B.user_lastlogintime as attr_user_lastlogintime, B.user_remark as attr_user_remark, C.money_freeze as attr_freeze, C.money_code_amount as attr_code_amount, C.money_balance as attr_balance ");
//        D.day_recharge as attr_recharge,D.day_withdraw as attr_withdraw,D.day_refund as attr_refund,
        select.append(", C.money_total_recharge as attr_total_recharge, (C.money_total_withdraw-C.money_total_refund) as attr_total_withdraw, C.money_total_refund as attr_total_refund ");
        select.append(", C.money_limit_amount as attr_limit_amount, C.money_limit_code as attr_limit_code ");
        select.append(", C.money_currency as attr_currency ");
        select.append(", C.money_fund_key as attr_fund_key ");

        select.append(", D.log_level1_count  as attr_level1_count ");
        select.append(", D.log_level2_count  as attr_level2_count ");
        select.append(", B.user_createtime  as attr_regtime ");

        select.append(", B.user_type  as attr_user_type ");

        select.append(" from inso_passport_user_attr as A ");
        select.append(whereSQL);
        if(sortName!=null && sortOrder!=null){
           // select.append(" order by "+" C.money_"+sortName +" "+sortOrder);
            if(sortName.equals("balance")){
                select.append(" order by "+" C.money_"+sortName +" "+sortOrder);
            }else if(sortName.equals("totalRecharge")){
                select.append(" order by "+" C.money_total_recharge" +" "+sortOrder);
            }else if(sortName.equals("totalWithdraw")){
                select.append(" order by "+" attr_total_withdraw " +" "+sortOrder);
            }else if(sortName.equals("level1Count")){
                select.append(" order by "+" D.log_level1_count " +" "+sortOrder);
            }else if(sortName.equals("level2Count")){
                select.append(" order by "+" D.log_level2_count " +" "+sortOrder);
            }else if(sortName.equals("userLastlogintime")){
                select.append(" order by "+" B.user_lastlogintime " +" "+sortOrder);
            }

        }else{
            select.append(" order by attr_userid desc ");
        }

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserAttr> list = mSlaveJdbcService.queryForList(select.toString(), UserAttr.class, values.toArray());
        RowPager<UserAttr> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public RowPager<UserInfo> querySubMemberPageScrollWithStaffid(PageVo pageVo, long staffid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from inso_passport_user_attr as A  ");
        whereSQLBuffer.append(" inner join inso_passport_user as B on A.attr_userid = B.user_id ");
        whereSQLBuffer.append(" where A.attr_direct_staffid = ? ");

        values.add(staffid);

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select B.* ");
        select.append(whereSQL);
        select.append(" order by B.user_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserInfo.class, values.toArray());
        RowPager<UserInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    /**
     * 根据会员id，查询其对应所有的1|2级下级会员
     * @param callback
     * @param userid
     */
    public void queryAllSubMemberWithMemberid(Callback<UserAttr> callback, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("inner join inso_passport_user as B on B.user_id = A.attr_userid and B.user_type = 'member' ");
        whereSQLBuffer.append("inner join inso_passport_user_money as C on C.money_userid = A.attr_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        values.add(userid);
        values.add(userid);
        whereSQLBuffer.append(" and (attr_parentid = ? or attr_grantfatherid = ? )");


        String whereSQL = whereSQLBuffer.toString();

//        StringBuilder select = new StringBuilder("select A.*, C.money_freeze as attr_freeze, C.money_code_amount as attr_code_amount, C.money_balance as attr_balance from inso_passport_user_attr as A ");

        StringBuilder select = new StringBuilder("select A.*, C.money_freeze as attr_freeze, C.money_code_amount as attr_code_amount, C.money_balance as attr_balance  ");
//        D.day_recharge as attr_recharge,D.day_withdraw as attr_withdraw,D.day_refund as attr_refund,
        select.append(", C.money_total_recharge as attr_total_recharge, C.money_total_withdraw as attr_total_withdraw, C.money_total_refund as attr_total_refund");
        select.append(" from inso_passport_user_attr as A ");

        select.append(whereSQL);

        mSlaveJdbcService.queryAll(callback, select.toString(), UserAttr.class, values.toArray());
    }

}

package com.inso.modules.report.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.passport.user.service.dao.UserAttrDaoMysql;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.MemberReport;

@Repository
public class UserReportDaoMysql extends DaoSupport implements UserReportDao {

    public static String TABLE = "inso_report_passport_user_day";

    public void addReport(FundAccountType accountType, ICurrencyType currencyType, long userid, String username, Date createtime)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_userid", userid);
        keyvalue.put("day_username", username);

        keyvalue.put("day_fund_key", accountType.getKey());
        keyvalue.put("day_currency", currencyType.getKey());

        keyvalue.put("day_pdate", createtime);
        persistent(TABLE, keyvalue);
    }

    /**
     *
     *   day_recharge      		decimal(18,2) NOT NULL DEFAULT 0 comment '充值金额',
     *   day_refund		        decimal(18,2) NOT NULL DEFAULT 0 comment '退款金额-保留字段',
     *   day_withdraw      		decimal(18,2) NOT NULL DEFAULT 0 comment '提现金额',
     *   day_business_recharge    	decimal(18,2) NOT NULL DEFAULT 0 comment '业务充值-如中奖',
     *   day_business_deduct      	decimal(18,2) NOT NULL DEFAULT 0 comment '业务扣款-如投注',
     *   day_business_feemoney     decimal(18,2) NOT NULL DEFAULT 0 comment '投注手续费',
     *
     *   day_finance_recharge    	decimal(18,2) NOT NULL DEFAULT 0 comment '金额充值-如理财收益',
     *   day_finance_deduct      	decimal(18,2) NOT NULL DEFAULT 0 comment '金额扣款-如购买理财',
     *   day_finance_feemoney      decimal(18,2) NOT NULL DEFAULT 0 comment '金额手续费-手续费用',
     *
     *   day_platform_recharge     decimal(18,2) NOT NULL DEFAULT 0 comment '平台充值',
     *   day_platform_presentation decimal(18,2) NOT NULL DEFAULT 0 comment '平台赠送',
     *   day_platform_deduct       decimal(18,2) NOT NULL DEFAULT 0 comment '平台扣款',
     *   day_feemoney             	decimal(18,2) NOT NULL DEFAULT 0 comment '手续费-提现才有',
     *   day_return_water          decimal(18,2) NOT NULL DEFAULT 0 comment '返佣金额',
     * @param report
     */

    public void updateReport(Date pdate, MemberReport report)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("day_recharge", report.getRecharge());
        setKeyValue.put("day_withdraw", report.getWithdraw());
        setKeyValue.put("day_refund", report.getRefund());
        setKeyValue.put("day_feemoney", report.getFeemoney());

        setKeyValue.put("day_business_recharge", report.getBusinessRecharge());
        setKeyValue.put("day_business_deduct", report.getBusinessDeduct());
        setKeyValue.put("day_business_feemoney", report.getBusinessFeemoney());

        setKeyValue.put("day_platform_recharge", report.getPlatformRecharge());
        setKeyValue.put("day_platform_presentation", report.getPlatformPresentation());
        setKeyValue.put("day_platform_deduct", report.getPlatformDeduct());

        setKeyValue.put("day_finance_recharge", report.getFinanceRecharge());
        setKeyValue.put("day_finance_deduct", report.getFinanceDeduct());
        setKeyValue.put("day_finance_feemoney", report.getFinanceFeemoney());

        setKeyValue.put("day_return_water", report.getReturnWater());

        LinkedHashMap<String, Object> whereValue = Maps.newLinkedHashMap();
        whereValue.put("day_pdate", pdate);
        whereValue.put("day_userid", report.getUserid());
        whereValue.put("day_fund_key", report.getFundKey());
        whereValue.put("day_currency", report.getCurrency());

        update(TABLE, setKeyValue, whereValue);
    }

    public void delete(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateRefund(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney)
    {
        if(feemoney == null)
        {
            feemoney = BigDecimal.ZERO;
        }
        String sql = "update " + TABLE + " set day_refund = day_refund + ?, day_feemoney = day_feemoney - ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, feemoney, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateUserRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_recharge = day_recharge + ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateUserWithdraw(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney)
    {
        String sql = "update " + TABLE + " set day_withdraw = day_withdraw + ?, day_feemoney = day_feemoney + ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, feemoney, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updatePlatformRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_platform_recharge = day_platform_recharge + ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updatePlatformPresentation(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_platform_presentation = day_platform_presentation + ? where day_pdate = ? and day_userid = ?  and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updatePlatformDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_platform_deduct = day_platform_deduct + ? where day_pdate = ? and day_userid = ?  and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateBusinessRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_business_recharge = day_business_recharge + ? where day_pdate = ? and day_userid = ?  and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateBusinessDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney)
    {
        if(feemoney == null)
        {
            feemoney = BigDecimal.ZERO;
        }
        String sql = "update " + TABLE + " set day_business_deduct = day_business_deduct + ?, day_business_feemoney = day_business_feemoney + ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, feemoney, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    @Override
    public void updateReturnWater(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money) {
        String sql = "update " + TABLE + " set day_return_water = day_return_water + ? where day_pdate = ? and day_userid = ?  and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateFinanceRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money)
    {
        String sql = "update " + TABLE + " set day_finance_recharge = day_finance_recharge + ? where day_pdate = ? and day_userid = ?  and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateFinanceDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney)
    {
        if(feemoney == null)
        {
            feemoney = BigDecimal.ZERO;
        }
        String sql = "update " + TABLE + " set day_finance_deduct = day_finance_deduct + ?, day_finance_feemoney = day_finance_feemoney + ? where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?";
        mWriterJdbcService.executeUpdate(sql, money, feemoney, pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public void updateSubLevel(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, UserLevelStatusInfo data)
    {
        StringBuilder sql = new StringBuilder("update ").append(TABLE);
        sql.append(" set ");
        sql.append(" day_lv1_recharge = ?, ");
        sql.append(" day_lv1_withdraw = ?, ");
        sql.append(" day_lv2_recharge = ?, ");
        sql.append(" day_lv2_withdraw = ? ");

        sql.append(" where day_pdate = ? and day_userid = ? and day_fund_key = ? and day_currency = ?");
        mWriterJdbcService.executeUpdate(sql.toString(), data.getLv1RechargeAmount(), data.getLv1WithdrawAmount(), data.getLv2RechargeAmount(), data.getLv2WithdrawAmount(),
                pdate, userid, accountType.getKey(), currencyType.getKey());
    }

    public MemberReport findAllHistoryReportByUserid(long userid)
    {
        StringBuilder select = new StringBuilder("select ");
        select.append("day_username");
        addSumColumn(select, "day_recharge");
        addSumColumn(select, "day_withdraw");
        addSumColumn(select, "day_feemoney");
        addSumColumn(select, "day_refund");
        addSumColumn(select, "day_business_recharge");
        addSumColumn(select, "day_business_deduct");
        addSumColumn(select, "day_business_feemoney");
        addSumColumn(select, "day_platform_recharge");
        addSumColumn(select, "day_platform_deduct");
        addSumColumn(select, "day_platform_presentation");
        addSumColumn(select, "day_return_water");
        select.append(" from inso_report_passport_user_day where day_userid = ? group by day_username");

        return mSlaveJdbcService.queryForObject(select.toString(), MemberReport.class, userid);
    }

    public MemberReport queryHistoryReportByUser(DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        String fromTimeStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        StringBuilder select = new StringBuilder("select ");
        select.append("day_username");
        addSumColumn(select, "day_recharge");
        addSumColumn(select, "day_withdraw");
        addSumColumn(select, "day_feemoney");
        addSumColumn(select, "day_refund");
        addSumColumn(select, "day_business_recharge");
        addSumColumn(select, "day_business_deduct");
        addSumColumn(select, "day_business_feemoney");
        addSumColumn(select, "day_platform_recharge");
        addSumColumn(select, "day_platform_deduct");
        addSumColumn(select, "day_platform_presentation");
        addSumColumn(select, "day_return_water");
        select.append(" from inso_report_passport_user_day where day_pdate  between ? and ? and day_userid = ? and day_fund_key = ? and day_currency = ? group by day_username ");

        return mSlaveJdbcService.queryForObject(select.toString(), MemberReport.class, fromTimeStr, toTimeStr, userid, accountType.getKey(), currencyType.getKey());
    }

    public List<MemberReport> queryByUserAndTime(DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        String fromTimeStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        StringBuilder select = new StringBuilder("select * from ");
        select.append(TABLE);
        select.append(" where day_pdate  between ? and ? and day_userid = ? and day_fund_key = ? and day_currency = ? limit 10");

        return mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, fromTimeStr, toTimeStr, userid, accountType.getKey(), currencyType.getKey());
    }

    public void queryAllMemberReport(String startTime, String endTime, Callback<MemberReport> callback)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder buffer = new StringBuilder("select * from " + TABLE + " as A");
        buffer.append(" inner join inso_passport_user as B on A.day_userid = B.user_id and B.user_type = 'member'");
        buffer.append(" where 1 = 1 ");
        if(!StringUtils.isEmpty(startTime))
        {
            buffer.append(" and day_pdate >= ? ");
            values.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            buffer.append(" and day_pdate <= ? ");
            values.add(endTime);
        }
        mSlaveJdbcService.queryAll(callback, buffer.toString(), MemberReport.class, values.toArray());
    }

    public void queryAllMemberReportByUserId(String startTime, String endTime, long agentid, long staffid, Callback<MemberReport> callback)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder buffer = new StringBuilder("select * from " + TABLE + " as A");
        buffer.append(" inner join inso_passport_user as B on A.day_userid = B.user_id and B.user_type = 'member'");
        buffer.append(" inner join inso_passport_user_attr as C on A.day_userid = C.attr_userid ");
        buffer.append(" where 1 = 1 ");
        if(!StringUtils.isEmpty(startTime))
        {
            buffer.append(" and day_pdate >= ? ");
            values.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            buffer.append(" and day_pdate <= ? ");
            values.add(endTime);
        }

        if(agentid > 0)
        {
            buffer.append(" and C.attr_agentid = ? ");
            values.add(agentid);

        }
        if(staffid > 0)
        {
            buffer.append(" and C.attr_direct_staffid = ? ");
            values.add(staffid);
        }

        mSlaveJdbcService.queryAll(callback, buffer.toString(), MemberReport.class, values.toArray());
    }

    public List<MemberReport> queryAgentDataListByWebApi(DateTime fromTime, DateTime toTime, long userid, UserInfo.UserType userType, int limit)
    {
        String startTimeStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toTimeStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder select = new StringBuilder();
        select.append("select ");

        if(userType == UserInfo.UserType.AGENT)
        {
            select.append(" day_currency, day_pdate ");
            addSumColumn(select, "day_recharge");
            addSumColumn(select, "day_withdraw");
            addSumColumn(select, "day_feemoney");
            addSumColumn(select, "day_refund");
            addSumColumn(select, "day_business_recharge");
            addSumColumn(select, "day_business_deduct");
            addSumColumn(select, "day_business_feemoney");
            addSumColumn(select, "day_platform_recharge");
            addSumColumn(select, "day_platform_deduct");
            addSumColumn(select, "day_platform_presentation");
            addSumColumn(select, "day_return_water");

            select.append(" from ").append(TABLE).append(" as A");

            select.append(" inner join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.day_userid = B.attr_userid ");
            select.append(" where day_pdate between ? and ? and B.attr_agentid = ? and attr_direct_staffid = 0");
            select.append(" group by day_pdate, day_currency ");
        }
        else
        {
            select.append(" * from ").append(TABLE).append(" as A");
            select.append(" where day_pdate between ? and ? and A.day_userid = ?");
        }

        select.append(" limit ").append(limit);
        return mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, startTimeStr, toTimeStr, userid);
    }

    public RowPager<MemberReport> queryScrollPage(PageVo pageVo, long userid, UserInfo.UserType userType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" inner join inso_passport_user as B on B.user_id=A.day_userid and B.user_type=? where 1 = 1");

        values.add(userType.getKey());

        // 时间放前面
        whereSQLBuffer.append(" and A.day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.day_userid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        // A.*, C.money_balance as day_balance 查询实时余额
        select.append("select A.* from ");
        select.append(TABLE);

        select.append( " as A");

        // 实时余额需要
//        select.append(" inner join inso_passport_user_money as C on C.money_userid = A.day_userid ");

        select.append(whereSQL);
        select.append(" order by day_pdate desc ");

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MemberReport> list = mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, values.toArray());
        RowPager<MemberReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    public RowPager<MemberReport> queryScrollPageBySuperiorId(PageVo pageVo, long userid,  long parentid, long grantid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" inner join inso_passport_user as B on B.user_id=A.day_userid and B.user_type = 'member' ");
        whereSQLBuffer.append("inner join inso_passport_user_attr as C on C.attr_userid  = A.day_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and A.day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.day_userid = ? ");
        }

        if(parentid > 0 && grantid > 0)
        {
            values.add(parentid);
            whereSQLBuffer.append(" and (C.attr_parentid = ? ");

            values.add(grantid);
            whereSQLBuffer.append(" or C.attr_grantfatherid = ? )");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        // A.*, C.money_balance as day_balance 查询实时余额
        select.append("select A.* from ");
        select.append(TABLE);

        select.append( " as A");

        // 实时余额需要
//        select.append(" inner join inso_passport_user_money as C on C.money_userid = A.day_userid ");

        select.append(whereSQL);
        select.append(" order by day_pdate desc ");

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MemberReport> list = mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, values.toArray());
        RowPager<MemberReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    @Override
    public RowPager<MemberReport> queryAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from inso_report_passport_user_day as A ");

        whereSQLBuffer.append(" inner join inso_passport_user as B on B.user_id = A.day_userid ");
        whereSQLBuffer.append(" left join inso_passport_user_relation as C on C.relation_descendant = A.day_userid ");
        whereSQLBuffer.append(" inner join inso_passport_user as D on D.user_id = C.relation_ancestor and D.user_type='agent' ");

        whereSQLBuffer.append(" where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        // 此处表示有数据的员工数据
        whereSQLBuffer.append("  and B.user_type = 'staff' ");
//        if(userTypes != null)
//        {
//            whereSQLBuffer.append(" and (");
//            boolean first = true;
//            for(UserInfo.UserType userType : userTypes)
//            {
//                if(first)
//                {
//                    first = false;
//                }
//                else
//                {
//                    whereSQLBuffer.append(" or ");
//                }
//                whereSQLBuffer.append("  B.user_type = ? ");
//                values.add(userType.getKey());
//
//            }
//            whereSQLBuffer.append(" ) ");
//        }

        if(ancestorid > 0)
        {
            whereSQLBuffer.append("  and D.user_id = ? ");
            values.add(ancestorid);
        }

        whereSQLBuffer.append(" group by day_pdate, C.relation_ancestor ");

        String whereSQL = whereSQLBuffer.toString();
        String countsql = " select count(1) from ( select count(1) " + whereSQL + ") as ss";
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        select.append("select day_pdate, D.user_name as day_username ");
        addSumColumn(select, "day_recharge");
        addSumColumn(select, "day_withdraw");
        addSumColumn(select, "day_feemoney");
        addSumColumn(select, "day_refund");
        addSumColumn(select, "day_business_recharge");
        addSumColumn(select, "day_business_deduct");
        addSumColumn(select, "day_business_feemoney");
        addSumColumn(select, "day_platform_recharge");
        addSumColumn(select, "day_platform_deduct");
        addSumColumn(select, "day_platform_presentation");
        addSumColumn(select, "day_return_water");

        select.append(whereSQL);
        select.append(" order by day_pdate desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<MemberReport> list = mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, values.toArray());
        RowPager<MemberReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    /**
     * 根据祖先id查询下级
     * @param pageVo
     * @param ancestorid
     * @param userTypes
     * @return
     */
    @Override
    public RowPager<MemberReport> querySubAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from inso_passport_user_relation as A ");
        whereSQLBuffer.append(" inner join inso_passport_user as B on B.user_id = A.relation_descendant ");
        whereSQLBuffer.append(" where 1 = 1");

        if(ancestorid > 0)
        {
            values.add(ancestorid);
//            whereSQLBuffer.append(" and A.relation_ancestor = ? and A.relation_depth != 0 ");
            whereSQLBuffer.append(" and A.relation_ancestor = ? ");
        }

        if(userTypes != null)
        {
            whereSQLBuffer.append(" and (");
            boolean first = true;
            for(UserInfo.UserType userType : userTypes)
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    whereSQLBuffer.append(" or ");
                }
                whereSQLBuffer.append("  B.user_type = ? ");
                values.add(userType.getKey());

            }
            whereSQLBuffer.append(" ) ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(DISTINCT B.user_name ) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        select.append("select B.user_name as day_username ");
        addSumColumn(select, "day_recharge", pageVo, values);
        addSumColumn(select, "day_withdraw", pageVo, values);
        addSumColumn(select, "day_feemoney", pageVo, values);
        addSumColumn(select, "day_refund", pageVo, values);
        addSumColumn(select, "day_business_recharge", pageVo, values);
        addSumColumn(select, "day_business_deduct", pageVo, values);
        addSumColumn(select, "day_business_feemoney", pageVo, values);
        addSumColumn(select, "day_platform_recharge", pageVo, values);
        addSumColumn(select, "day_platform_deduct", pageVo, values);
        addSumColumn(select, "day_platform_presentation", pageVo, values);
        addSumColumn(select, "day_return_water", pageVo, values);

        select.append(whereSQL);
        select.append(" GROUP BY B.user_name ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());


        List<MemberReport> list = mSlaveJdbcService.queryForList(select.toString(), MemberReport.class, values.toArray());
        RowPager<MemberReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



    private void addSumColumn(StringBuilder sql, String column)
    {
        sql.append(", sum(");
        sql.append(column);
        sql.append(") ");
        sql.append(column);

    }

    private void addSumColumn(StringBuilder sql, String column, PageVo pageVo, List<Object> values)
    {
        // (select sum(C.day_recharge) from inso_passport_user_relation as tree2
        // left join inso_report_passport_user_day as C on C.day_userid = tree2.relation_descendant
        // where tree2.relation_ancestor = A.relation_descendant) as day_recharge
        sql.append(", (");
        sql.append(" select sum(D." + column + ") from inso_passport_user_relation as tree2 ");
        sql.append(" inner join inso_passport_user as C on C.user_id = tree2.relation_descendant ");
        sql.append(" left join inso_report_passport_user_day as D on D.day_userid = tree2.relation_descendant ");
        sql.append(" where tree2.relation_ancestor = A.relation_descendant and C.user_type = 'staff' ");
        sql.append(" and D.day_pdate between '" + pageVo.getFromTime() + "' and '" + pageVo.getToTime() + "' ");
        sql.append(") as ");
        sql.append(column);

    }
}

package com.inso.modules.ad.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class VipLimitDaoMysql extends DaoSupport implements VipLimitDao {

    /**
     *   limit_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   limit_vipid                  int(11) NOT NULL comment '',
     *   limit_total_money_of_day     decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚最大金额',
     *   limit_free_money_of_day      decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚免费任务金额',
     *   limit_invite_count_of_day    int(11) NOT NULL DEFAULT 0 comment '不免费的任务需要成功邀请好友才能接着往下做',
     *   limit_force_buy_vip          varchar(50) NOT NULL comment '对于邀请的好友是否强制要买vip才能算免费等到额度: enable|disable',
     *   limit_max_money_of_single    decimal(18,2) NOT NULL DEFAULT 0 comment '单笔可以做最大金额,不能超过免费额度1/5',
     *   limit_createtime             datetime DEFAULT NULL comment '创建时间',
     */
    private static final String TABLE = "inso_ad_vip_limit";

    @Override
    public void add(long vipid, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                    long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                    long buyCountOfDay, BigDecimal buyMoneyOfDay,
                    Status status, BigDecimal maxMoneyOfSingle)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("limit_vipid", vipid);

        keyvalue.put("limit_total_money_of_day", totalMoneyOfDay);
        keyvalue.put("limit_free_money_of_day", freeMoneyOfDay);
        keyvalue.put("limit_max_money_of_single", maxMoneyOfSingle);

        keyvalue.put("limit_invite_count_of_day", inviteCountOfDay);
        keyvalue.put("limit_invite_money_of_day", inviteMoneyOfDay);

        keyvalue.put("limit_buy_count_of_day", buyCountOfDay);
        keyvalue.put("limit_buy_money_of_day", buyMoneyOfDay);

        keyvalue.put("limit_status", status.getKey());
        keyvalue.put("limit_createtime", date);



        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                           long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                           long buyCountOfDay, BigDecimal buyMoneyOfDay,
                           BigDecimal maxMoneyOfSingle, long paybackPeriod, Status status,
                           BigDecimal lv1RebateBalanceRate, BigDecimal lv2RebateBalanceRate,
                           BigDecimal lv1WithdrawlRate, BigDecimal lv2WithdrawlRate)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(totalMoneyOfDay != null)
        {
            setKeyValue.put("limit_total_money_of_day", totalMoneyOfDay);
        }
        if(freeMoneyOfDay != null)
        {
            setKeyValue.put("limit_free_money_of_day", freeMoneyOfDay);
        }
        setKeyValue.put("limit_invite_count_of_day", inviteCountOfDay);
        setKeyValue.put("limit_invite_money_of_day", inviteMoneyOfDay);

        setKeyValue.put("limit_buy_count_of_day", buyCountOfDay);
        setKeyValue.put("limit_buy_money_of_day", buyMoneyOfDay);

        if(status != null)
        {
            setKeyValue.put("limit_status", status.getKey());
        }
        if(maxMoneyOfSingle != null)
        {
            setKeyValue.put("limit_max_money_of_single", maxMoneyOfSingle);
        }
        if(paybackPeriod > 0)
        {
            setKeyValue.put("limit_payback_period", paybackPeriod);
        }

        if(lv1RebateBalanceRate != null)
        {
            setKeyValue.put("limit_lv1_rebate_balance_rate", lv1RebateBalanceRate);
        }

        if(lv2RebateBalanceRate != null)
        {
            setKeyValue.put("limit_lv2_rebate_balance_rate", lv2RebateBalanceRate);
        }

        if(lv1WithdrawlRate != null)
        {
            setKeyValue.put("limit_lv1_rebate_withdrawl_rate", lv1WithdrawlRate);
        }
        if(lv2WithdrawlRate != null)
        {
            setKeyValue.put("limit_lv2_rebate_withdrawl_rate", lv2WithdrawlRate);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("limit_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AdVipLimitInfo findById(long id)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.vip_name as limit_vip_name, B.vip_level as limit_vip_level from ").append(TABLE).append(" as A ");
        sql.append(" left join inso_web_vip as B on A.limit_vipid = B.vip_id  ");
        sql.append(" where limit_id = ? ");
        return mSlaveJdbcService.queryForObject(sql.toString(), AdVipLimitInfo.class, id);
    }

    public AdVipLimitInfo findByVipId(long vipid)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.vip_name as limit_vip_name, B.vip_level as limit_vip_level from ").append(TABLE).append(" as A ");
        sql.append(" left join inso_web_vip as B on A.limit_vipid = B.vip_id  ");
        sql.append(" where limit_vipid = ? ");
        //String sql = "select * from " + TABLE + " where limit_vipid = ?";
        return mSlaveJdbcService.queryForObject(sql.toString(), AdVipLimitInfo.class, vipid);
    }

    @Override
    public RowPager<AdVipLimitInfo> queryScrollPage(PageVo pageVo, Status forceVipStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_web_vip as B on A.limit_vipid = B.vip_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(forceVipStatus != null)
        {
            values.add(forceVipStatus.getKey());
            whereSQLBuffer.append(" and limit_force_buy_vip = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*, B.vip_name as limit_vip_name, B.vip_level as limit_vip_level from ");
        select.append(whereSQL);
        select.append(" order by B.vip_level asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AdVipLimitInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdVipLimitInfo.class, values.toArray());
        RowPager<AdVipLimitInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    @Override
    public List<AdVipLimitInfo> queryAllEnable(VIPType type)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_web_vip as B on A.limit_vipid = B.vip_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        values.add(Status.ENABLE.getKey());
        whereSQLBuffer.append(" and A.limit_status = ? ");

        if(type!=null){
            values.add(type.getKey());
            whereSQLBuffer.append(" and B.vip_type = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();

        StringBuilder select = new StringBuilder("select A.*,B.vip_price limit_price, B.vip_name as limit_vip_name, B.vip_level as limit_vip_level from ");
        select.append(whereSQL);
        select.append(" order by B.vip_level asc ");

        List<AdVipLimitInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdVipLimitInfo.class, values.toArray());
        return list;
    }

}

package com.inso.modules.ad.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class WithdrawlLimitDaoMysql extends DaoSupport implements WithdrawlLimitDao {

    private static final String TABLE = "inso_ad_vip_limit_withdrawl";

    @Override
    public void add(UserInfo userInfo, BigDecimal amount)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("withdrawl_userid", userInfo.getId());
        keyvalue.put("withdrawl_username", userInfo.getName());
        keyvalue.put("withdrawl_amount", amount);
        keyvalue.put("withdrawl_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long userid, BigDecimal amount)
    {
        String sql = "update " + TABLE + " set withdrawl_amount = ? where withdrawl_userid = ?";
        mWriterJdbcService.executeUpdate(sql, amount, userid);
    }

    public WithdrawlLimitInfo findByUserId(long id)
    {
        String sql = "select * from " + TABLE + " where withdrawl_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, WithdrawlLimitInfo.class, id);
    }

    @Override
    public RowPager<WithdrawlLimitInfo> queryScrollPage(PageVo pageVo, Status status, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

//        if(status != null)
//        {
//            values.add(status.getKey());
//            whereSQLBuffer.append(" and category_status = ? ");
//        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and withdrawl_userid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by withdrawl_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<WithdrawlLimitInfo> list = mSlaveJdbcService.queryForList(select.toString(), WithdrawlLimitInfo.class, values.toArray());
        RowPager<WithdrawlLimitInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.passport.returnwater.service.dao;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.modules.passport.business.model.ReturnWaterLogDetail;

@Repository
public class ReturnWaterLogDetailDaoMysql extends DaoSupport implements ReturnWaterLogDetailDao {


    private static String TABLE = "inso_passport_return_water_log_detail";


    public void addLog(int level, UserInfo userInfo, FundAccountType accountType, ICurrencyType currencyType, UserInfo childUserInfo)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("detail_userid", userInfo.getId());
        keyvalue.put("detail_username", userInfo.getName());

        keyvalue.put("detail_fund_key", accountType.getKey());
        keyvalue.put("detail_currency", currencyType.getKey());

        keyvalue.put("detail_childid", childUserInfo.getId());
        keyvalue.put("detail_childname", childUserInfo.getName());
        keyvalue.put("detail_level", level);
        persistent(TABLE, keyvalue);
    }

    public void updateAmount(int level, long userid, long childid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(TABLE);
        sql.append(" set detail_amount = detail_amount + ?");
        sql.append(" where detail_userid = ? and detail_childid = ? and detail_fund_key = ? and detail_currency = ?  and detail_level = ?");
        mWriterJdbcService.executeUpdate(sql.toString(), amount, userid, childid, accountType.getKey(), currencyType.getKey(), level);
    }

    public ReturnWaterLogDetail findById(int level, long userid, long childid, FundAccountType accountType, ICurrencyType currencyType)
    {
        String sql = "select * from " + TABLE + " where detail_userid = ? and detail_childid = ? and detail_fund_key = ? and detail_currency = ?  and detail_level = ? ";
        return mSlaveJdbcService.queryForObject(sql, ReturnWaterLogDetail.class, userid, childid, accountType.getKey(), currencyType.getKey(), level);
    }

    public List<ReturnWaterLogDetail> queryByUserid(long userid, int level, int limit)
    {
        String sql = "select * from " + TABLE + " where detail_userid = ? and detail_level = ? order by detail_amount desc limit " + limit;
        return mSlaveJdbcService.queryForList(sql, ReturnWaterLogDetail.class, userid, level);
    }

}

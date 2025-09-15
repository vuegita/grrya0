package com.inso.modules.passport.returnwater.service.dao;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;

@Repository
public class ReturnWaterLogAmountDaoMysql extends DaoSupport implements ReturnWaterLogAmountDao {


    private static String TABLE = "inso_passport_return_water_log_amount";


    public void addLog(long userid, String username, FundAccountType accountType, ICurrencyType currencyType)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("log_userid", userid);
        keyvalue.put("log_username", username);

        keyvalue.put("log_fund_key", accountType.getKey());
        keyvalue.put("log_currency", currencyType.getKey());
        persistent(TABLE, keyvalue);
    }

    public void updateAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, int level, BigDecimal amount)
    {
        if(level == 1)
        {
            String sql = "update " + TABLE + " set log_level1_amount = log_level1_amount + ? where log_userid = ? and log_fund_key = ? and log_currency = ?";
            mWriterJdbcService.executeUpdate(sql, amount, userid, accountType.getKey(), currencyType.getKey());
        }
        else
        {
            String sql = "update " + TABLE + " set log_level2_amount = log_level2_amount + ? where log_userid = ? and log_fund_key = ? and log_currency = ?";
            mWriterJdbcService.executeUpdate(sql, amount, userid, accountType.getKey(), currencyType.getKey());
        }
    }


    public ReturnWaterLog findByUserid(long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.log_fund_key, A.log_currency, A.log_level1_amount, A.log_level2_amount ");
        sql.append(", B.* ");
        sql.append(" from ").append(TABLE).append(" as A ");
        sql.append(" left join ").append(ReturnWaterLogCountDaoMysql.TABLE).append(" as B on A.log_userid = B.log_userid ");
        sql.append(" where A.log_userid = ?  and A.log_fund_key = ? and A.log_currency = ?");
//        String sql = "select * from " + TABLE + " where log_userid = ?  and log_fund_key = ? and log_currency = ?";
        return mSlaveJdbcService.queryForObject(sql.toString(), ReturnWaterLog.class, userid, accountType.getKey(), currencyType.getKey());
    }


    public List<ReturnWaterLog> queryByUser(long userid, FundAccountType accountType)
    {
//        StringBuilder sql = new StringBuilder("select A.* ");
//        sql.append(", B.log_level1_count, B.log_level2_count ");
//        sql.append(" from ").append(TABLE).append(" as A ");
//        sql.append(" left join inso_passport_return_water_log_count as B on B.log_userid = A.log_userid ");
//        sql.append(" where A.log_userid = ?  and A.log_fund_key = ?");
        StringBuilder sql = new StringBuilder("select A.* ");
        sql.append(", C.log_fund_key, C.log_currency, C.log_level1_amount, C.log_level2_amount ");
        sql.append(" from " + ReturnWaterLogCountDaoMysql.TABLE + " as A ");
        sql.append(" left join inso_passport_return_water_log_amount as C on  C.log_userid = A.log_userid ");
        sql.append(" where A.log_userid = ? ");

        return mSlaveJdbcService.queryForList(sql.toString(), ReturnWaterLog.class, userid);
    }

    @Override
    public RowPager<ReturnWaterLog> queryScrollPage(PageVo pageVo, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from " + ReturnWaterLogCountDaoMysql.TABLE + " as A");

        whereSQLBuffer.append(" inner join inso_passport_user as B on A.log_userid = B.user_id and B.user_type='member' ");
        whereSQLBuffer.append(" left join inso_passport_return_water_log_amount as C on A.log_userid = C.log_userid ");

        whereSQLBuffer.append(" where 1 = 1");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.log_userid = ? ");
        }

       // whereSQLBuffer.append(" and B.log_level1_count > 0 ");

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        //   log_fund_key              varchar(100) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
        //  log_currency              varchar(100) NOT NULL comment '币种->USDT|ETH|BTC等',
        //  log_level1_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',
        //  log_level2_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", C.log_fund_key, C.log_currency, C.log_level1_amount, C.log_level2_amount ");
        select.append(whereSQL);
        select.append(" order by  C.log_level1_amount  desc ");//A.log_userid
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ReturnWaterLog> list = mSlaveJdbcService.queryForList(select.toString(), ReturnWaterLog.class, values.toArray());
        RowPager<ReturnWaterLog> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

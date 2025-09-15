package com.inso.modules.coin.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ProfitConfigDaoMysql extends DaoSupport implements ProfitConfigDao {

    /**
     config_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     config_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     config_agentname              varchar(255) NOT NULL comment  '',

     config_currency               varchar(255) NOT NULL comment '币种',
     config_level                  int(11) UNSIGNED NOT NULL ,

     config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

     config_status                 varchar(20) NOT NULL comment '状态',
     config_createtime             datetime NOT NULL comment '创建时间',
     config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    public static final String TABLE = "inso_coin_core_mining_profit_config";

    @Override
    public long add(UserInfo agentInfo, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("config_agentid", agentInfo.getId());
        keyvalue.put("config_agentname", agentInfo.getName());

        keyvalue.put("config_profit_type", profitType.getKey());
        keyvalue.put("config_currency_type", currency.getKey());
        keyvalue.put("config_level", level);

        keyvalue.put("config_min_amount", minAmount);
        keyvalue.put("config_daily_rate", dailyRate);

        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, BigDecimal dailyRate, BigDecimal minAmount, CryptoCurrency currency, long level, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

//        if(currency != null)
//        {
//            setKeyValue.put("config_currency_type", currency.getKey());
//        }

        if(dailyRate != null)
        {
            setKeyValue.put("config_daily_rate", dailyRate);
        }

        if(minAmount != null)
        {
            setKeyValue.put("config_min_amount", minAmount);
        }

        if(level > 0)
        {
            setKeyValue.put("config_level", level);
        }

        if(status != null)
        {
            setKeyValue.put("config_status", status.getKey());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("config_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public ProfitConfigInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, ProfitConfigInfo.class, id);
    }

    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where  config_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public ProfitConfigInfo findByAgentId(long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency)
    {
        String sql = "select * from " + TABLE + "  where config_agentid = ? and config_profit_type = ? and config_currency_type = ?";
        return mSlaveJdbcService.queryForObject(sql, ProfitConfigInfo.class, agentid, profitType.getKey(), currency.getKey());
    }

    @Override
    public RowPager<ProfitConfigInfo> queryScrollPage(PageVo pageVo, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status)
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

        if(profitType != null)
        {
            values.add(profitType.getKey());
            whereSQLBuffer.append(" and config_profit_type = ? ");
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
        List<ProfitConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), ProfitConfigInfo.class, values.toArray());
        RowPager<ProfitConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<ProfitConfigInfo> queryAllList(long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");


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

        if(profitType != null)
        {
            values.add(profitType.getKey());
            whereSQLBuffer.append(" and config_profit_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and config_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by config_level asc ");
        return  mSlaveJdbcService.queryForList(select.toString(), ProfitConfigInfo.class, values.toArray());


        // and config_profit_type = ? and config_currency_type = ?
//        StringBuilder select = new StringBuilder("select * ");
//        select.append("from ").append(TABLE);
//        select.append(" where config_agentid = ? and config_profit_type = ? and config_currency_type = ? and config_status = ? order by config_level asc");
//
//        return mSlaveJdbcService.queryForList(select.toString(), ProfitConfigInfo.class, agentid, profitType.getKey(), currency.getKey(), status.getKey());
    }


}

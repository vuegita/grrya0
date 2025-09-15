package com.inso.modules.coin.cloud_mining.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CloudProfitConfigDaoMysql extends DaoSupport implements CloudProfitConfigDao {

    /**
     config_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     config_days                    int(11) UNSIGNED NOT NULL comment '投资期限',
     config_level                  int(11) UNSIGNED NOT NULL ,

     config_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '最低投资金额',
     config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

     config_status                 varchar(20) NOT NULL comment '状态',
     config_createtime             datetime NOT NULL comment '创建时间',
     config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    public static final String TABLE = "inso_coin_cloud_profit_config";

    @Override
    public long add(long days, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("config_days", days);
        keyvalue.put("config_level", level);
        
        keyvalue.put("config_min_amount", minAmount);
        keyvalue.put("config_daily_rate", dailyRate);

        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, BigDecimal dailyRate, BigDecimal minAmount, long level, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(minAmount != null)
        {
            setKeyValue.put("config_min_amount", minAmount);
        }

        if(dailyRate != null)
        {
            setKeyValue.put("config_daily_rate", dailyRate);
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

    public CloudProfitConfigInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, CloudProfitConfigInfo.class, id);
    }

    public CloudProfitConfigInfo findByCurrencyAndAmount(CryptoCurrency currency, BigDecimal amount)
    {
        String sql = "select MIN(config_amount), config_daily_rate, config_days, config_level from " + TABLE + "  where config_days = ? and config_min_amount >= ?";
        return mSlaveJdbcService.queryForObject(sql, CloudProfitConfigInfo.class, currency.getKey(), amount);
    }

    @Override
    public RowPager<CloudProfitConfigInfo> queryScrollPage(PageVo pageVo, long days, Status status)
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

        if(days > 0)
        {
            values.add(days);
            whereSQLBuffer.append(" and config_days = ? ");
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
        select.append(" order by config_days asc, config_level asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<CloudProfitConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), CloudProfitConfigInfo.class, values.toArray());
        RowPager<CloudProfitConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<CloudProfitConfigInfo> queryAllList(Status status)
    {
        //
        StringBuilder select = new StringBuilder("select * ");
        select.append("from ").append(TABLE);
        select.append(" where config_status = ? order by config_days asc, config_level asc ");

        return mSlaveJdbcService.queryForList(select.toString(), CloudProfitConfigInfo.class, status.getKey());
    }

    public List<CloudProfitConfigInfo> queryAllListByDays(long days)
    {
        //
        StringBuilder select = new StringBuilder("select * ");
        select.append("from ").append(TABLE);
        select.append(" where config_days = ? and config_status = ? order by config_level asc ");
        Status status = Status.ENABLE;
        return mSlaveJdbcService.queryForList(select.toString(), CloudProfitConfigInfo.class, days, status.getKey());
    }


}

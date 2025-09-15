package com.inso.modules.coin.withdraw.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CoinWithdrawChannelDaoMysql extends DaoSupport implements CoinWithdrawChannelDao {

    /**
     channel_key    	           varchar(255) NOT NULL ,
     channel_dimension_type       varchar(255) NOT NULL comment '维度: platfrom|agent-id',
     channel_network_type         varchar(255) NOT NULL comment '网络类型',

     channel_trigger_privatekey   varchar(255) NOT NULL comment '账号私钥',
     channel_trigger_address      varchar(255) NOT NULL comment '账号地址',

     channel_fee_rate             decimal(18, 8) NOT NULL comment '手续费率',
     channel_single_feemoney      decimal(18, 8) NOT NULL comment '单笔再加',

     channel_status               varchar(20) NOT NULL comment '状态',
     channel_createtime  	       datetime DEFAULT NULL ,
     channel_remark               varchar(1000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_withdraw_channel";

    @Override
    public void add(String key, MyDimensionType dimensionType, String triggerPrivateKey, String triggerAddress, CryptoNetworkType networkType,
                    BigDecimal gasLimit,
                    BigDecimal feeRate, BigDecimal singleFeemoney, Status status)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("channel_key", key);
        keyvalue.put("channel_dimension_type", dimensionType.getKey());
        keyvalue.put("channel_network_type", networkType.getKey());

        keyvalue.put("channel_trigger_privatekey", triggerPrivateKey);
        keyvalue.put("channel_trigger_address", triggerAddress);
        keyvalue.put("channel_gas_limit", gasLimit);

        keyvalue.put("channel_fee_rate", feeRate);
        keyvalue.put("channel_single_feemoney", singleFeemoney);

        keyvalue.put("channel_status", status.getKey());
        keyvalue.put("channel_createtime", new Date());
        keyvalue.put("channel_remark", StringUtils.getEmpty());

        persistent(TABLE, keyvalue);
    }

    public CoinWithdrawChannel findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType)
    {
        String sql = "select * from " + TABLE + " where channel_key = ? and channel_network_type = ? and channel_dimension_type = ?";
        return mSlaveJdbcService.queryForObject(sql, CoinWithdrawChannel.class, key, networkType.getKey(), dimensionType.getKey());
    }

    public CoinWithdrawChannel findById(long id)
    {
        String sql = "select * from " + TABLE + " where channel_id = ?";
        return mSlaveJdbcService.queryForObject(sql, CoinWithdrawChannel.class, id);
    }

    public void updateInfo(long id, String triggerPrivateKey, String triggerAddress, BigDecimal gasLimit, BigDecimal feeRate, BigDecimal singleFeemoney, Status status)
    {
        LinkedHashMap<String, Object> setkeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(triggerPrivateKey))
        {
            setkeyValue.put("channel_trigger_privatekey", triggerPrivateKey);
        }
        if(!StringUtils.isEmpty(triggerAddress))
        {
            setkeyValue.put("channel_trigger_address", triggerAddress);
        }
        if(feeRate != null)
        {
            setkeyValue.put("channel_fee_rate", feeRate);
        }
        if(singleFeemoney != null)
        {
            setkeyValue.put("channel_single_feemoney", singleFeemoney);
        }

        if(status != null)
        {
            setkeyValue.put("channel_status", status.getKey());
        }

        if(gasLimit != null)
        {
            setkeyValue.put("channel_gas_limit", gasLimit);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("channel_id", id);

        update(TABLE, setkeyValue, whereKeyValue);
    }

    @Override
    public RowPager<CoinWithdrawChannel> queryScrollPage(PageVo pageVo, String key, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE);

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(!StringUtils.isEmpty(key))
        {
            values.add(key);
            whereSQLBuffer.append(" and channel_key = ? ");
        }

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and channel_network_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and channel_status = ? ");
        }

        if(dimensionType != null)
        {
            values.add(dimensionType.getKey());
            whereSQLBuffer.append(" and channel_dimension_type = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * ");
        select.append(whereSQL);
        select.append(" order by channel_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<CoinWithdrawChannel> list = mSlaveJdbcService.queryForList(select.toString(), CoinWithdrawChannel.class, values.toArray());
        RowPager<CoinWithdrawChannel> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

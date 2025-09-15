package com.inso.modules.coin.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CoinSettleConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CoinSettleConfigDaoMysql extends DaoSupport implements CoinSettleConfigDao{

    /**
     config_key    	      varchar(255) NOT NULL ,

     config_network_type     varchar(255) NOT NULL comment '网络类型',
     config_receiv_address   varchar(255) NOT NULL comment '收款账号',
     config_share_ratio      decimal(18,3) NOT NULL comment '分成比例',

     config_status           varchar(20) NOT NULL comment '状态',
     config_createtime  	  datetime DEFAULT NULL ,
     config_remark           varchar(1000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_settle_config";

    @Override
    public void add(String key, MyDimensionType dimensionType, String address, CryptoNetworkType networkType, BigDecimal shareRatio, Status status)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("config_key", key);
        keyvalue.put("config_dimension_type", dimensionType.getKey());

        keyvalue.put("config_receiv_address", address);
        keyvalue.put("config_network_type", networkType.getKey());
        keyvalue.put("config_share_ratio", shareRatio);

        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", new Date());
        keyvalue.put("config_remark", StringUtils.getEmpty());

        persistent(TABLE, keyvalue);
    }
    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where  config_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public CoinSettleConfig findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType)
    {
        String sql = "select * from " + TABLE + " where config_key = ? and config_network_type = ? and config_dimension_type = ?";
        return mSlaveJdbcService.queryForObject(sql, CoinSettleConfig.class, key, networkType.getKey(), dimensionType.getKey());
    }

    public CoinSettleConfig findById(long id)
    {
        String sql = "select * from " + TABLE + " where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, CoinSettleConfig.class, id);
    }

    public void updateInfo(long id, String receivAddress, BigDecimal shareRatio, Status status)
    {
        if(shareRatio != null)
        {
            String sql = "update " + TABLE + " set config_receiv_address = ?, config_share_ratio = ?, config_status = ? where config_id = ?";
            mWriterJdbcService.executeUpdate(sql, receivAddress, shareRatio, status.getKey(), id);
        }
        else
        {
            String sql = "update " + TABLE + " set config_receiv_address = ?, config_status = ? where config_id = ?";
            mWriterJdbcService.executeUpdate(sql, receivAddress, status.getKey(), id);
        }

    }

    @Override
    public RowPager<CoinSettleConfig> queryScrollPage(PageVo pageVo, String agentname, CryptoNetworkType networkType, Status status, MyDimensionType dimensionType)
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

        if(!StringUtils.isEmpty(agentname))
        {
            values.add(agentname);
            whereSQLBuffer.append(" and config_key = ? ");
        }

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and config_network_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and config_status = ? ");
        }

        if(dimensionType != null)
        {
            values.add(dimensionType.getKey());
            whereSQLBuffer.append(" and config_dimension_type = ? ");
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
        select.append(" order by config_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<CoinSettleConfig> list = mSlaveJdbcService.queryForList(select.toString(), CoinSettleConfig.class, values.toArray());
        RowPager<CoinSettleConfig> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

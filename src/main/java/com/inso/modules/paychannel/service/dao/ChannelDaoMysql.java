package com.inso.modules.paychannel.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ChannelDaoMysql extends DaoSupport implements ChannelDao {


    public static String TABLE = "inso_pay_channel";


    public void add(String title, JSONObject secret, PayProductType productType, ChannelStatus channelStatus, ChannelType type,
                    ICurrencyType currencyType, String remark, long sort, BigDecimal feerate, BigDecimal extraFeemoney)
    {
        Date datetime = new Date();
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("channel_name", title);
        keyvalue.put("channel_secret", secret.toJSONString());
        keyvalue.put("channel_status", channelStatus.getKey());
        keyvalue.put("channel_type", type.getKey());
        keyvalue.put("channel_product_type", productType.getKey());

        if(currencyType != null)
        {
            keyvalue.put("channel_currency_type", currencyType.getKey());
        }

        keyvalue.put("channel_limit_total_count_of_day", BigDecimal.ZERO);

        keyvalue.put("channel_createtime", datetime);

        keyvalue.put("channel_sort", sort);
        keyvalue.put("channel_remark", StringUtils.getNotEmpty(remark));

        keyvalue.put("channel_feerate", BigDecimalUtils.getNotNull(feerate));
        keyvalue.put("channel_extra_feemoney", BigDecimalUtils.getNotNull(extraFeemoney));

        persistent(TABLE, keyvalue);
    }

    public void delete(long channelid)
    {
        String sql = "delete from " + TABLE + " where channel_id = ?";
        mWriterJdbcService.executeUpdate(sql, channelid);
    }

    public ChannelInfo findById(long channelid)
    {
        String sql = "select * from " + TABLE + " where channel_id = ?";
        return mSlaveJdbcService.queryForObject(sql, ChannelInfo.class, channelid);
    }

    public void updateInfo(long channelid, String title, JSONObject secret, ChannelStatus channelStatus, String remark ,long sort, BigDecimal feerate, BigDecimal extraFeemoney)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("channel_name", title);
        setKeyValue.put("channel_secret", secret.toJSONString());
        setKeyValue.put("channel_status", channelStatus.getKey());
        setKeyValue.put("channel_sort", sort);
        setKeyValue.put("channel_remark", StringUtils.getNotEmpty(remark));

        if(feerate != null)
        {
            setKeyValue.put("channel_feerate", feerate);
        }

        if(extraFeemoney != null)
        {
            setKeyValue.put("channel_extra_feemoney", extraFeemoney);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("channel_id", channelid);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public List<ChannelInfo> queryAllList(ChannelStatus status, ChannelType type, PayProductType productType, ICurrencyType currencyType)
    {
        if(productType != null)
        {
            if(currencyType != null)
            {
                String sql = "select * from " + TABLE + " where channel_status = ? and channel_type = ? and channel_product_type = ? and channel_currency_type = ? order by channel_sort asc";
                return mSlaveJdbcService.queryForList(sql, ChannelInfo.class, status.getKey(), type.getKey(), productType.getKey(), currencyType.getKey());
            }

            String sql = "select * from " + TABLE + " where channel_status = ? and channel_type = ? and channel_product_type = ? order by channel_sort asc";
            return mSlaveJdbcService.queryForList(sql, ChannelInfo.class, status.getKey(), type.getKey(), productType.getKey());
        }
        String sql = "select * from " + TABLE + " where channel_status = ? and channel_type = ? order by channel_sort asc";
        return mSlaveJdbcService.queryForList(sql, ChannelInfo.class, status.getKey(), type.getKey());
    }

    public void queryAll(Callback<ChannelInfo> callback)
    {
        String sql = "select * from " + TABLE;
        mSlaveJdbcService.queryAll(callback, sql, ChannelInfo.class);
    }


    @Override
    public RowPager<ChannelInfo> queryScrollPage(PageVo pageVo, ChannelStatus status, ChannelStatus ignoreStatus, ChannelType type ,String remark)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("");

        whereSQLBuffer.append(" where 1 = 1");


        if(status!= null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and channel_status = ? ");
        }

        if(ignoreStatus!= null)
        {
            values.add(ignoreStatus.getKey());
            whereSQLBuffer.append(" and channel_status != ? ");
        }

        if(type != null)
        {
            values.add(type.getKey());
            whereSQLBuffer.append(" and channel_type = ? ");
        }

        if(remark != null)
        {
            values.add(remark);
            whereSQLBuffer.append(" and channel_remark = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_pay_channel " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_pay_channel");
        select.append(whereSQL);
        select.append(" order by channel_sort asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ChannelInfo> list = mSlaveJdbcService.queryForList(select.toString(), ChannelInfo.class, values.toArray());
        RowPager<ChannelInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

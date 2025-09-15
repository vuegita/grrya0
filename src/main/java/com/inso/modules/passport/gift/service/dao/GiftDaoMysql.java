package com.inso.modules.passport.gift.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class GiftDaoMysql extends DaoSupport implements GiftDao {

    private static final String TABLE = "inso_passport_gift_config";


    @Override
    public void add(String title, String desc, GiftTargetType targetType, GiftPeriodType periodType, BigDecimal presentAmount, BigDecimal limitAmount,
                    long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable, JSONObject remark)
    {
        Date date = new Date();

        String key = targetType.getKey();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("config_key", key);
        keyvalue.put("config_title", StringUtils.getNotEmpty(title));
        keyvalue.put("config_desc", StringUtils.getNotEmpty(desc));
        keyvalue.put("config_target_type", targetType.getKey());
        keyvalue.put("config_period_type", periodType.getKey());
        keyvalue.put("config_present_amount", presentAmount);
        keyvalue.put("config_limit_amount", limitAmount);
        keyvalue.put("config_sort", sort);
        keyvalue.put("config_createtime", date);
        keyvalue.put("config_status", status.getKey());

        keyvalue.put("config_present_arr_value", presentAmountArrValue);
        keyvalue.put("config_present_arr_enable", presentAmountArrEnable.getKey());

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("config_remark", remark.toJSONString());
        }
        persistent(TABLE, keyvalue);
    }

    @Override
    public void update(long id, String title, String desc, BigDecimal presentAmount, BigDecimal limitAmount, long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable)
    {
        LinkedHashMap set = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(title))
        {
            set.put("config_title", title);
        }

        if(!StringUtils.isEmpty(desc))
        {
            set.put("config_desc", desc);
        }

        if(presentAmount != null)
        {
            set.put("config_present_amount", presentAmount);
        }

        if(limitAmount != null)
        {
            set.put("config_limit_amount", limitAmount);
        }

        if(sort >= 0)
        {
            set.put("config_sort", sort);
        }

        if(status != null)
        {
            set.put("config_status", status.getKey());
        }

        if(presentAmountArrValue != null)
        {
            set.put("config_present_arr_value", presentAmountArrValue);
        }

        if(presentAmountArrEnable != null)
        {
            set.put("config_present_arr_enable", presentAmountArrEnable.getKey());
        }

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("config_id", id);
        update(TABLE, set, where);
    }

    public GiftConfigInfo findById(long cardid)
    {
        String sql = "select * from " + TABLE  + " where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, GiftConfigInfo.class, cardid);
    }

    public List<GiftConfigInfo> queryAll(Status status)
    {
        String sql = "select * from " + TABLE  + " where config_status = ? order by config_sort";
        return mSlaveJdbcService.queryForList(sql, GiftConfigInfo.class, status.getKey());
    }

    @Override
    public RowPager<GiftConfigInfo> queryScrollPage(PageVo pageVo, GiftTargetType targetType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        // 时间放前面
        if(!StringUtils.isEmpty(pageVo.getFromTime()) && !StringUtils.isEmpty(pageVo.getToTime()))
        {
            whereSQLBuffer.append(" and config_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(targetType != null)
        {
            values.add(targetType.getKey());
            whereSQLBuffer.append(" and config_target_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and config_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total <= 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by config_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<GiftConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), GiftConfigInfo.class, values.toArray());
        RowPager<GiftConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.ad.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CategoryDaoMysql extends DaoSupport implements CategoryDao {

    private static final String TABLE = "inso_ad_category";

    @Override
    public void addCategory(String key, String name, BigDecimal returnRate, Status status, BigDecimal beginPrice, BigDecimal endPrice)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("category_key", key);
        keyvalue.put("category_name", name);
        keyvalue.put("category_min_price", beginPrice);
        keyvalue.put("category_max_price", endPrice);
        keyvalue.put("category_return_rate", BigDecimalUtils.getNotNull(returnRate));
        keyvalue.put("category_status", status.getKey());
        keyvalue.put("category_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, BigDecimal returnRate, String name, BigDecimal minPrice, BigDecimal maxPrice)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("category_status", status.getKey());

        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("category_name", name);
        }

        if(minPrice != null)
        {
            setKeyValue.put("category_min_price", minPrice);
        }

        if(maxPrice != null)
        {
            setKeyValue.put("category_max_price", maxPrice);
        }

        if(returnRate != null)
        {
            setKeyValue.put("category_return_rate", returnRate);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("category_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AdCategoryInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where category_id = ?";
        return mSlaveJdbcService.queryForObject(sql, AdCategoryInfo.class, id);
    }

    public AdCategoryInfo findByKey(String key)
    {
        String sql = "select * from " + TABLE + " where category_key = ?";
        return mSlaveJdbcService.queryForObject(sql, AdCategoryInfo.class, key);
    }

    public List<AdCategoryInfo> queryAllByStatus(Status status)
    {
        if(status != null)
        {
            String sql = "select * from " + TABLE + " where category_status = ?";
            return mSlaveJdbcService.queryForList(sql, AdCategoryInfo.class, status.getKey());
        }
        else
        {
            String sql = "select * from " + TABLE;
            return mSlaveJdbcService.queryForList(sql, AdCategoryInfo.class);
        }
    }

    @Override
    public RowPager<AdCategoryInfo> queryScrollPage(PageVo pageVo, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and category_status = ? ");
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
        select.append(" order by category_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AdCategoryInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdCategoryInfo.class, values.toArray());
        RowPager<AdCategoryInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.ad.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MaterielDaoMysql extends DaoSupport implements MaterielDao {

    /**
     *   materiel_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   materiel_categoryid         int(11) UNSIGNED NOT NULL ,
     *   materiel_name               varchar(50) NOT NULL DEFAULT '' comment '名称',
     *   materiel_desc               varchar(100) NOT NULL DEFAULT '' comment '描述',
     *   materiel_thumbnail          varchar(255) NOT NULL DEFAULT '' comment '缩略图',
     *   materiel_jump_url           varchar(255) NOT NULL DEFAULT '' comment '跳转链接',
     *   materiel_price              decimal(18,2) NOT NULL DEFAULT 0 comment '单价',
     *   materiel_provider           varchar(255) NOT NULL DEFAULT '' comment '广告主',
     *   materiel_admin              varchar(50) NOT NULL DEFAULT '' comment '操作人',
     *   materiel_event              varchar(50) NOT NULL comment '事件类型=download|buy|like',
     *   materiel_limit_min_day      int(11) NOT NULL DEFAULT 0 comment '限制最小天数内不能重复操作, 为0表示不限制',
     *   materiel_status             varchar(50) NOT NULL comment 'enable|disable',
     *   materiel_createtime         datetime DEFAULT NULL comment '创建时间',
     *   materiel_endtime            datetime DEFAULT NULL comment '结束时间-广告主需要推广多少天',
     *   materiel_remark            varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */
    public static final String TABLE = "inso_ad_materiel";

    @Override
    public long add(String key, long categoryid, String name, String desc, Status status,
                    String thumb, String introImg, String jumpUrl,
                    BigDecimal price, String provider, String admin,
                    AdEventType eventType, long limitMinDay, int expiresDay)
    {
        Date date = new Date();

        DateTime dateTime = new DateTime(date);
        DateTime expiresDateTime = dateTime.plusDays(expiresDay);

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("materiel_categoryid", categoryid);
        keyvalue.put("materiel_key", key);
        keyvalue.put("materiel_name", name);
        keyvalue.put("materiel_desc", desc);
        keyvalue.put("materiel_thumb", StringUtils.getNotEmpty(thumb));
        keyvalue.put("materiel_intro_img", StringUtils.getNotEmpty(introImg));
        keyvalue.put("materiel_jump_url", StringUtils.getNotEmpty(jumpUrl));
        keyvalue.put("materiel_price", BigDecimalUtils.getNotNull(price));
        keyvalue.put("materiel_provider", StringUtils.getNotEmpty(provider));
        keyvalue.put("materiel_admin", StringUtils.getNotEmpty(admin));
        keyvalue.put("materiel_event_type", eventType.getKey());
        keyvalue.put("materiel_limit_min_day", limitMinDay);
        keyvalue.put("materiel_status", status.getKey());
        keyvalue.put("materiel_endtime", expiresDateTime.toDate());
        keyvalue.put("materiel_createtime", date);

        keyvalue.put("materiel_remark", StringUtils.getEmpty());

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, String name, String desc, Status status, String thumb, String introImg, String jumpUrl, BigDecimal price, Date endTime)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("materiel_name", name);
        }

        if(!StringUtils.isEmpty(desc))
        {
            setKeyValue.put("materiel_desc", desc);
        }

        if(!StringUtils.isEmpty(thumb))
        {
            setKeyValue.put("materiel_thumb", thumb);
        }

        if(!StringUtils.isEmpty(introImg))
        {
            setKeyValue.put("materiel_intro_img", thumb);
        }

        if(!StringUtils.isEmpty(jumpUrl))
        {
            setKeyValue.put("materiel_jump_url", jumpUrl);
        }

        if(status != null)
        {
            setKeyValue.put("materiel_status", status.getKey());
        }

        if(price != null)
        {
            setKeyValue.put("materiel_price", price);
        }

        if(endTime != null)
        {
            setKeyValue.put("materiel_endtime", endTime);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("materiel_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AdMaterielInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where materiel_id = ?";
        return mSlaveJdbcService.queryForObject(sql, AdMaterielInfo.class, id);
    }

    public long countByKey(String key)
    {
        String sql = "select count(1) from " + TABLE + " where materiel_key = ?";
        return mSlaveJdbcService.count(sql, key);
    }

    public long count()
    {
        String sql = "select count(1) from " + TABLE;
        return mSlaveJdbcService.count(sql);
    }

    public void queryAll(Callback<AdMaterielInfo> callback)
    {
        String sql = "select * from " + TABLE;
        mSlaveJdbcService.queryAll(callback, sql, AdMaterielInfo.class);
    }

    public List<AdMaterielInfo> queryByCategory(DateTime dateTime, long categoryid, int pageOffset, int pageSize,long minPrice,long maxPrice)
    {
//        if(categoryid > 0)
//        {
//            Date date = dateTime.toDate();
//            StringBuilder sql = new StringBuilder();
//            sql.append("select * from ").append(TABLE);
//            sql.append(" where materiel_createtime >= ? and materiel_categoryid = ? ");
//
//            sql.append(" order by materiel_id desc ");
//            sql.append(" limit ").append(pageOffset).append(",").append(pageSize);
//            return mSlaveJdbcService.queryForList(sql.toString(), AdMaterielInfo.class, date, categoryid);
//        }else{
//            Date date = dateTime.toDate();
//            StringBuilder sql = new StringBuilder();
//            sql.append("select * from ").append(TABLE);
//            sql.append(" where materiel_createtime >= ? ");
//
//            sql.append(" order by materiel_id desc ");
//            sql.append(" limit ").append(pageOffset).append(",").append(pageSize);
//            return mSlaveJdbcService.queryForList(sql.toString(), AdMaterielInfo.class, date);
//        }

        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        Date date = dateTime.toDate();
        whereSQLBuffer.append(" and materiel_createtime >= ? ");
        values.add(date);

        whereSQLBuffer.append(" and materiel_status >= ? ");
        values.add(Status.ENABLE.getKey());

        if(categoryid > 0)
        {
            values.add(categoryid);
            whereSQLBuffer.append(" and materiel_categoryid = ? ");
        }

        if(minPrice >= 0 && maxPrice > 0 && maxPrice>=minPrice)
        {
            whereSQLBuffer.append(" and materiel_price >= ? and materiel_price <= ?  ");
            values.add(minPrice);
            values.add(maxPrice);
        }

        String whereSQL = whereSQLBuffer.toString();
        StringBuilder select = new StringBuilder("select * from ").append(TABLE);
        select.append(whereSQL);
        select.append(" order by materiel_id desc ");
        select.append(" limit ").append(pageOffset).append(",").append(pageSize);
        List<AdMaterielInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdMaterielInfo.class, values.toArray());

        return list;




    }

    @Override
    public RowPager<AdMaterielInfo> queryScrollPage(PageVo pageVo, long categoryid, Status status, AdEventType eventType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        if(!StringUtils.isEmpty(pageVo.getFromTime()))
        {
            whereSQLBuffer.append(" and materiel_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(categoryid > 0)
        {
            values.add(categoryid);
            whereSQLBuffer.append(" and materiel_categoryid = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and materiel_status = ? ");
        }

        if(eventType != null)
        {
            values.add(eventType.getKey());
            whereSQLBuffer.append(" and materiel_event_type = ? ");
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
        select.append(" order by materiel_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<AdMaterielInfo> list = mSlaveJdbcService.queryForList(select.toString(), AdMaterielInfo.class, values.toArray());
        RowPager<AdMaterielInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

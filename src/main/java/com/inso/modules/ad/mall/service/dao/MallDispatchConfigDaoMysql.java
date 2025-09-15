package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallDispatchConfigDaoMysql extends DaoSupport implements MallDispatchConfigDao {

    private static final String TABLE = "inso_ad_mall_dispatch_config";

    /**
     config_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     config_min_count          int(11) UNSIGNED NOT NULL ,
     config_max_count          int(11) UNSIGNED NOT NULL ,
     config_level              varchar(50) NOT NULL comment 'Lv1|Lv2',
     config_createtime         datetime DEFAULT NULL comment '创建时间',
     config_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void addCategory(MallStoreLevel levelType, Status status, long minCount, long maxCount)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("config_min_count", minCount);
        keyvalue.put("config_max_count", maxCount);
        keyvalue.put("config_level", levelType.getKey());
        keyvalue.put("config_status", status.getKey());
        keyvalue.put("config_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, long minCount, long maxCount)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        setKeyValue.put("config_min_count", minCount);
        setKeyValue.put("config_max_count", maxCount);

        if(status != null)
        {
            setKeyValue.put("config_status", status.getKey());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("config_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MallDispatchConfigInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where config_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MallDispatchConfigInfo.class, id);
    }

    public MallDispatchConfigInfo findByKey(MallStoreLevel levelType)
    {
        String sql = "select * from " + TABLE + " where config_level = ? ";
        return mSlaveJdbcService.queryForObject(sql, MallDispatchConfigInfo.class, levelType.getKey());
    }

    public List<MallDispatchConfigInfo> queryAll(Status status)
    {
        String sql = "select * from " + TABLE + " where config_status = ?";
        return mSlaveJdbcService.queryForList(sql, MallDispatchConfigInfo.class, status.getKey());
    }

    @Override
    public RowPager<MallDispatchConfigInfo> queryScrollPage(PageVo pageVo)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        else if(pageVo.getFromTime() != null)
//        {
//            whereSQLBuffer.append(" and config_createtime between ? and ? ");
//            values.add(pageVo.getFromTime());
//            values.add(pageVo.getToTime());
//        }
//
//        if(status != null)
//        {
//            values.add(status.getKey());
//            whereSQLBuffer.append(" and config_status = ? ");
//        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by config_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<MallDispatchConfigInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallDispatchConfigInfo.class, values.toArray());
        RowPager<MallDispatchConfigInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

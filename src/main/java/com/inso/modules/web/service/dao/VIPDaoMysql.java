package com.inso.modules.web.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class VIPDaoMysql extends DaoSupport implements VIPDao {

    /**
     *   vip_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   vip_type       		varchar(50) NOT NULL comment 'vip 类型,检举类型',
     *   vip_level       		int(11) UNSIGNED NOT NULL comment 'vip等级',
     *   vip_name       		varchar(50) NOT NULL comment 'vip名称',
     *   vip_status       		varchar(50) NOT NULL comment 'enable|disable',
     *   vip_createtime  		datetime DEFAULT NULL comment '创建时间',
     *   vip_remark            varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */
    private static final String TABLE = "inso_web_vip";

    @Override
    public void addVIPLevel(VIPType vipType, long level, String name, Status status, BigDecimal price)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("vip_type", vipType.getKey());
        keyvalue.put("vip_level", level);
        keyvalue.put("vip_name", name);
        keyvalue.put("vip_status", status.getKey());
        keyvalue.put("vip_price", price);
        keyvalue.put("vip_createtime", date);

        keyvalue.put("vip_remark", StringUtils.getEmpty());
        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, String name, BigDecimal price, long level)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("vip_status", status.getKey());

        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("vip_name", name);
        }

        setKeyValue.put("vip_price", price);

        setKeyValue.put("vip_level", level);

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("vip_id", id);



        update(TABLE, setKeyValue, whereKeyValue);
    }

    @Override
    public List<VIPInfo> queryAllStatus(VIPType type, Status status)
    {
        if(status != null)
        {
            String sql = "select * from " + TABLE + " where vip_type = ? and vip_status = ?";
            return mSlaveJdbcService.queryForList(sql, VIPInfo.class, type.getKey(), status.getKey());
        }
        else
        {
            String sql = "select * from " + TABLE + " where vip_type = ? ";
            return mSlaveJdbcService.queryForList(sql, VIPInfo.class, type.getKey());
        }
    }

    public VIPInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where vip_id = ?";
        return mSlaveJdbcService.queryForObject(sql, VIPInfo.class, id);
    }

    public VIPInfo findByLevel(VIPType vipType, long level)
    {
        String sql = "select * from " + TABLE + " where vip_type = ? and vip_level = ?";
        return mSlaveJdbcService.queryForObject(sql, VIPInfo.class, vipType.getKey(), level);
    }

    public long findMaxLevel(VIPType vipType)
    {
        String sql = "select vip_level from " + TABLE + " where vip_type = ? order by vip_id desc limit 1";
        Long rs = mSlaveJdbcService.queryForObject(sql, Long.class, vipType.getKey());
        if(rs == null)
        {
            return 0;
        }
        return rs;
    }

    @Override
    public RowPager<VIPInfo> queryScrollPage(PageVo pageVo, VIPType vipType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(vipType != null)
        {
            values.add(vipType.getKey());
            whereSQLBuffer.append(" and vip_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and vip_status = ? ");
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
        select.append(" order by vip_level desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<VIPInfo> list = mSlaveJdbcService.queryForList(select.toString(), VIPInfo.class, values.toArray());
        RowPager<VIPInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

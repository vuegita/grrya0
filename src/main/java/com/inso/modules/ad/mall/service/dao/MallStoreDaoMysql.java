package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallStoreDaoMysql extends DaoSupport implements MallStoreDao {

    private static final String TABLE = "inso_ad_mall_store";

    /**
     store_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     store_name               varchar(50) NOT NULL comment '名称',
     store_userid         int(11) UNSIGNED NOT NULL ,
     store_username       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     store_status             varchar(50) NOT NULL comment 'enable|disable',
     store_createtime         datetime DEFAULT NULL comment '创建时间',
     store_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void addCategory(UserInfo userInfo, String name, MallStoreLevel level, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("store_name", name);
        keyvalue.put("store_userid", userInfo.getId());
        keyvalue.put("store_username", userInfo.getName());
        keyvalue.put("store_status", status.getKey());
        keyvalue.put("store_level", level.getKey());
        keyvalue.put("store_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, MallStoreLevel level, String name)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("store_status", status.getKey());

        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("store_name", name);
        }
        if(level != null)
        {
            setKeyValue.put("store_level", level.getKey());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("store_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MallStoreInfo findUserid(long userid)
    {
        String sql = "select * from " + TABLE + " where store_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, MallStoreInfo.class, userid);
    }

    public void queryAll(Callback<MallStoreInfo> callback)
    {
        String sql = "select * from " + TABLE;
        mSlaveJdbcService.queryAll(callback, sql, MallStoreInfo.class);
    }

    @Override
    public RowPager<MallStoreInfo> queryScrollPage(PageVo pageVo, Status status, long merchantid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        if(merchantid > 0)
        {
            values.add(merchantid);
            whereSQLBuffer.append(" and store_userid = ? ");
        }

        // 时间放前面
        else if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and store_status = ? ");
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
        select.append(" order by store_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MallStoreInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallStoreInfo.class, values.toArray());
        RowPager<MallStoreInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

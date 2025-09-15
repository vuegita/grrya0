package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.UserAttrDaoMysql;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class InventoryDaoMysql extends DaoSupport implements InventoryDao {

    /**
     inventory_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     inventory_userid                 int(11) UNSIGNED NOT NULL comment '商家ID',
     inventory_username               varchar(255) NOT NULL comment  '',

     inventory_price                  decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
     inventory_quantity               int(11) UNSIGNED NOT NULL comment '库存数量',

     inventory_materielid             int(11) UNSIGNED NOT NULL ,
     inventory_categoryid             int(11) UNSIGNED NOT NULL ,

     inventory_status                 varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     inventory_createtime             datetime DEFAULT NULL comment '创建时间',
     inventory_remark                 varchar(5000) NOT NULL DEFAULT '' COMMENT '',

     */
    private static final String TABLE = "inso_ad_mall_shop_inventory";

    @Override
    public void addOrder(UserInfo userInfo, AdMaterielInfo materielInfo,
                         long quantity, Status status, JSONObject jsonObject)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("inventory_userid", userInfo.getId());
        keyvalue.put("inventory_username", userInfo.getName());

        keyvalue.put("inventory_price", materielInfo.getPrice());
        keyvalue.put("inventory_quantity", quantity);

        keyvalue.put("inventory_materielid", materielInfo.getId());
        keyvalue.put("inventory_categoryid", materielInfo.getCategoryid());

        keyvalue.put("inventory_status", status.getKey());
        keyvalue.put("inventory_createtime", date);

        if(jsonObject != null && !jsonObject.isEmpty())
        {
            keyvalue.put("inventory_remark", jsonObject.toJSONString());
        }
        else
        {
            keyvalue.put("inventory_remark", StringUtils.getEmpty());
        }

        persistent(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(long id, Status status, long quantity, JSONObject jsonObject)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(status != null)
        {
            setKeyValue.put("inventory_status", status.getKey());
        }
        setKeyValue.put("inventory_quantity", quantity);

        if(jsonObject != null)
        {
            setKeyValue.put("inventory_remark", jsonObject.toJSONString());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("inventory_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public InventoryInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where inventory_id = ?";
        return mSlaveJdbcService.queryForObject(sql, InventoryInfo.class, id);
    }

    public InventoryInfo findByUseridAndMaterielid(long userid, long materielid)
    {
        String sql = "select * from " + TABLE + " where inventory_userid = ? and inventory_materielid = ?";
        return mSlaveJdbcService.queryForObject(sql, InventoryInfo.class, userid, materielid);
    }

    @Override
    public RowPager<InventoryInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status Status, long categoryid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.inventory_userid = B.attr_userid ");

        whereSQLBuffer.append(" where 1 = 1 ");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and inventory_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and B.attr_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and B.attr_staffid = ? ");
        }

        if(!StringUtils.isEmpty(pageVo.getFromTime()))
        {
            // 时间放前面
            whereSQLBuffer.append(" and inventory_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(Status != null)
        {
            values.add(Status.getKey());
            whereSQLBuffer.append(" and inventory_status = ? ");
        }

        if(categoryid > 0)
        {
            values.add(categoryid);
            whereSQLBuffer.append(" and inventory_categoryid = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        select.append(" order by inventory_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<InventoryInfo> list = mSlaveJdbcService.queryForList(select.toString(), InventoryInfo.class, values.toArray());
        RowPager<InventoryInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

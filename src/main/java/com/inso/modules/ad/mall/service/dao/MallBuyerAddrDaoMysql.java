package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallBuyerAddrDaoMysql extends DaoSupport implements MallBuyerAddrDao {

    private static final String TABLE = "inso_ad_mall_buyer_address";

    /**
     address_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     address_location           varchar(500) NOT NULL comment '位置明细,以空格隔开',
     address_phone              varchar(100) NOT NULL comment '电话',
     address_userid             int(11) UNSIGNED NOT NULL ,
     address_username           varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     address_status             varchar(50) NOT NULL comment 'enable|disable',
     address_createtime         datetime DEFAULT NULL comment '创建时间',
     address_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void addCategory(UserInfo userInfo, String location, Status status, String phone)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("address_location", location);
        keyvalue.put("address_phone", phone);
        keyvalue.put("address_userid", userInfo.getId());
        keyvalue.put("address_username", userInfo.getName());
        keyvalue.put("address_status", status.getKey());
        keyvalue.put("address_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, String phone, Status status, String location)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("address_status", status.getKey());

        if(!StringUtils.isEmpty(location))
        {
            setKeyValue.put("address_location", location);
        }

        if(!StringUtils.isEmpty(phone))
        {
            setKeyValue.put("address_phone", phone);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("address_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MallBuyerAddrInfo findUserid(long userid)
    {
        String sql = "select * from " + TABLE + " where address_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, MallBuyerAddrInfo.class, userid);
    }

    @Override
    public RowPager<MallBuyerAddrInfo> queryScrollPage(PageVo pageVo, Status status, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and address_userid = ? ");
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
            whereSQLBuffer.append(" and address_status = ? ");
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
        select.append(" order by address_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MallBuyerAddrInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallBuyerAddrInfo.class, values.toArray());
        RowPager<MallBuyerAddrInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

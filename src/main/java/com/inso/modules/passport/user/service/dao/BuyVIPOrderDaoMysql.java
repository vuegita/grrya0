package com.inso.modules.passport.user.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.BuyVipOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class BuyVIPOrderDaoMysql extends DaoSupport implements BuyVipOrderDao {

    /**
     order_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     order_userid       		int(11) UNSIGNED NOT NULL ,
     order_vip_type       	varchar(50) NOT NULL comment 'vip 类型,检举类型',
     order_vipid       		int(11) UNSIGNED NOT NULL ,
     order_status             varchar(50) NOT NULL comment 'enable|disable',
     order_expires_time       datetime DEFAULT NULL comment '过期时间-保留参数',
     order_createtime         datetime DEFAULT NULL comment '时间',
     */
    private static final String TABLE = "inso_passport_buy_vip_order";

    @Override
    public void add(String orderno, UserAttr userAttr, VIPInfo vipInfo, OrderTxStatus status, BigDecimal amount)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_vip_type", vipInfo.getType());
        keyvalue.put("order_vipid", vipInfo.getId());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_status", status.getKey());
        keyvalue.put("order_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(String orderno, OrderTxStatus status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_status", status.getKey());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public BuyVipOrderInfo findByNo(String orderno)
    {
        StringBuilder sql = new StringBuilder("select A.*, B.vip_name as order_vip_name, B.vip_level as order_vip_level from " + TABLE).append(" as A");
        sql.append(" left join inso_web_vip as B on A.order_vipid = B.vip_id ");
        sql.append(" where order_no = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), BuyVipOrderInfo.class, orderno);
    }

    @Override
    public RowPager<BuyVipOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long agentid, long staffid, long userid, OrderTxStatus status, VIPType vipType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_web_vip as B on A.order_vipid = B.vip_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(orderno))
        {
            values.add(orderno);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and order_userid = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and order_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }

            if(agentid > 0)
            {
                values.add(agentid);
                whereSQLBuffer.append(" and order_agentid = ? ");
            }

            if(staffid > 0)
            {
                values.add(staffid);
                whereSQLBuffer.append(" and order_staffid = ? ");
            }

            if(vipType != null)
            {
                values.add(vipType.getKey());
                whereSQLBuffer.append(" and order_vip_type = ? ");
            }
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.*, B.vip_name as order_vip_name, B.vip_level as order_vip_level from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<BuyVipOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), BuyVipOrderInfo.class, values.toArray());
        RowPager<BuyVipOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

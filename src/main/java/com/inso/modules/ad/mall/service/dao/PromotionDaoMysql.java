package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.PromotionInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.UserAttrDaoMysql;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class PromotionDaoMysql extends DaoSupport implements PromotionDao {

    /**
     promotion_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     promotion_userid             int(11) NOT NULL DEFAULT 0,
     promotion_username           varchar(255) NOT NULL comment '商家',
     promotion_price              decimal(18,2) NOT NULL DEFAULT 0 comment '单价',
     promotion_total_amount       decimal(18,2) NOT NULL DEFAULT 0 comment '推广总金额',
     promotion_status             varchar(50) NOT NULL comment 'enable|disable',
     promotion_createtime         datetime DEFAULT NULL comment '创建时间',
     promotion_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

     */
    private static final String TABLE = "inso_ad_mall_merchant_promotion";

    @Override
    public void addOrder(UserInfo userInfo, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("promotion_userid", userInfo.getId());
        keyvalue.put("promotion_username", userInfo.getName());

        keyvalue.put("promotion_price", price);
        keyvalue.put("promotion_total_amount", totalAmount);

        keyvalue.put("promotion_status", status.getKey());
        keyvalue.put("promotion_createtime", date);

        if(jsonObject != null && !jsonObject.isEmpty())
        {
            keyvalue.put("promotion_remark", jsonObject.toJSONString());
        }
        else
        {
            keyvalue.put("promotion_remark", StringUtils.getEmpty());
        }
        persistent(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(long id, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        if(status != null)
        {
            setKeyValue.put("promotion_status", status.getKey());
        }

        if(price != null)
        {
            setKeyValue.put("promotion_price", price);
        }

        if(totalAmount != null)
        {
            setKeyValue.put("promotion_total_amount", totalAmount);
        }

        if(jsonObject != null)
        {
            setKeyValue.put("promotion_remark", jsonObject.toJSONString());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("promotion_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public PromotionInfo findByUserId(long userid)
    {
        String sql = "select * from " + TABLE + " where promotion_userid = ?";
        return mSlaveJdbcService.queryForObject(sql, PromotionInfo.class, userid);
    }

    @Override
    public RowPager<PromotionInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.promotion_userid = B.attr_userid ");

        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(pageVo.getFromTime()))
        {
            // 时间放前面
            whereSQLBuffer.append(" and promotion_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and promotion_userid = ? ");
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

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and promotion_status = ? ");
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
        select.append(" order by promotion_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<PromotionInfo> list = mSlaveJdbcService.queryForList(select.toString(), PromotionInfo.class, values.toArray());
        RowPager<PromotionInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

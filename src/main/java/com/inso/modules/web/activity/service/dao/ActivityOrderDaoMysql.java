package com.inso.modules.web.activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.model.ActivityOrderInfo;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamOrderInfo;
import com.inso.modules.web.team.service.dao.TeamOrderDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ActivityOrderDaoMysql extends DaoSupport implements ActivityOrderDao {

    /**
     order_no                     varchar(255) NOT NULL comment '内部系统-订单号',
     order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
     order_business_type          varchar(255) NOT NULL comment '业务类型',

     order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',

     order_userid                 int(11) NOT NULL,
     order_username               varchar(255) NOT NULL comment  '',
     order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname              varchar(255) NOT NULL comment  '',
     order_staffid                int(11) NOT NULL DEFAULT 0,
     order_staffname              varchar(255) NOT NULL comment  '',

     order_currency_type          varchar(255) NOT NULL comment  '',
     order_amount                 decimal(25,8) NOT NULL comment '流水金额',
     order_feemoney               decimal(25,8) NOT NULL comment '手续费',

     order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     order_createtime             datetime NOT NULL,
     order_remark                 varchar(3000) DEFAULT '',
     */
    private static final String TABLE = "inso_web_activity_order";

    @Override
    public void addOrder(ActivityInfo activityInfo, String orderno, String outTradeNo, UserAttr userAttr,
                         BigDecimal amount, ICurrencyType currency, OrderTxStatus txStatus, JSONObject remark)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("order_no", orderno);
        keyvalue.put("order_out_trade_no", StringUtils.getNotEmpty(outTradeNo));
        keyvalue.put("order_business_type", activityInfo.getBusinessType());
        keyvalue.put("order_activity_id", activityInfo.getId());

        keyvalue.put("order_userid", userAttr.getUserid());
        keyvalue.put("order_username", userAttr.getUsername());
        keyvalue.put("order_agentid", userAttr.getAgentid());
        keyvalue.put("order_agentname", userAttr.getAgentname());
        keyvalue.put("order_staffid", userAttr.getDirectStaffid());
        keyvalue.put("order_staffname", userAttr.getDirectStaffname());

        keyvalue.put("order_currency_type", currency.getKey());

        keyvalue.put("order_amount", amount);
        keyvalue.put("order_feemoney", BigDecimal.ZERO);

        keyvalue.put("order_status", txStatus.getKey());
        keyvalue.put("order_createtime", date);

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("order_remark", remark.toJSONString());
        }
        else
        {
            keyvalue.put("order_remark", StringUtils.getEmpty());
        }

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(String orderno, OrderTxStatus status, JSONObject remark)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("order_status", status.getKey());

        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("order_remark", remark.toJSONString());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("order_no", orderno);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public ActivityOrderInfo findById(String orderno)
    {
        String sql = "select * from " + TABLE + " where order_no = ?";
        return mSlaveJdbcService.queryForObject(sql, ActivityOrderInfo.class, orderno);
    }

    public void deleteById(String orderno)
    {
        String sql = "delete from " + TABLE + " where order_no = ?";
        mWriterJdbcService.executeUpdate(sql, orderno);
    }

    @Override
    public RowPager<ActivityOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, ActivityBusinessType businessType,  OrderTxStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(sysOrderno))
        {
            values.add(sysOrderno);
            whereSQLBuffer.append(" and order_no = ? ");
        }
        else
        {
            if(!StringUtils.isEmpty(pageVo.getFromTime()))
            {
                // 时间放前面
                whereSQLBuffer.append(" and order_createtime between ? and ? ");
                values.add(pageVo.getFromTime());
                values.add(pageVo.getToTime());
            }

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and order_userid = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and order_status = ? ");
            }

            if(businessType != null)
            {
                values.add(businessType.getKey());
                whereSQLBuffer.append(" and order_business_type = ? ");
            }
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

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        select.append(" order by order_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ActivityOrderInfo> list = mSlaveJdbcService.queryForList(select.toString(), ActivityOrderInfo.class, values.toArray());
        RowPager<ActivityOrderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

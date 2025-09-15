package com.inso.modules.web.promotion_channel.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.promotion_channel.model.PromotionChannelInfo;
import com.inso.modules.web.promotion_channel.model.PromotionChannelType;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.dao.TeamBuyingGroupDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class PromotionChannelDaoMysql extends DaoSupport implements PromotionChannelDao {

    /**
     channel_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
     channel_name               varchar(255) NOT NULL DEFAULT '',
     channel_type               varchar(255) NOT NULL DEFAULT '',
     channel_url                varchar(255) NOT NULL DEFAULT '',

     channel_agentid            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     channel_agentname          varchar(255) NOT NULL comment  '',
     channel_staffid            int(11) NOT NULL DEFAULT 0,
     channel_staffname          varchar(255) NOT NULL comment  '',

     channel_subscribe_count    int(11) NOT NULL DEFAULT 0,
     channel_view_count         int(11) NOT NULL DEFAULT 0,
     channel_amount             decimal(25,8) NOT NULL comment '推广费用',

     channel_status             varchar(50) NOT NULL comment '',
     channel_createtime         datetime DEFAULT NULL ,
     channel_remark             varchar(1000) NOT NULL comment '',
     */
    private static final String TABLE = "inso_web_system_promotion_channel";

    @Override
    public long add(String name, PromotionChannelType channelType, String url, UserAttr userAttr, String contract,
                    long subscribeCount, long viewCount, BigDecimal amount, Status status, String remark)
    {
        DateTime date = new DateTime();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("channel_name", StringUtils.getNotEmpty(name));
        keyvalue.put("channel_type", channelType.getKey());
        keyvalue.put("channel_url", StringUtils.getNotEmpty(url));
        keyvalue.put("channel_contract", StringUtils.getNotEmpty(contract));
        
        keyvalue.put("channel_subscribe_count", subscribeCount);
        keyvalue.put("channel_view_count", viewCount);
        keyvalue.put("channel_amount", BigDecimalUtils.getNotNull(amount));
        
        keyvalue.put("channel_agentid", userAttr.getAgentid());
        keyvalue.put("channel_agentname", userAttr.getAgentname());
        keyvalue.put("channel_staffid", userAttr.getUserid());
        keyvalue.put("channel_staffname", userAttr.getUsername());

        keyvalue.put("channel_status", status.getKey());
        keyvalue.put("channel_createtime", date.toDate());
        keyvalue.put("channel_remark", StringUtils.getNotEmpty(remark));

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    @Transactional
    public void updateInfo(long id, String name, long subscribeCount, long viewCount, String contact, BigDecimal amount, Status status, String remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(name != null)
        {
            setKeyValue.put("channel_name", name);
        }

        if(contact != null)
        {
            setKeyValue.put("channel_contact", contact);
        }

        if(subscribeCount >= 0)
        {
            setKeyValue.put("channel_subscribe_count", subscribeCount);
        }

        if(viewCount >= 0)
        {
            setKeyValue.put("channel_view_count", viewCount);
        }

        if(amount != null)
        {
            setKeyValue.put("channel_amount", amount);
        }

        if(status != null)
        {
            setKeyValue.put("channel_status", status.getKey());
        }

        if(remark != null)
        {
            setKeyValue.put("channel_remark", remark);
        }


        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("channel_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public PromotionChannelInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where channel_id = ?";
        return mSlaveJdbcService.queryForObject(sql, PromotionChannelInfo.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where channel_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }


    @Override
    public RowPager<PromotionChannelInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String name, PromotionChannelType channelType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and channel_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(!StringUtils.isEmpty(name))
        {
            values.add(name);
            whereSQLBuffer.append(" and channel_name = ?");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and channel_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and channel_staffid = ? ");
        }

        if(channelType != null)
        {
            values.add(channelType.getKey());
            whereSQLBuffer.append(" and channel_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and channel_status = ? ");
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
        select.append(" order by channel_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<PromotionChannelInfo> list = mSlaveJdbcService.queryForList(select.toString(), PromotionChannelInfo.class, values.toArray());
        RowPager<PromotionChannelInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }



}

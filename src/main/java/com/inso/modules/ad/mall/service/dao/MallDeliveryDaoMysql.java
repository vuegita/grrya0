package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallDeliveryInfo;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallDeliveryDaoMysql extends DaoSupport implements MallDeliveryDao {

    private static final String TABLE = "inso_ad_shipping_delivery";

    /**
     delivery_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     delivery_orderno            varchar(255) NOT NULL DEFAULT '' comment '商品订单号',
     delivery_index              int(11) NOT NULL DEFAULT 0 comment '',
     delivery_trackingno         varchar(255) NOT NULL DEFAULT '' comment '快递订单号',
     delivery_location           varchar(255) NOT NULL DEFAULT '' comment '当前已到达配送位置',
     delivery_status             varchar(50) NOT NULL comment 'enable|disable',
     delivery_createtime         datetime DEFAULT NOT NULL comment '创建时间',
     delivery_updatetime         datetime DEFAULT NULL comment '到达时间',
     delivery_remark             varchar(1024) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void add(String orderno, Status status, String location, boolean isFinish, Date createtime)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("delivery_orderno", orderno);
        keyvalue.put("delivery_location", location);
        keyvalue.put("delivery_is_finish", isFinish);
        keyvalue.put("delivery_status", status.getKey());
        keyvalue.put("delivery_createtime", createtime);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status, String location)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("delivery_status", status.getKey());

        if(!StringUtils.isEmpty(location))
        {
            setKeyValue.put("delivery_status", location);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("delivery_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MallDeliveryInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where delivery_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MallDeliveryInfo.class, id);
    }

    public List<MallDeliveryInfo> queryList(String orderno)
    {
        String sql = "select * from " + TABLE + " where delivery_orderno = ?";
        return mSlaveJdbcService.queryForList(sql, MallDeliveryInfo.class, orderno);
    }

    public void updateStatus(String orderno, Status status)
    {
        String sql = "update " + TABLE + " set delivery_status = ? where delivery_orderno = ? and delivery_status != ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), orderno, status.getKey());
    }

    public void queryAll(Callback<MallDeliveryInfo> callback, DateTime fromTime, DateTime toTime, Status status)
    {
        String fromStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        if(status != null)
        {
            String sql = "select * from " + TABLE + " where delivery_createtime between ? and ? and delivery_status = ?";
            mSlaveJdbcService.queryAll(callback, sql, MallDeliveryInfo.class, fromStr, toStr, status.getKey());
        }
        else
        {
            String sql = "select * from " + TABLE + " where delivery_createtime between ? and ? ";
            mSlaveJdbcService.queryAll(callback, sql, MallDeliveryInfo.class, fromStr, toStr);
        }
    }

    @Override
    public RowPager<MallDeliveryInfo> queryScrollPage(PageVo pageVo, String orderno, Status status, String trackno)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(!StringUtils.isEmpty(orderno))
        {
            values.add(orderno);
            whereSQLBuffer.append(" and delivery_orderno = ? ");
        }

//        if(!StringUtils.isEmpty(trackno))
//        {
//            values.add(orderno);
//            whereSQLBuffer.append(" and delivery_trackno = ? ");
//        }

        // 时间放前面
        if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and delivery_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }
        else
        {
            whereSQLBuffer.append(" and delivery_createtime >= ? ");
            values.add(DateTime.now().minusMonths(1).toDate());
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and delivery_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by delivery_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<MallDeliveryInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallDeliveryInfo.class, values.toArray());
        RowPager<MallDeliveryInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

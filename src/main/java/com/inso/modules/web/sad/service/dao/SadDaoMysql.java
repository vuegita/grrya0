package com.inso.modules.web.sad.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.sad.model.SadInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class SadDaoMysql extends DaoSupport implements SadDao {

    /**
     sad_key             varchar(255) NOT NULL comment  '所属代理',
     sad_type            varchar(255) NOT NULL comment  '广告类型',

     sad_title           varchar(255) NOT NULL comment  '广告标题',
     sad_content         varchar(255) NOT NULL comment  '广告内容',

     sad_ga_value        varchar(255) NOT NULL DEFAULT '' comment  '保底参数1',
     sad_gb1_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数2',
     sad_gb2_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数3',
     sad_gb3_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数4',
     sad_gb4_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数5',

     sad_status          varchar(20) NOT NULL,
     sad_createtime      datetime DEFAULT NULL ,
     */
    private static final String TABLE = "inso_web_test_sad";

    @Override
    public void addOrder(String key, MyDimensionType dimensionType, String title, String content, Status status, String gaValue, String gb1Value, String gb2Value)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("sad_key", key);
        keyvalue.put("sad_type", dimensionType.getKey());

        keyvalue.put("sad_title", title);
        keyvalue.put("sad_content", StringUtils.getNotEmpty(content));

        keyvalue.put("sad_ga_value", gaValue);
        keyvalue.put("sad_gb1_value", gb1Value);
        keyvalue.put("sad_gb2_value", gb2Value);
        keyvalue.put("sad_gb3_value", StringUtils.getEmpty());
        keyvalue.put("sad_gb4_value", StringUtils.getEmpty());

        keyvalue.put("sad_status", status.getKey());
        keyvalue.put("sad_createtime", new Date());

        persistent(TABLE, keyvalue);
    }

    public void delete(long id)
    {
        // record_pdate, record_business_type, record_agentid, record_staffid, record_fund_key, record_currency
        String sql = "delete from " + TABLE + " where sad_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public SadInfo find(long id)
    {
        // record_pdate, record_business_type, record_agentid, record_staffid, record_fund_key, record_currency
        String sql = "select * from " + TABLE + " where sad_id = ?";
        return mWriterJdbcService.queryForObject(sql, SadInfo.class, id);
    }


    @Override
    public RowPager<SadInfo> queryScrollPage(PageVo pageVo, String key, MyDimensionType dimensionType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and sad_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(!StringUtils.isEmpty(key))
        {
            values.add(key);
            whereSQLBuffer.append(" and sad_key = ? ");
        }

        if(dimensionType != null)
        {
            values.add(dimensionType.getKey());
            whereSQLBuffer.append(" and sad_type = ? ");
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + TABLE + " as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from " + TABLE + " as A ");
        select.append(whereSQL);
        select.append(" order by sad_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SadInfo> list = mSlaveJdbcService.queryForList(select.toString(), SadInfo.class, values.toArray());
        RowPager<SadInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

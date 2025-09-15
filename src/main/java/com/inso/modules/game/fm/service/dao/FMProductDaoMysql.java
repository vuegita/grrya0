package com.inso.modules.game.fm.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;

@Repository
public class FMProductDaoMysql extends DaoSupport implements FMProductDao {

    private static final String TABLE = "inso_game_financial_management_product";

    /**
     derivatives_id       		            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     derivatives_title                     varchar(20) NOT NULL comment '标题',
     derivatives_desc                      varchar(100) NOT NULL comment '介绍',
     derivatives_time_horizon              int(11) NOT NULL comment ' 投资期限（1日，3日，7日）',

     derivatives_return_expected_start   	decimal(18,2) NOT NULL comment '预期收益范围start',
     derivatives_return_expected_end	    decimal(18,2) NOT NULL comment '预期收益范围 end',
     derivatives_return_real_rate   		    decimal(18,2) NOT NULL comment '实际收益率',

     derivatives_sale_estimate             int(11) NOT NULL DEFAULT 0 comment '预售总份额',
     derivatives_sale_real                 int(11) NOT NULL DEFAULT 0 comment '实际总份额',
     derivatives_sale_actual               int(11) NOT NULL DEFAULT 0 comment '实际已售份额',

     derivatives_limit_min_sale            int(11) NOT NULL DEFAULT 0 comment '限售最小额度',
     derivatives_limit_max_sale            int(11) NOT NULL DEFAULT 0 comment '限售最大额度',
     derivatives_limit_min_bets            int(11) NOT NULL DEFAULT 0 comment '最低投注额',
     derivatives_limit_min_balance         decimal(18,2) NOT NULL comment '最低帐户余额',

     derivatives_status     			    varchar(50) NOT NULL comment 'new=草稿 | saling=销售中 | saled=已售磬 | finish=结束' ,

     product_createtime       		    datetime NOT NULL comment '创建时间',
     product_begin_sale_time           datetime NOT NULL comment '开售时间',
     product_end_sale_time             datetime NOT NULL comment '停售时间',
     product_endtime                   datetime NOT NULL comment '结束时间',
     derivatives_remark             	    varchar(1000) DEFAULT '',

     */

    public long add(String tile, String desc, long timeHorizon, FMType fmType,
                    BigDecimal return_expected_start, BigDecimal return_expected_end, BigDecimal return_real_rate,
                    long sale_estimate, long sale_real,
                    long limitMinSale, long limitMaxSale, long limitMinBets, BigDecimal limitMinBalance, FMProductStatus status,
                    DateTime beginSaleTime, DateTime endSaleTime)
    {
        DateTime nowTime = new DateTime();
        Date createtime = nowTime.toDate();
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("product_title", tile);
        keyvalue.put("product_desc", desc);
        keyvalue.put("product_time_horizon", timeHorizon);

        keyvalue.put("product_type", fmType.getKey());

        keyvalue.put("product_return_expected_start", return_expected_start);
        keyvalue.put("product_return_expected_end", return_expected_end);
        keyvalue.put("product_return_real_rate", return_real_rate);

        keyvalue.put("product_sale_estimate", sale_estimate);
        keyvalue.put("product_sale_real", sale_real);
//        keyvalue.put("product_sale_actual", totalCount);

        keyvalue.put("product_limit_min_sale", limitMinSale);
        keyvalue.put("product_limit_max_sale", limitMaxSale);
        keyvalue.put("product_limit_min_bets", limitMinBets);
        keyvalue.put("product_limit_min_balance", limitMinBalance);

        keyvalue.put("product_status", status.getKey());

        keyvalue.put("product_createtime", new Date());
        keyvalue.put("product_begin_sale_time", beginSaleTime.toDate());
        keyvalue.put("product_end_sale_time", endSaleTime.toDate());

        // 下午3点后结算
        DateTime endTime = endSaleTime.plusDays((int)timeHorizon + 1);
        keyvalue.put("product_endtime", endTime.toDate());


        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateBasicInfo(long id, String title, String desc, BigDecimal realRate, FMProductStatus status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("product_title", title);
        setKeyValue.put("product_desc", desc);
        setKeyValue.put("product_status", status.getKey());

        if(realRate != null)
        {
            setKeyValue.put("product_return_real_rate", realRate);
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("product_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateStatusToResult(long id, BigDecimal return_real_rate, FMProductStatus status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(return_real_rate != null)
        {
            setKeyValue.put("product_return_real_rate", return_real_rate);
        }

//        setKeyValue.put("product_sale_actual", sale_actual);
        setKeyValue.put("product_status", status.getKey());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("product_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateSaleActual(long id, long saleActual, boolean isAdd)
    {
        String sql = null;
        if(isAdd)
        {
            sql = "update " + TABLE + " set product_sale_actual = product_sale_actual + ? where product_id = ?";
        }
        else
        {
            sql = "update " + TABLE + " set product_sale_actual = product_sale_actual - ? where product_id = ?";
        }
        mWriterJdbcService.executeUpdate(sql, saleActual, id);
    }

    public void updateSaleActualAndInterest(long id, long saleActual, BigDecimal interestAmount)
    {
        String sql = "update " + TABLE + " set product_sale_actual = ?, product_return_real_interest =? where product_id = ?";
        mWriterJdbcService.executeUpdate(sql, saleActual, interestAmount, id);
    }

    public FMProductInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where product_id = ?";
        return mSlaveJdbcService.queryForObject(sql, FMProductInfo.class, id);
    }

    public List<FMProductInfo> queryByTime(String beginTime, String endTime, int limit)
    {
        String sql = "select * from " + TABLE + " where product_begin_sale_time between ? and ? order by product_begin_sale_time desc ";
        if(limit > 0)
        {
            sql += " limit " + limit;
        }
        return mSlaveJdbcService.queryForList(sql, FMProductInfo.class, beginTime, endTime);
    }

    public void queryAllByStartSaleTime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback)
    {
        String sql = "select * from " + TABLE + " where product_begin_sale_time between ? and ? order by product_createtime desc";
        mSlaveJdbcService.queryAll(callback, sql, FMProductInfo.class, startTimeString, endTimeString);
    }

    public void queryAllByUpdateTime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback)
    {
        String sql = "select * from " + TABLE + " where product_end_sale_time between ? and ?";
        mSlaveJdbcService.queryAll(callback, sql, FMProductInfo.class, startTimeString, endTimeString);
    }

    public RowPager<FMProductInfo> queryScrollPage(PageVo pageVo, long id, long userid, FMProductStatus status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append(" and product_id = ? ");
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and product_begin_sale_time between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            // 0 表示系统
            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and product_userid = ? ");
            }

            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and product_status = ? ");
            }

        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_game_financial_management_product " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from inso_game_financial_management_product ");
        select.append(whereSQL);
        select.append(" order by product_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<FMProductInfo> list = mSlaveJdbcService.queryForList(select.toString(), FMProductInfo.class, values.toArray());
        RowPager<FMProductInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}


package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class SettleWithdrawOrderReportDaoMysql extends DaoSupport implements SettleWithdrawOrderReportDao {

    /**
     or_id                  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     or_orderno             varchar(255) NOT NULL comment '内部系统-订单号',
     or_reportid            int(11) NOT NULL,

     or_createtime          datetime NOT NULL,
    */

    private static final String TABLE = "inso_web_settle_withdraw_order_report";

    @Override
    public void addOrder( String settleOrderNo,long reportid)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        Date date =new Date();

        keyvalue.put("or_orderno", settleOrderNo);
        keyvalue.put("or_reportid", reportid);

        keyvalue.put("or_createtime", date);

        persistent(TABLE, keyvalue);
    }

    @Override
    public void delete(long id)
    {
        String sql = "delete from " + TABLE + " where or_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }


//    @Override
//    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid)
//    {
//        List<Object> values = Lists.newArrayList();
//        StringBuilder whereSQLBuffer = new StringBuilder();
//        whereSQLBuffer.append(" where 1 = 1 ");
//
//        // 时间放前面
//        whereSQLBuffer.append(" and record_pdate between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());
//
//        if(agentid > 0)
//        {
//            values.add(agentid);
//            whereSQLBuffer.append(" and record_agentid = ? ");
//        }
//
//
//        String whereSQL = whereSQLBuffer.toString();
//        String countsql = "select count(1) from " + TABLE + " as A " + whereSQL;
//        long total = mSlaveJdbcService.count(countsql, values.toArray());
//
//        if(total == 0)
//        {
//            return RowPager.getEmptyRowPager();
//        }
//
//        StringBuilder select = new StringBuilder("select * from " + TABLE + " as A ");
//        select.append(whereSQL);
//        select.append(" order by report_createtime desc ");
//        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
//        List<SettleWithdrawReportInfo> list = mSlaveJdbcService.queryForList(select.toString(), SettleWithdrawReportInfo.class, values.toArray());
//        RowPager<SettleWithdrawReportInfo> rowPage = new RowPager<>(total, list);
//        return rowPage;
//    }

}

package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class SettleWithdrawReportDaoMysql extends DaoSupport implements SettleWithdrawReportDao {

    /**
    report_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    report_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
    report_agentname              varchar(255) NOT NULL comment  '',

    report_createtime             datetime NOT NULL,
    report_remark                 varchar(5000) NOT NULL DEFAULT '' comment '',
    */

    private static final String TABLE = "inso_web_settle_withdraw_report";

    @Override
    public long addOrder(SettleOrderInfo recordInfo, JSONArray remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        Date date =new Date();

        keyvalue.put("report_agentid", recordInfo.getAgentid());
        keyvalue.put("report_agentname", StringUtils.getNotEmpty(recordInfo.getAgentname()));


        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("report_remark", remark.toJSONString());
        }

        keyvalue.put("report_createtime", date);

       return persistentOfReturnPK(TABLE, keyvalue);
    }

    @Override
    public void delete(long id)
    {
        String sql = "delete from " + TABLE + " where report_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }


    @Override
    public RowPager<SettleWithdrawReportInfo> queryScrollPagequeryScrollPage(PageVo pageVo, long agentid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and report_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and report_agentid = ? ");
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
        select.append(" order by report_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SettleWithdrawReportInfo> list = mSlaveJdbcService.queryForList(select.toString(), SettleWithdrawReportInfo.class, values.toArray());
        RowPager<SettleWithdrawReportInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

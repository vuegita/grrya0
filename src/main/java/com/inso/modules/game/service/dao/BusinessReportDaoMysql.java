package com.inso.modules.game.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.BusinessReport;

@Repository
public class BusinessReportDaoMysql extends DaoSupport implements BusinessReportDao {

    private static String TABLE = "inso_report_business_day";

    /**
     day_pdate	 				    date NOT NULL ,
     day_key                       varchar(50) NOT NULL comment '业务唯一编码',
     day_title 	                varchar(50) NOT NULL comment  '业务名称',
     day_bet_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_bet_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_win_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_win_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_feemoney             	    decimal(18,2) DEFAULT 0 NOT NULL comment '手续费-提现才有',
     day_remark         	        varchar(1000) NOT NULL comment  '',
     */

    public void addReport(Date pdate, GameChildType childType, BusinessReport report, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("day_pdate", pdate);
        keyvalue.put("day_key", childType.getKey());
        keyvalue.put("day_title", childType.getTitle());
        keyvalue.put("day_bet_amount", report.getBetAmount());
        keyvalue.put("day_bet_count", report.getBetCount());
        keyvalue.put("day_win_amount", report.getWinAmount());
        keyvalue.put("day_win_amount2", report.getWinAmount2());
        keyvalue.put("day_win_count", report.getWinCount());
        keyvalue.put("day_feemoney", report.getFeemoney());

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("day_remark", FastJsonHelper.jsonEncode(remark));
        }
        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, GameChildType childType)
    {
        String sql = "delete from " + TABLE + " where day_pdate = ? and day_key = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, childType.getKey());
    }

    public RowPager<BusinessReport> queryScrollPage(PageVo pageVo, String key)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
        whereSQLBuffer.append(" and day_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(!StringUtils.isEmpty(key))
        {
            values.add(key);
            whereSQLBuffer.append(" and day_key = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by day_pdate desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<BusinessReport> list = mSlaveJdbcService.queryForList(select.toString(), BusinessReport.class, values.toArray());
        RowPager<BusinessReport> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

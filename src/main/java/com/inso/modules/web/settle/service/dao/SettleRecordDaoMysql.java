package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class SettleRecordDaoMysql extends DaoSupport implements SettleRecordDao {

    /**
     *   record_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   record_pdate                 date NOT NULL ,
     *   record_business_type         varchar(20) NOT NULL comment '业务类型',
     *
     *   record_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     *   record_agentname             varchar(255) NOT NULL comment  '',
     *   record_staffid               int(11) NOT NULL DEFAULT 0,
     *   record_staffname             varchar(255) NOT NULL comment  '',
     *
     *   record_fund_key              varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
     *   record_currency              varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',
     *
     *   record_amount                decimal(25,8) NOT NULL comment '流水金额',
     *   record_feemoney              decimal(25,8) NOT NULL comment '手续费',
     *   record_remark                varchar(3000) NOT NULL DEFAULT '' comment '',
     */
    private static final String TABLE = "inso_web_settle_record";

    @Override
    public void addOrder(Date pdate, SettleBusinessType businessType,
                         SettleRecordInfo recordInfo, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("record_pdate", pdate);
        keyvalue.put("record_business_type", businessType.getKey());

        keyvalue.put("record_agentid", recordInfo.getAgentid());
        keyvalue.put("record_agentname", StringUtils.getNotEmpty(recordInfo.getAgentname()));
        keyvalue.put("record_staffid", recordInfo.getStaffid());
        keyvalue.put("record_staffname", StringUtils.getNotEmpty(recordInfo.getStaffname()));

        keyvalue.put("record_currency", recordInfo.getCurrency());

        keyvalue.put("record_amount", recordInfo.getAmount());
        keyvalue.put("record_feemoney", BigDecimalUtils.getNotNull(recordInfo.getFeemoney()));

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("record_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void delete(Date pdate, SettleBusinessType businessType, long agentid, long staffid, ICurrencyType currencyType)
    {
        // record_pdate, record_business_type, record_agentid, record_staffid, record_fund_key, record_currency
        String sql = "delete from " + TABLE + " where record_pdate = ? and record_business_type = ?  and record_agentid = ? and record_staffid = ? and record_currency = ?";
        mWriterJdbcService.executeUpdate(sql, pdate, businessType.getKey(), agentid, staffid, currencyType.getKey());
    }


    @Override
    public RowPager<SettleRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ICurrencyType currencyType, SettleBusinessType businessType, String dimensionType)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and record_pdate between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and record_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and record_staffid = ? ");
        }

        if(currencyType != null)
        {
            values.add(currencyType.getKey());
            whereSQLBuffer.append(" and record_currency = ? ");
        }
        if(businessType != null)
        {
            values.add(businessType.getKey());
            whereSQLBuffer.append(" and record_business_type = ? ");
        }
        if("agent".equalsIgnoreCase(dimensionType))
        {
            whereSQLBuffer.append(" and record_staffid = 0 ");
        }
        else if("staff".equalsIgnoreCase(dimensionType))
        {
            whereSQLBuffer.append(" and record_staffid > 0 ");
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
        select.append(" order by record_pdate desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<SettleRecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), SettleRecordInfo.class, values.toArray());
        RowPager<SettleRecordInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

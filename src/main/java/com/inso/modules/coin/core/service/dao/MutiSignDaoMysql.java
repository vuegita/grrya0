package com.inso.modules.coin.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutisignInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MutiSignDaoMysql extends DaoSupport implements MutiSignDao {

    /**
     mutisign_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     mutisign_userid               int(11) NOT NULL,
     mutisign_username             varchar(255) NOT NULL ,

     mutisign_sender_address       varchar(255) NOT NULL,
     mutisign_currency_type        varchar(255) NOT NULL comment '所属代币',
     mutisign_balance              decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额',

     mutisign_status               varchar(20) NOT NULL comment '授权状态',
     mutisign_createtime           datetime DEFAULT NULL ,
     mutisign_remark               varchar(1000) DEFAULT '',
     */
    private static final String TABLE = "inso_coin_mutisign";

    @Override
    public void add(CoinAccountInfo accountInfo, CryptoNetworkType networkType, CryptoCurrency currency, BigDecimal balance, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("mutisign_userid", accountInfo.getUserid());
        keyvalue.put("mutisign_username", accountInfo.getUsername());

        keyvalue.put("mutisign_sender_address", accountInfo.getAddress());
        keyvalue.put("mutisign_network_type", networkType.getKey());
        keyvalue.put("mutisign_currency_type", currency.getKey());
        keyvalue.put("mutisign_balance", BigDecimalUtils.getNotNull(balance));

        keyvalue.put("mutisign_status", status.getKey());

        keyvalue.put("mutisign_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, BigDecimal balance, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(balance != null)
        {
            setKeyValue.put("mutisign_balance", balance);
        }

        if(status != null)
        {
            setKeyValue.put("mutisign_status", status.getKey());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("mutisign_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateStatus(String address, Status status)
    {
        String sql = "update " + TABLE + " set mutisign_status = ? where mutisign_sender_address = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), address);
    }


    public MutisignInfo findById(long id)
    {
        StringBuilder sql = new StringBuilder("select * from ").append(TABLE);
        sql.append(" where mutisign_id = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), MutisignInfo.class, id);
    }

    public MutisignInfo findByAddress(String address, CryptoCurrency currency)
    {
        String sql = "select * from " + TABLE + " where mutisign_sender_address = ? and mutisign_currency_type = ?";
        return mSlaveJdbcService.queryForObject(sql, MutisignInfo.class, address, currency.getKey());
    }

    @Override
    public RowPager<MutisignInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency currency, Status status, long agentid , long staffid)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_passport_user_attr as B on A.mutisign_userid = B.attr_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and mutisign_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and mutisign_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and B.attr_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and B.attr_direct_staffid = ? ");
        }

        if(currency != null)
        {
            values.add(currency.getKey());
            whereSQLBuffer.append(" and C.currency_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and mutisign_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.attr_direct_staffname as mutisign_staffname, B.attr_agentname as mutisign_agentname ");
        select.append(whereSQL);

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MutisignInfo> list = mSlaveJdbcService.queryForList(select.toString(), MutisignInfo.class, values.toArray());
        RowPager<MutisignInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAll(Callback<MutisignInfo> callback, DateTime fromTime, DateTime toTime)
    {
        StringBuilder select = new StringBuilder("select A.* ");
        select.append(" from ").append(TABLE).append(" as A ");
        select.append(" left join inso_passport_user_attr as B on A.mutisign_userid = B.attr_userid ");

        if(fromTime != null && toTime != null)
        {
            String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
            String toTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

            select.append(" where mutisign_createtime between ? and ?");

            mSlaveJdbcService.queryAll(callback, select.toString(), MutisignInfo.class, fromTimeString, toTimeString);
        }
        else
        {
            mSlaveJdbcService.queryAll(callback, select.toString(), MutisignInfo.class);
        }

    }

}

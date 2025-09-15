package com.inso.modules.coin.binance_activity.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class BARecordDaoMysql extends DaoSupport implements BARecordDao {

    /**
     record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_contract_id            int(11) NOT NULL comment '合约id',
     record_network_type           varchar(255) NOT NULL comment  '',
     record_currency_type          varchar(255) NOT NULL comment '投资币种',

     record_userid                 int(11) UNSIGNED NOT NULL comment '用户id',
     record_username               varchar(255) NOT NULL comment  '',
     record_address                varchar(255) NOT NULL DEFAULT '' comment '用户地址',

     record_status                 varchar(20) NOT NULL comment '状态',
     record_createtime             datetime NOT NULL comment '创建时间',
     record_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_binance_activity_mining_record";

    @Override
    public long add(ContractInfo contractInfo, UserInfo userInfo, String address, Status status)
    {
        DateTime dateTime = new DateTime();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("record_contractid", contractInfo.getId());
        keyvalue.put("record_network_type", contractInfo.getNetworkType());
        keyvalue.put("record_currency_type", contractInfo.getCurrencyType());

        keyvalue.put("record_userid", userInfo.getId());
        keyvalue.put("record_username", userInfo.getName());
        keyvalue.put("record_address", address);

        keyvalue.put("record_total_reward_amount", BigDecimal.ZERO);

        keyvalue.put("record_status", status.getKey());
        keyvalue.put("record_createtime", dateTime.toDate());
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public void updateInfo(long id, BigDecimal totalRewardAmount, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("record_status", status.getKey());
        }

//        if(rewardBalance != null)
//        {
//            setKeyValue.put("record_reward_balance", rewardBalance);
//        }
//
        if(totalRewardAmount != null)
        {
            setKeyValue.put("record_total_reward_amount", totalRewardAmount);
        }
//
//        if(invesAmount != null)
//        {
//            setKeyValue.put("record_inves_total_amount", invesAmount);
//            setKeyValue.put("record_endtime", endTime);
//        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("record_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public BARecordInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where record_id = ?";
        return mSlaveJdbcService.queryForObject(sql, BARecordInfo.class, id);
    }

    public BARecordInfo findByUseridAndContractid(long userid, long contractid)
    {
        String sql = "select * from " + TABLE + "  where record_userid = ? and record_contractid = ?";
        return mSlaveJdbcService.queryForObject(sql, BARecordInfo.class, userid, contractid);
    }

    public List<BARecordInfo> queryByUser(long userid)
    {
//        String sql = "select * from " + TABLE + "  where record_userid = ?";
//        return mSlaveJdbcService.queryForList(sql, MiningRecordInfo.class, userid);


        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
//        whereSQLBuffer.append(" left join inso_coin_defi_mining_product as B on B.product_id =A.record_product_id ");
     //   whereSQLBuffer.append(" left join inso_passport_user_money as C on C.money_userid =A.record_userid ");
//        whereSQLBuffer.append(" and C.money_fund_key = ? and C.money_currency = A.record_quote_currency ");
//        values.add(FundAccountType.Spot);

        whereSQLBuffer.append(" where 1 = 1 ");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.record_userid = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        StringBuilder select = new StringBuilder("select A.* ");
//        select.append(", C.money_balance as record_money_balance ");
//        select.append(", B.product_expected_rate as record_expected_rate ");
        select.append(whereSQL);
        select.append(" order by record_createtime desc ");
        List<BARecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), BARecordInfo.class, values.toArray());
        return list;


    }

    @Override
    public RowPager<BARecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid )
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
//        whereSQLBuffer.append(" left join ").append(MiningProductDaoMysql.TABLE).append(" as B on A.record_product_id = B.product_id ");
        whereSQLBuffer.append(" left join inso_passport_user_attr as C on C.attr_userid=A.record_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
        whereSQLBuffer.append(" and record_createtime between ? and ? ");
        values.add(pageVo.getFromTime());
        values.add(pageVo.getToTime());


        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and A.record_userid = ? ");
        }

        if(agentid > 0)
        {
            values.add(agentid);
            whereSQLBuffer.append(" and C.attr_agentid = ? ");
        }

        if(staffid > 0)
        {
            values.add(staffid);
            whereSQLBuffer.append(" and C.attr_direct_staffid = ? ");
        }

        if(quoteCurrency != null)
        {
            values.add(quoteCurrency.getKey());
            whereSQLBuffer.append(" and B.product_currency = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and record_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by record_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<BARecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), BARecordInfo.class, values.toArray());
        RowPager<BARecordInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    public void queryAll(Callback<BARecordInfo> callback)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
//        whereSQLBuffer.append(" left join ").append(CloudProductDaoMysql.TABLE).append(" as B on A.record_product_id = B.product_id ");
//        whereSQLBuffer.append(" left join inso_passport_user_attr as B on B.attr_userid=A.record_userid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        String whereSQL = whereSQLBuffer.toString();

        StringBuilder select = new StringBuilder("select A.* ");
//        select.append(", B.attr_agentid as record_agentid, B.attr_agentname as record_agentname ");
//        select.append(", B.product_daily_rate as record_daily_rate ");

        select.append(whereSQL);
        mSlaveJdbcService.queryAll(callback, select.toString(), BARecordInfo.class, values.toArray());
    }


}

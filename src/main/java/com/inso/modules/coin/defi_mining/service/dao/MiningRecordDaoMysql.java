package com.inso.modules.coin.defi_mining.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MiningRecordDaoMysql extends DaoSupport implements MiningRecordDao{

    /**
     record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_record_id             int(11) NOT NULL ,
     record_account_id             int(11) NOT NULL ,

     record_userid                 int(11) NOT NULL ,
     record_username 	            varchar(255) NOT NULL comment '用户名',
     record_address 	            varchar(255) NOT NULL comment '用户地址',

     record_reward_amount          decimal(25,8) NOT NULL DEFAULT 0 comment '收益金额',

     record_status                varchar(20) NOT NULL comment '状态',
     record_createtime  	        datetime DEFAULT NULL ,
     record_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_defi_mining_record";

    @Override
    public long add(CoinAccountInfo accountInfo, MiningProductInfo productInfo, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("record_product_id", productInfo.getId());

        keyvalue.put("record_contractid", productInfo.getContractid());

        keyvalue.put("record_network_type", productInfo.getNetworkType());
        keyvalue.put("record_base_currency", productInfo.getBaseCurrency());
        keyvalue.put("record_quote_currency", productInfo.getQuoteCurrency());

        keyvalue.put("record_userid", accountInfo.getUserid());
        keyvalue.put("record_username", accountInfo.getUsername());

        keyvalue.put("record_address", accountInfo.getAddress());
        keyvalue.put("record_reward_balance", BigDecimal.ZERO);
        keyvalue.put("record_total_reward_amount", BigDecimal.ZERO);

        // 质押配置
        keyvalue.put("record_staking_status", Status.DISABLE.getKey());
        keyvalue.put("record_staking_amount", BigDecimal.ZERO);
        // 默认48小时
        keyvalue.put("record_staking_reward_hour", 0);

        keyvalue.put("record_status", status.getKey());

        keyvalue.put("record_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public void updateInfo(long id, Status status, BigDecimal rewardAmount,
                           Status stakingStatus, StakingSettleMode settleMode, BigDecimal stakingAmount, BigDecimal stakingRewardAmount, BigDecimal stakingRewardExternal, long stakingHour,
                           BigDecimal voucherNodeValue, StakingSettleMode voucherNodeSettleMode,
                           BigDecimal voucherStakingValue)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("record_status", status.getKey());
        }

        if(rewardAmount != null)
        {
            setKeyValue.put("record_total_reward_amount", rewardAmount);
        }

        if(stakingRewardAmount != null)
        {
            setKeyValue.put("record_staking_reward_value", stakingRewardAmount);
        }

        if(stakingRewardExternal != null)
        {
            setKeyValue.put("record_staking_reward_external", stakingRewardExternal);
        }

        if(stakingStatus != null)
        {
            setKeyValue.put("record_staking_status", stakingStatus.getKey());
        }

        if(settleMode != null)
        {
            setKeyValue.put("record_staking_settle_mode", settleMode.getKey());
        }

        if(stakingAmount != null)
        {
            setKeyValue.put("record_staking_amount", stakingAmount);
        }

        if(stakingHour >= 0)
        {
            setKeyValue.put("record_staking_reward_hour", stakingHour);
        }

        if(voucherNodeValue != null)
        {
            setKeyValue.put("record_voucher_node_value", voucherNodeValue);
        }

        if(voucherNodeSettleMode != null)
        {
            setKeyValue.put("record_voucher_node_settle_mode", voucherNodeSettleMode.getKey());
        }

        if(voucherStakingValue != null)
        {
            setKeyValue.put("record_voucher_staking_value", voucherStakingValue);
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("record_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateTotalRewardAmount(long id, BigDecimal newTotalRewardAmount)
    {
        String sql = "update " + TABLE + " set record_total_reward_amount = ? where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, newTotalRewardAmount, id);
    }

    public MiningRecordInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where record_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MiningRecordInfo.class, id);
    }

    public MiningRecordInfo findByAccountIdAndProductId(long accoundid, long productid)
    {
        String sql = "select * from " + TABLE + "  where record_userid = ? and record_product_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MiningRecordInfo.class, accoundid, productid);
    }

    public List<MiningRecordInfo> queryByUser(long userid)
    {
//        String sql = "select * from " + TABLE + "  where record_userid = ?";
//        return mSlaveJdbcService.queryForList(sql, MiningRecordInfo.class, userid);


        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_coin_defi_mining_product as B on B.product_id =A.record_product_id ");
        whereSQLBuffer.append(" left join inso_coin_token_approve_auth as D on A.record_userid = D.auth_userid and A.record_contractid =D.auth_contract_id ");
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
        select.append(", B.product_expected_rate as record_expected_rate ");
        select.append(", D.auth_balance as record_wallet_balance ");
        select.append(whereSQL);
        select.append(" order by record_createtime desc ");
        List<MiningRecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), MiningRecordInfo.class, values.toArray());
        return list;


    }

    @Override
    public RowPager<MiningRecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status stakingStatus, Status status, long agentid, long staffid )
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(MiningProductDaoMysql.TABLE).append(" as B on A.record_product_id = B.product_id ");
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

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and B.product_network_type = ? ");
        }

        if(quoteCurrency != null)
        {
            values.add(quoteCurrency.getKey());
            whereSQLBuffer.append(" and B.product_quote_currency = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and record_status = ? ");
        }

        if(stakingStatus != null)
        {
            values.add(stakingStatus.getKey());
            whereSQLBuffer.append(" and record_staking_status = ? ");
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
        List<MiningRecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), MiningRecordInfo.class, values.toArray());
        RowPager<MiningRecordInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    public void queryAll(Callback<MiningRecordInfo> callback)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(MiningProductDaoMysql.TABLE).append(" as B on A.record_product_id = B.product_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        String whereSQL = whereSQLBuffer.toString();

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.product_expected_rate as record_expected_rate ");
        select.append(", B.product_min_wallet_balance as record_min_wallet_balance ");

        select.append(whereSQL);
        mSlaveJdbcService.queryAll(callback, select.toString(), MiningRecordInfo.class, values.toArray());
    }


}

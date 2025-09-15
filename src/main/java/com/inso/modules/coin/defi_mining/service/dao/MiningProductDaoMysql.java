package com.inso.modules.coin.defi_mining.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.dao.ContractDaoMysql;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MiningProductDaoMysql extends DaoSupport implements MiningProductDao{

    /**
     product_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     product_name 	                varchar(255) NOT NULL comment '产品名称',
     product_network_type          varchar(255) NOT NULL comment '网络类型',

     product_base_currency         varchar(255) NOT NULL comment 'ERC20-TRC20相关token',
     product_quote_currency        varchar(255) NOT NULL comment '收益稳定币=USDT|USDC|USDP',

     product_min_withdral_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '最小提现金额',
     product_min_wallet_balance    decimal(25,8) NOT NULL DEFAULT 0 comment '最小钱包余额',
     product_expected_rate         decimal(18,3) NOT NULL DEFAULT 0 comment '预期收益率',
     product_reward_period         int(11) NOT NULL DEFAULT 0 comment '收益日期',

     product_network_type_sort     int(11) NOT NULL DEFAULT 0 comment '网络类型排序',
     product_base_currency_sort    int(11) NOT NULL DEFAULT 0 comment '币种类型',

     product_status                varchar(20) NOT NULL comment '状态',
     product_createtime  	        datetime DEFAULT NULL ,
     product_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    public static final String TABLE = "inso_coin_defi_mining_product";

    @Override
    public long add(ContractInfo contractInfo, String name, CryptoCurrency baseCurrency,
                    BigDecimal minWithdrawAmount, BigDecimal minWalletBalance, BigDecimal expectedRate,
                    long networkTypeSort, long quoteCurrencySort, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("product_contractid", contractInfo.getId());

        keyvalue.put("product_name", name);
        keyvalue.put("product_network_type", contractInfo.getNetworkType());

        keyvalue.put("product_base_currency", baseCurrency.getKey());
        keyvalue.put("product_quote_currency", contractInfo.getCurrencyType());

        keyvalue.put("product_min_withdraw_amount", minWithdrawAmount);
        keyvalue.put("product_min_wallet_balance", minWalletBalance);
        keyvalue.put("product_expected_rate", expectedRate);
        keyvalue.put("product_reward_period", 0);

        keyvalue.put("product_network_type_sort", networkTypeSort);
        keyvalue.put("product_quote_currency_sort", quoteCurrencySort);

        keyvalue.put("product_status", status.getKey());

        keyvalue.put("product_createtime", date);
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateInfo(long id, String name, BigDecimal minWithdrawAmount, BigDecimal minWalletBalance, long networkTypeSort, long baseCurrencySort, BigDecimal expectedRate, Status status)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(name))
        {
            setKeyValue.put("product_name", name);
        }

        if(minWithdrawAmount != null)
        {
            setKeyValue.put("product_min_withdraw_amount", minWithdrawAmount);
        }
        if(minWalletBalance != null)
        {
            setKeyValue.put("product_min_wallet_balance", minWalletBalance);
        }
        if(expectedRate != null)
        {
            setKeyValue.put("product_expected_rate", expectedRate);
        }

        setKeyValue.put("product_network_type_sort", networkTypeSort);
        setKeyValue.put("product_quote_currency_sort", baseCurrencySort);

        if(status != null)
        {
            setKeyValue.put("product_status", status.getKey());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("product_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MiningProductInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where product_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MiningProductInfo.class, id);
    }

    public MiningProductInfo findByCurrencyAndNetwork(CryptoCurrency baseCurrency, CryptoNetworkType networkType)
    {
        String sql = "select * from " + TABLE + "  where product_base_currency = ? and  product_network_type = ?";
        return mSlaveJdbcService.queryForObject(sql, MiningProductInfo.class, baseCurrency.getKey(), networkType.getKey());
    }

    @Override
    public RowPager<MiningProductInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status status)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and product_network_type = ? ");
        }


        if(quoteCurrency != null)
        {
            values.add(quoteCurrency.getKey());
            whereSQLBuffer.append(" and product_quote_currency = ? ");
        }
//
//        if(chainType != null)
//        {
//            values.add(chainType.getKey());
//            whereSQLBuffer.append(" and product_chain_type = ? ");
//        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and product_status = ? ");
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
        select.append(" order by product_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<MiningProductInfo> list = mSlaveJdbcService.queryForList(select.toString(), MiningProductInfo.class, values.toArray());
        RowPager<MiningProductInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public List<MiningProductInfo> queryAllList(Status status)
    {
        //
        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.contract_currency_ctr_addr as product_quote_currency_ctr_addr ");
        select.append(", B.contract_address as product_approve_ctr_address ");

        select.append("from ").append(TABLE).append(" as A ");
        select.append("left join ").append(ContractDaoMysql.TABLE).append(" as B on A.product_contractid = B.contract_id ");
        select.append(" where product_status = ?");

        return mSlaveJdbcService.queryForList(select.toString(), MiningProductInfo.class, status.getKey());
    }

    public void queryAll(Callback<MiningProductInfo> callback)
    {
        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.contract_currency_ctr_addr as product_quote_currency_ctr_addr ");
        select.append(", B.contract_address as product_approve_ctr_address ");

        select.append("from ").append(TABLE).append(" as A ");
        select.append("left join ").append(ContractDaoMysql.TABLE).append(" as B on A.product_contractid = B.contract_id ");

        mSlaveJdbcService.queryAll(callback, select.toString(), MiningProductInfo.class);
    }

}

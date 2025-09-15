package com.inso.modules.coin.approve.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ContractDaoMysql extends DaoSupport implements ContractDao {

    /**
     contract_address 	           varchar(255) NOT NULL comment '合约地址',
     contract_chain_type	       varchar(255) NOT NULL comment '链',

     contract_trigger_private_key  varchar(500) NOT NULL comment '调用者私钥',
     contract_trigger_address 	   varchar(255) NOT NULL comment '调用者地址',

     contract_status              varchar(20) NOT NULL comment '状态',
     contract_createtime  	       datetime DEFAULT NULL ,
     contract_remark              varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    public static final String TABLE = "inso_coin_contract";

    @Override
    public void add(String desc, String address, CryptoNetworkType networkType,
                    CryptoCurrency currency, String currencyCtrAddr, CryptoChainType currencyChaintType,
                    String triggerPrivateKey, String triggerAddress, Status status, RemarkVO remarkVO)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("contract_desc", StringUtils.getNotEmpty(desc));
        keyvalue.put("contract_address", address);
        keyvalue.put("contract_network_type", networkType.getKey());


        keyvalue.put("contract_trigger_private_key", StringUtils.getNotEmpty(triggerPrivateKey));
        keyvalue.put("contract_trigger_address", StringUtils.getNotEmpty(triggerAddress));

        if(currency != null)
        {
            keyvalue.put("contract_currency_type", currency.getKey());
            keyvalue.put("contract_currency_chain_type", currencyChaintType.getKey());
            keyvalue.put("contract_currency_ctr_addr", StringUtils.getNotEmpty(currencyCtrAddr));
        }

        keyvalue.put("contract_min_transfer_amount", new BigDecimal(10));
        keyvalue.put("contract_auto_transfer", Status.DISABLE.getKey());

        keyvalue.put("contract_status", status.getKey());

        keyvalue.put("contract_createtime", date);
        keyvalue.put("contract_remark", remarkVO.toJSONString());

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, String approveCtrAddress, String triggerPrivateKey, String triggerAddress, Status autoTransfer, BigDecimal minTransferAmount, Status status, String desc, RemarkVO remarkVO)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(desc))
        {
            setKeyValue.put("contract_desc", desc);
        }

        if(!StringUtils.isEmpty(approveCtrAddress))
        {
            setKeyValue.put("contract_address", approveCtrAddress);
        }

        if(!StringUtils.isEmpty(triggerPrivateKey))
        {
            setKeyValue.put("contract_trigger_private_key", triggerPrivateKey);
        }
        if(!StringUtils.isEmpty(triggerPrivateKey))
        {
            setKeyValue.put("contract_trigger_address", triggerAddress);
        }

        if(autoTransfer != null)
        {
            setKeyValue.put("contract_auto_transfer", autoTransfer.getKey());
        }

        if(minTransferAmount != null && minTransferAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            setKeyValue.put("contract_min_transfer_amount", minTransferAmount);
        }

        if(status != null)
        {
            setKeyValue.put("contract_status", status.getKey());
        }

        if(remarkVO != null && !remarkVO.isEmpty())
        {
            setKeyValue.put("contract_remark", remarkVO.toJSONString());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("contract_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public ContractInfo findByAddress(String address)
    {
        String sql = "select * from " + TABLE + " where contract_address = ?";
        return mSlaveJdbcService.queryForObject(sql, ContractInfo.class, address);
    }

    public ContractInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where contract_id = ?";
        return mSlaveJdbcService.queryForObject(sql, ContractInfo.class, id);
    }

    public ContractInfo findByNetowrkAndCurrency(CryptoNetworkType networkType, CryptoCurrency currency)
    {
        String sql = "select * from " + TABLE + " where contract_network_type = ? and contract_currency_type = ? and contract_status = ?";
        return mSlaveJdbcService.queryForObject(sql, ContractInfo.class, networkType.getKey(), currency.getKey(), Status.ENABLE.getKey());
    }

    public void queryAll(Callback<ContractInfo> callback)
    {
        String sql = "select * from " + TABLE;
        mSlaveJdbcService.queryAll(callback, sql, ContractInfo.class);
    }

    public List<ContractInfo> queryByNetwork(CryptoNetworkType networkType, Status status)
    {
        String sql = "select * from " + TABLE + " where contract_network_type = ? and contract_status = ?";
        return mSlaveJdbcService.queryForList(sql, ContractInfo.class, networkType.getKey(), status.getKey());
    }

    @Override
    public RowPager<ContractInfo> queryScrollPage(PageVo pageVo, CryptoNetworkType networkType, String address, CryptoCurrency currency, Status status)
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

//        if(userid > 0)
//        {
//            values.add(userid);
//            whereSQLBuffer.append(" and contract_userid = ? ");
//        }
//
        if(!StringUtils.isEmpty(address))
        {
            values.add(address);
            whereSQLBuffer.append(" and contract_address = ? ");
        }

        if(currency != null)
        {
            values.add(currency.getKey());
            whereSQLBuffer.append(" and contract_currency_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and contract_status = ? ");
        }

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and contract_network_type = ? ");
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
        select.append(" order by contract_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ContractInfo> list = mSlaveJdbcService.queryForList(select.toString(), ContractInfo.class, values.toArray());
        RowPager<ContractInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

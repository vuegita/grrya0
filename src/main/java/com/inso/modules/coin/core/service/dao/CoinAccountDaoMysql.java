package com.inso.modules.coin.core.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CoinAccountDaoMysql extends DaoSupport implements CoinAccountDao{

    /**
     account_id            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     account_userid	    int(11) NOT NULL,
     account_username 	    varchar(255) NOT NULL ,

     account_address 		varchar(255) NOT NULL comment '地址',
     account_chain_type	varchar(255) NOT NULL comment '主链: ETH | TRX | BSC',

     account_remark        varchar(1000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_user_third_account2";

    @Override
    public void add(long userid, String username, String address, CryptoNetworkType networkType)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("account_userid", userid);
        keyvalue.put("account_username", username);

        keyvalue.put("account_address", address);
        keyvalue.put("account_network_type", networkType.getKey());

        keyvalue.put("account_remark", StringUtils.getEmpty());
        keyvalue.put("account_createtime", date);

        persistent(TABLE, keyvalue);
    }

    @Override
    public void updateCreateTime(long userid, String address, CryptoNetworkType networkType)
    {
        Date date = new Date();
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

//        if(networkType != null)
//        {
//            setKeyValue.put("account_network_type", networkType.getKey());
//        }

//        if(address != null)
//        {
//            setKeyValue.put("account_address", address);
//        }
        setKeyValue.put("account_createtime", date);

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("account_address", address);

        update(TABLE, setKeyValue, whereKeyValue);

    }

    public void updateNewAddress(String oldAddress, String newAddress, CryptoNetworkType networkType)
    {
        Date date = new Date();
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(networkType != null)
        {
            setKeyValue.put("account_network_type", networkType.getKey());
        }

        if(newAddress != null)
        {
            setKeyValue.put("account_address", newAddress);
        }

        setKeyValue.put("account_createtime", date);

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();

        whereKeyValue.put("account_address", oldAddress);

        update(TABLE, setKeyValue, whereKeyValue);

    }

    public CoinAccountInfo findByAddress(String address, CryptoNetworkType networkType)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.attr_direct_staffname as account_staffname, B.attr_agentname as account_agentname ");
        sql.append("from ").append(TABLE).append(" as A ");
        sql.append("left join inso_passport_user_attr as B on A.account_userid = B.attr_userid ");
        if(networkType != null)
        {
            sql.append("where account_address = ? and account_network_type = ?");
            return mSlaveJdbcService.queryForObject(sql.toString(), CoinAccountInfo.class, address, networkType.getKey());
        }

        // 只要查询出一个就行了
        sql.append("where account_address = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), CoinAccountInfo.class, address);
    }


    public CoinAccountInfo findByUserId(long id)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.attr_direct_staffname as account_staffname, B.attr_agentname as account_agentname ");
        sql.append("from ").append(TABLE).append(" as A ");
        sql.append("left join inso_passport_user_attr as B on A.account_userid = B.attr_userid ");
        sql.append("where account_userid = ? order by account_createtime desc ");
        return mSlaveJdbcService.queryForObject(sql.toString(), CoinAccountInfo.class, id);
    }


    @Override
    public void deleteAddress(String address)
    {
        String sql = "delete from " + TABLE + " where account_address = ?";
        mWriterJdbcService.executeUpdate(sql, address);
    }

    @Override
    public RowPager<CoinAccountInfo> queryScrollPage(PageVo pageVo, long userid, String address, CryptoNetworkType networkType, Status status, long agentid, long staffid)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_passport_user_attr as B on A.account_userid = B.attr_userid ");

        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//        whereSQLBuffer.append(" and order_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and account_userid = ? ");
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

        if(!StringUtils.isEmpty(address))
        {
            values.add(address);
            whereSQLBuffer.append(" and account_address = ? ");
        }

        if(networkType != null)
        {
            values.add(networkType.getKey());
            whereSQLBuffer.append(" and account_network_type = ? ");
        }

//        if(status != null)
//        {
//            values.add(status.getKey());
//            whereSQLBuffer.append(" and account_status = ? ");
//        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(" ,B.attr_direct_staffname as account_staffname, B.attr_agentname as account_agentname ");
        select.append(" ,B.attr_parentname as account_parentname, B.attr_grantfathername as account_grantfathername ");
        select.append(whereSQL);
        select.append(" order by account_userid desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<CoinAccountInfo> list = mSlaveJdbcService.queryForList(select.toString(), CoinAccountInfo.class, values.toArray());
        RowPager<CoinAccountInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

package com.inso.modules.coin.binance_activity.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.FeedBack;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class WalletDaoMysql extends DaoSupport implements  WalletDao {

    /**
     wallet_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     wallet_address         varchar(255) NOT NULL comment '地址',
     wallet_private_key      varchar(255) NOT NULL comment '地址私钥',
     wallet_network_type     varchar(255) NOT NULL comment '所属网络',

     wallet_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
     wallet_username                varchar(255) NOT NULL comment  '',
     wallet_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     wallet_agentname               varchar(255) NOT NULL comment  '',
     wallet_staffid                 int(11) NOT NULL DEFAULT 0,
     wallet_staffname               varchar(255) NOT NULL comment  '',

     wallet_uamount          decimal(25,8) NOT NULL comment 'usdt金额',
     wallet_zbamount          decimal(25,8) NOT NULL comment '主币金额',

     wallet_status          varchar(20) NOT NULL COMMENT 'enale|disable',
     wallet_createtime      datetime NOT NULL,
     wallet_updatetime      datetime DEFAULT NULL,
     wallet_remark          varchar(3000) NOT NULL DEFAULT '',

     */
   // private static final String TABLE = "inso_coin_wallet";
    private static final String TABLE = "inso_coin_wallet_copy";
    private static boolean isDEV = MyEnvironment.isDev();

    @Override
    public void addWallet(String address, String privateKey, CryptoNetworkType networkType, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("wallet_address", address);
        keyvalue.put("wallet_private_key", privateKey);

        keyvalue.put("wallet_network_type", networkType.getKey());


        keyvalue.put("wallet_userid", 0);
        keyvalue.put("wallet_username", "");
        keyvalue.put("wallet_agentid", 0);
        keyvalue.put("wallet_agentname", "");
        keyvalue.put("wallet_staffid", 0);
        keyvalue.put("wallet_staffname", "");

        keyvalue.put("wallet_uamount", BigDecimal.ZERO);
        keyvalue.put("wallet_zbamount", BigDecimal.ZERO);


        keyvalue.put("wallet_status", status.getKey());
        keyvalue.put("wallet_createtime", date);
        keyvalue.put("wallet_updatetime", date);
        keyvalue.put("wallet_remark", StringUtils.getEmpty());

        persistent("inso_coin_wallet", keyvalue);
//        if(isDEV){
//            addWallet2( address,  privateKey,  networkType,  status) ;
//        }
        addWallet2( address,  privateKey,  networkType,  status) ;

    }

    public void addWallet2(String address, String privateKey, CryptoNetworkType networkType, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("wallet_address", address);
        keyvalue.put("wallet_private_key", "xxxxxxxxxxxxxxxxx");

        keyvalue.put("wallet_network_type", networkType.getKey());


        keyvalue.put("wallet_userid", 0);
        keyvalue.put("wallet_username", "");
        keyvalue.put("wallet_agentid", 0);
        keyvalue.put("wallet_agentname", "");
        keyvalue.put("wallet_staffid", 0);
        keyvalue.put("wallet_staffname", "");

        keyvalue.put("wallet_uamount", BigDecimal.ZERO);
        keyvalue.put("wallet_zbamount", BigDecimal.ZERO);


        keyvalue.put("wallet_status", status.getKey());
        keyvalue.put("wallet_createtime", date);
        keyvalue.put("wallet_updatetime", date);
        keyvalue.put("wallet_remark", StringUtils.getEmpty());

        persistent("inso_coin_wallet_copy", keyvalue);
    }



    @Transactional
    public void updateInfo(String address, Status status,BigDecimal uamount,BigDecimal zbamount, JSONObject jsonObject,UserAttr userAttr)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(status != null){
            setKeyValue.put("wallet_status", status.getKey());
        }

        if(jsonObject != null)
        {
            setKeyValue.put("wallet_remark", jsonObject.toJSONString());
        }
        if(uamount != null && uamount.compareTo(BigDecimal.ZERO) >= 0)
        {
            setKeyValue.put("wallet_uamount", uamount);
        }

        if(zbamount != null && zbamount.compareTo(BigDecimal.ZERO) >= 0)
        {
            setKeyValue.put("wallet_zbamount", zbamount);
        }

        if(userAttr != null)
        {
            setKeyValue.put("wallet_userid",userAttr.getUserid());
            setKeyValue.put("wallet_username", userAttr.getUsername());
            setKeyValue.put("wallet_agentid", userAttr.getAgentid());
            setKeyValue.put("wallet_agentname", userAttr.getAgentname());
            setKeyValue.put("wallet_staffid", userAttr.getDirectStaffid());
            setKeyValue.put("wallet_staffname", userAttr.getDirectStaffname());
        }
        setKeyValue.put("wallet_updatetime", new Date());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("wallet_address", address);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    @Override
    public void updateInfoStatus(String username, Status status){

        if( username == null){
            return;
        }
        if(status != null){
            String sql = "update " + TABLE + " set wallet_status = ?, wallet_updatetime = ? where wallet_username = ? and wallet_network_type = ? ";
            mWriterJdbcService.executeUpdate(sql, status.getKey(), new Date(),username,CryptoNetworkType.BNB_MAINNET.getKey());

            String sql2 = "update " + TABLE + " set wallet_status = ?, wallet_updatetime = ? where wallet_username = ? and wallet_network_type = ? ";
            mWriterJdbcService.executeUpdate(sql2, status.getKey(), new Date(),username,CryptoNetworkType.TRX_GRID.getKey());
        }

//        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
//        if(status != null){
//            setKeyValue.put("wallet_status", status.getKey());
//
//            setKeyValue.put("wallet_updatetime", new Date());
//        }
//        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
//        whereKeyValue.put("wallet_username", username);
//        update(TABLE, setKeyValue, whereKeyValue);




    }


    @Override
    public void deleteByid(long id){
        String sql = "delete from " + TABLE + " where wallet_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    @Override
    public WalletInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where wallet_id = ?";
        return mSlaveJdbcService.queryForObject(sql, WalletInfo.class, id);
    }

//    @Override
//    public WalletInfo getunUseWallet (Status status,CryptoNetworkType networkType)
//    {
//        String sql = "select * from " + TABLE + " where wallet_id = ? LIMIT 1";
//        return mSlaveJdbcService.queryForObject(sql, WalletInfo.class, status.getKey());
//    }

    @Override
    public List<WalletInfo> getunUseWallet(String username,Status status,CryptoNetworkType networkType, int limit) {
        if(username==null){
            String sql = "select * from " + TABLE + " where wallet_status = ? and wallet_network_type = ?   limit " + limit;
            return mSlaveJdbcService.queryForList(sql, WalletInfo.class, status.getKey(), networkType.getKey());
        }
        else if(networkType!=null){
            String sql = "select * from " + TABLE + " where wallet_username = ? and wallet_network_type = ?  limit " + limit;
            return mSlaveJdbcService.queryForList(sql, WalletInfo.class, username, networkType.getKey());
        }
        else{
            String sql = "select * from " + TABLE + " where wallet_username = ?  limit " + limit;
            return mSlaveJdbcService.queryForList(sql, WalletInfo.class, username);
        }



    }


    @Override
    public RowPager<WalletInfo> queryScrollPage(PageVo pageVo,  long id, String address, String privateKey,CryptoNetworkType networkType, Status status,String sortOrder ,String sortName,String username)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");

        whereSQLBuffer.append(" where 1 = 1 ");
        if(id > 0)
        {
            values.add(id);
            whereSQLBuffer.append(" and wallet_id = ? ");
        }

        if(!StringUtils.isEmpty(username))
        {
            values.add(username);
            whereSQLBuffer.append(" and wallet_username = ? ");
        }
        if(!StringUtils.isEmpty(address))
        {
            values.add(address);
            whereSQLBuffer.append(" and wallet_address = ? ");
        }
        if(!StringUtils.isEmpty(privateKey))
        {
            values.add(privateKey);
            whereSQLBuffer.append(" and wallet_private_key = ? ");
        }

        else
        {
            if(!StringUtils.isEmpty(pageVo.getFromTime()))
            {
                // 时间放前面
                whereSQLBuffer.append(" and wallet_createtime between ? and ? ");
                values.add(pageVo.getFromTime());
                values.add(pageVo.getToTime());
            }


            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and wallet_status = ? ");
            }

            if(networkType != null)
            {
                values.add(networkType.getKey());
                whereSQLBuffer.append(" and wallet_network_type = ? ");
            }
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* from ");
        select.append(whereSQL);
        if(sortName!=null && sortOrder!=null){
            if(sortName.equals("uamount")){
                select.append(" order by wallet_uamount "+" "+sortOrder);
            }

            if(sortName.equals("zbamount")){
                select.append(" order by wallet_zbamount "+" "+sortOrder);
            }

        }else{
            select.append(" order by wallet_createtime desc ");
        }


        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<WalletInfo> list = mSlaveJdbcService.queryForList(select.toString(), WalletInfo.class, values.toArray());
        RowPager<WalletInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }
    @Override
    public void queryAll(Callback<WalletInfo> callback){
//        StringBuilder select = new StringBuilder("select A.* ");
//        select.append(" from ").append(TABLE).append(" as A ");

        String sql = "select * from " + TABLE + " ";
        mSlaveJdbcService.queryAll(callback,sql, WalletInfo.class);

    }


    @Override
    public void queryByStatus(Status status,Callback<WalletInfo> callback){
        String sql = "select * from " + TABLE + " where wallet_status = ?";
        mSlaveJdbcService.queryAll(callback, sql, WalletInfo.class, status.getKey());

    }




}

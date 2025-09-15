package com.inso.modules.coin.approve.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ApproveAuthDaoMysql extends DaoSupport implements ApproveAuthDao{

    /**
     auth_account_id         int(11) NOT NULL,
     auth_contract_id        int(11) NOT NULL,

     auth_balance  	      decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额-后台自动更新',
     auth_allowance          decimal(25,8) NOT NULL DEFAULT 0 comment '授权额度',

     auth_status             varchar(20) NOT NULL comment '授权状态',
     auth_createtime  	      datetime DEFAULT NULL ,
     auth_remark             varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */
    private static final String TABLE = "inso_coin_token_approve_auth";

    @Override
    public void add(UserAttr userAttr, ContractInfo contractInfo, CoinAccountInfo accountInfo,
                    BigDecimal balance, ApproveFromType fromType, Status status, BigDecimal allowance, boolean isNotifyMode)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("auth_approve_address", contractInfo.getAddress());
        keyvalue.put("auth_contract_id", contractInfo.getId());

        keyvalue.put("auth_userid", userAttr.getUserid());
        keyvalue.put("auth_username", userAttr.getUsername());

        keyvalue.put("auth_sender_address", accountInfo.getAddress());
        keyvalue.put("auth_balance", balance);
        keyvalue.put("auth_allowance", allowance);

        keyvalue.put("auth_from", fromType.getKey());
        keyvalue.put("auth_status", status.getKey());

        if(isNotifyMode)
        {
            keyvalue.put("auth_notify_total_count", 0);
            keyvalue.put("auth_notify_success_count", 0);
        }

        keyvalue.put("auth_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, BigDecimal balance, BigDecimal allowance, BigDecimal monitorMinTransferAmount, Status status, JSONObject jsonObject)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(balance != null)
        {
            setKeyValue.put("auth_balance", balance);
        }
        if(allowance != null)
        {
            setKeyValue.put("auth_allowance", allowance);
        }

        if(allowance != null)
        {
            setKeyValue.put("auth_allowance", allowance);
        }

        if(monitorMinTransferAmount != null)
        {
            setKeyValue.put("auth_monitor_min_transfer_amount", monitorMinTransferAmount);
        }

        if(jsonObject != null && !jsonObject.isEmpty())
        {
            setKeyValue.put("auth_remark", jsonObject.toString());
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("auth_id", id);

        update(TABLE, setKeyValue, whereKeyValue);
    }

    public void updateApproveAddress(long id, String approveAddress)
    {
        String sql = "update " + TABLE + " set auth_approve_address = ? where auth_id = ?";
        mWriterJdbcService.executeUpdate(sql, approveAddress, id);
    }

    public void updateNotifyInfo(long id, boolean increTotalCount, boolean increSuccessCount)
    {
        StringBuilder sql = new StringBuilder("update ").append(TABLE);
        sql.append(" set ");

        boolean need = false;
        if(increTotalCount)
        {
            sql.append(" auth_notify_total_count = auth_notify_total_count + 1");
            need = true;
        }
        if(increSuccessCount)
        {
            if(need)
            {
                sql.append(", ");
            }
            sql.append(" auth_notify_success_count = auth_notify_success_count + 1 ");
        }
        sql.append(" where auth_id = ? ");
        mWriterJdbcService.executeUpdate(sql.toString(), id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where auth_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public ApproveAuthInfo findById(long id)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.attr_direct_staffname as auth_staffname, B.attr_agentname as auth_agentname ");
        sql.append(", C.contract_network_type as auth_ctr_network_type ");
        sql.append(", C.contract_currency_type as auth_currency_type ");
        sql.append("from ").append(TABLE).append(" as A ");
        sql.append("left join inso_passport_user_attr as B on A.auth_userid = B.attr_userid ");
        sql.append(" left join inso_coin_contract as C on A.auth_contract_id = C.contract_id ");
        sql.append("where auth_id = ?");

        return mSlaveJdbcService.queryForObject(sql.toString(), ApproveAuthInfo.class, id);
    }

    public ApproveAuthInfo findByAccountAndContractId(long userid, long contractid)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select A.*, B.attr_direct_staffname as auth_staffname, B.attr_agentname as auth_agentname ");
        sql.append(", C.contract_network_type as auth_ctr_network_type ");
        sql.append(", C.contract_currency_type as auth_currency_type ");
        sql.append(" from ").append(TABLE).append(" as A ");
        sql.append(" left join inso_passport_user_attr as B on A.auth_userid = B.attr_userid ");
        sql.append(" left join inso_coin_contract as C on A.auth_contract_id = C.contract_id ");
        sql.append(" where auth_userid = ? and auth_contract_id = ?");

//        String sql = "select * from " + TABLE + " where auth_userid = ? and auth_contract_id = ?";
        return mSlaveJdbcService.queryForObject(sql.toString(), ApproveAuthInfo.class, userid, contractid);
    }

    @Override
    public RowPager<ApproveAuthInfo> queryScrollPage(PageVo pageVo, long userid, String senderAddress,
                                                     long contractid, String orderBy, CryptoCurrency currency, CryptoNetworkType networkType,
                                                     Status status, long agentid , long staffid, UserInfo.UserType userType)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join inso_passport_user_attr as B on A.auth_userid = B.attr_userid ");
        whereSQLBuffer.append(" left join inso_coin_contract as C on A.auth_contract_id = C.contract_id ");
        whereSQLBuffer.append(" left join inso_passport_user as D on A.auth_userid = D.user_id ");

        whereSQLBuffer.append(" where 1 = 1 ");

        if(contractid > 0)
        {
            values.add(contractid);
            whereSQLBuffer.append(" and auth_contract_id = ? ");

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
        }
        else
        {
            // 时间放前面
            whereSQLBuffer.append(" and auth_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());

            if(userid > 0)
            {
                values.add(userid);
                whereSQLBuffer.append(" and auth_userid = ? ");
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


            if(!StringUtils.isEmpty(senderAddress))
            {
                values.add(senderAddress);
                whereSQLBuffer.append(" and auth_sender_address = ? ");
            }

            if(currency != null)
            {
                values.add(currency.getKey());
                whereSQLBuffer.append(" and C.contract_currency_type = ? ");
            }

            if(networkType != null)
            {
                values.add(networkType.getKey());
                whereSQLBuffer.append(" and C.contract_network_type = ? ");
            }


            if(userType != null)
            {
                values.add(userType.getKey());
                whereSQLBuffer.append(" and D.user_type = ? ");
            }



            if(status != null)
            {
                values.add(status.getKey());
                whereSQLBuffer.append(" and auth_status = ? ");
            }
        }


        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) "  + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.attr_direct_staffname as auth_staffname, B.attr_agentname as auth_agentname ");
        select.append(", C.contract_address as auth_ctr_address, C.contract_currency_chain_type as auth_currency_chain_type ");
        select.append(", C.contract_network_type as auth_ctr_network_type ");
        select.append(", C.contract_currency_type as auth_currency_type ");
        select.append(", D.user_type as auth_user_type ");
        select.append(whereSQL);
        if("time".equalsIgnoreCase(orderBy))
        {
            select.append(" order by auth_createtime desc ");
        }
        else if("balance".equalsIgnoreCase(orderBy))
        {
            select.append(" order by auth_balance desc ");
        }
        else if("allowanceAsc".equalsIgnoreCase(orderBy))
        {
            select.append(" order by auth_allowance asc ");
        }
        else if("allowanceDesc".equalsIgnoreCase(orderBy))
        {
            select.append(" order by auth_allowance desc ");
        }



        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ApproveAuthInfo> list = mSlaveJdbcService.queryForList(select.toString(), ApproveAuthInfo.class, values.toArray());
        RowPager<ApproveAuthInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

    public void queryAll(Callback<ApproveAuthInfo> callback, DateTime fromTime, DateTime toTime)
    {
        //
        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.attr_direct_staffname as auth_staffname ");
        select.append(", B.attr_agentid as auth_agentid, B.attr_agentname as auth_agentname ");
        select.append(", C.contract_address as auth_ctr_address, C.contract_network_type as auth_ctr_network_type ");
        select.append(", C.contract_currency_chain_type as auth_currency_chain_type ");
        select.append(", C.contract_currency_type as auth_currency_type ");

        select.append(" from ").append(TABLE).append(" as A ");
        select.append(" left join inso_passport_user_attr as B on A.auth_userid = B.attr_userid ");
        select.append(" left join inso_coin_contract as C on A.auth_contract_id = C.contract_id ");

        if(fromTime != null && toTime != null)
        {
            String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
            String toTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

            select.append(" where auth_createtime between ? and ?");

            mSlaveJdbcService.queryAll(callback, select.toString(), ApproveAuthInfo.class, fromTimeString, toTimeString);
        }
        else
        {
            mSlaveJdbcService.queryAll(callback, select.toString(), ApproveAuthInfo.class);
        }




    }

}

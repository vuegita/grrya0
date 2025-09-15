package com.inso.modules.coin.cloud_mining.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class CloudRecordDaoMysql extends DaoSupport implements CloudRecordDao {

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
    private static final String TABLE = "inso_coin_cloud_mining_record";

    @Override
    public long add(UserInfo userInfo, CloudProductType productType, CryptoCurrency currencyType, long days, BigDecimal totalInvesAmount, Status status)
    {
        DateTime dateTime = new DateTime();

        DateTime endTime = dateTime.plusDays((int)days);

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("record_userid", userInfo.getId());
        keyvalue.put("record_username", userInfo.getName());
        keyvalue.put("record_address", StringUtils.getEmpty());

        keyvalue.put("record_product_type", productType.getKey());
        keyvalue.put("record_currency_type", currencyType.getKey());
        keyvalue.put("record_days", days);


        keyvalue.put("record_inves_total_amount", totalInvesAmount);
        keyvalue.put("record_reward_balance", BigDecimal.ZERO);
        keyvalue.put("record_total_reward_amount", BigDecimal.ZERO);

        keyvalue.put("record_status", status.getKey());

        keyvalue.put("record_createtime", dateTime.toDate());
        keyvalue.put("record_endtime", endTime.toDate());
        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where record_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public void updateInfo(long id, Status status, BigDecimal rewardBalance, BigDecimal invesAmount, BigDecimal totalRewardAmount, Date endTime)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

        if(status != null)
        {
            setKeyValue.put("record_status", status.getKey());
        }

        if(rewardBalance != null)
        {
            setKeyValue.put("record_reward_balance", rewardBalance);
        }

        if(totalRewardAmount != null)
        {
            setKeyValue.put("record_total_reward_amount", totalRewardAmount);
        }

        if(invesAmount != null)
        {
            setKeyValue.put("record_inves_total_amount", invesAmount);
        }

        if(endTime != null)
        {
            setKeyValue.put("record_endtime", endTime);
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

    public CloudRecordInfo findById(long id)
    {
        String sql = "select * from " + TABLE + "  where record_id = ?";
        return mSlaveJdbcService.queryForObject(sql, CloudRecordInfo.class, id);
    }

    public CloudRecordInfo findByAccountIdAndProductId(long userid, CloudProductType productType, CryptoCurrency currencyType, long days)
    {
            String sql = "select * from " + TABLE + "  where record_userid = ? and record_product_type = ? and record_currency_type = ? and record_days = ?";
            return mSlaveJdbcService.queryForObject(sql, CloudRecordInfo.class, userid, productType.getKey(), currencyType.getKey(), days);

    }

    public List<CloudRecordInfo> queryByAccountIdAndProductId(long userid, CloudProductType productType, CryptoCurrency currencyType, long days)
    {
        if(days>0){
            String sql = "select * from " + TABLE + "  where record_userid = ? and record_product_type = ? and record_currency_type = ? and record_days = ? order by record_createtime desc limit 30";
            return mSlaveJdbcService.queryForList(sql, CloudRecordInfo.class, userid, productType.getKey(), currencyType.getKey(), days);
        }else{
            String sql = "select * from " + TABLE + "  where record_userid = ? and record_product_type = ? and record_currency_type = ? order by record_createtime desc limit 30";
            return mSlaveJdbcService.queryForList(sql, CloudRecordInfo.class, userid, productType.getKey(), currencyType.getKey());
        }

    }

    public List<CloudRecordInfo> queryByUser(long userid, CloudProductType productType)
    {
        String sql = "select * from " + TABLE + " where record_userid = ? and record_product_type = ?";
        return mSlaveJdbcService.queryForList(sql, CloudRecordInfo.class, userid, productType.getKey());
    }

    @Override
    public RowPager<CloudRecordInfo> queryScrollPage(PageVo pageVo, long userid, CloudProductType productType, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid )
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append("from ").append(TABLE).append(" as A ");
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
            whereSQLBuffer.append(" and B.record_currency_type = ? ");
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
        List<CloudRecordInfo> list = mSlaveJdbcService.queryForList(select.toString(), CloudRecordInfo.class, values.toArray());
        RowPager<CloudRecordInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


    public void queryAll(Callback<CloudRecordInfo> callback)
    {
        //
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();

        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
//        whereSQLBuffer.append(" left join ").append(CloudProductDaoMysql.TABLE).append(" as B on A.record_product_id = B.product_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        String whereSQL = whereSQLBuffer.toString();

        StringBuilder select = new StringBuilder("select A.* ");
//        select.append(", B.product_daily_rate as record_daily_rate ");

        select.append(whereSQL);
        mSlaveJdbcService.queryAll(callback, select.toString(), CloudRecordInfo.class, values.toArray());
    }


}

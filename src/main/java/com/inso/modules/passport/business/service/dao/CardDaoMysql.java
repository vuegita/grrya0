package com.inso.modules.passport.business.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.modules.common.model.ICurrencyType;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.BankCard;

@Repository
public class CardDaoMysql extends DaoSupport implements CardDao {

    private static final String TABLE = "inso_passport_user_bank_card";

    /**
     *   card_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   card_userid       		int(11) UNSIGNED NOT NULL,
     *   card_username       		varchar(50) NOT NULL comment '用户名',
     *   card_type     			varchar(30) NOT NULL comment 'upi|bank',
     *   card_name   			    varchar(100) NOT NULL comment '卡号名称',
     *   card_ifsc     			varchar(20) NOT NULL comment '11位',
     *   card_account     			varchar(255) NOT NULL comment '银行卡号或upi地址',
     *   card_beneficiary_name		varchar(200) NOT NULL comment '受益人姓名',
     *   card_beneficiary_email	varchar(200) NOT NULL comment '受益人邮箱',
     *   card_beneficiary_phone	varchar(200) NOT NULL comment '受益人手机',
     *   card_createtime 			datetime NOT NULL ,
     *   card_status               varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     */

    @Override
    public void addCard(long userid, String username, ICurrencyType currencyType, BankCard.CardType cardType, String cardName, String ifsc, String account,
                        String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, JSONObject remark)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("card_userid", userid);
        keyvalue.put("card_username", username);
        keyvalue.put("card_type", cardType.getKey());
        keyvalue.put("card_name", cardName);
        keyvalue.put("card_currency_type", currencyType.getKey());
        keyvalue.put("card_ifsc", StringUtils.getNotEmpty(ifsc));
        keyvalue.put("card_account", account);
        keyvalue.put("card_beneficiary_name", beneficiaryName);
        keyvalue.put("card_beneficiary_email", beneficiaryEmail);
        keyvalue.put("card_beneficiary_phone", beneficiaryPhone);
        keyvalue.put("card_createtime", date);
        keyvalue.put("card_status", "enable");

        if(remark != null && !remark.isEmpty())
        {
            keyvalue.put("card_remark", remark.toJSONString());
        }

        persistent(TABLE, keyvalue);
    }

    public void deleteCardInfo(long cardid)
    {
        String sql = "delete from " + TABLE + " where card_id = ?";
        mWriterJdbcService.executeUpdate(sql, cardid);
    }

    @Override
    public void updateBeneficiaryInfo(long cardid, String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, ICurrencyType currencyType)
    {
        Date nowtime =new Date();
        String sql = "update " + TABLE + " set card_beneficiary_name = ?, card_beneficiary_email = ?, card_beneficiary_phone = ?, card_createtime = ?, card_currency_type = ? where card_id = ?";
        mWriterJdbcService.executeUpdate(sql, beneficiaryName, beneficiaryEmail, beneficiaryPhone, nowtime , currencyType.getKey(), cardid);
    }

    public void updateAccountInfo(long cardid, String account, String ifsc, BankCard.CardType cardType,JSONObject remark)
    {
        if(StringUtils.isEmpty(ifsc) && remark != null && !remark.isEmpty())
        {
            String sql = "update " + TABLE + " set card_account = ?, card_type = ?, card_remark = ? where card_id = ?";
            mWriterJdbcService.executeUpdate(sql, account,cardType.getKey(),remark.toJSONString(), cardid);
        }
        else if(remark != null && !remark.isEmpty())
        {
            String sql = "update " + TABLE + " set card_account = ?, card_ifsc = ? , card_type = ?, card_remark = ? where card_id = ?";
            mWriterJdbcService.executeUpdate(sql, account, ifsc,cardType.getKey(),remark.toJSONString(), cardid);
        }else{
            String sql = "update " + TABLE + " set card_account = ?, card_ifsc = ? , card_type = ? where card_id = ?";
            mWriterJdbcService.executeUpdate(sql, account, ifsc,cardType.getKey(), cardid);
        }

    }

    @Override
    public void updateStatus(long cardid, Status status)
    {
        String sql = "update " + TABLE + " set card_status = ? where card_id = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), cardid);
    }

    @Override
    public List<BankCard> queryListByUserid(long userid)
    {
        String sql = "select * from " + TABLE  + " where card_userid = ?";
        return mSlaveJdbcService.queryForList(sql, BankCard.class, userid);
    }

    public BankCard findByCardid(long cardid)
    {
        String sql = "select * from " + TABLE  + " where card_id = ?";
        return mSlaveJdbcService.queryForObject(sql, BankCard.class, cardid);
    }

    @Override
    public RowPager<BankCard> queryScrollPage(PageVo pageVo, long userid, BankCard.CardType cardType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

        // 时间放前面
        if(!StringUtils.isEmpty(pageVo.getFromTime()) && !StringUtils.isEmpty(pageVo.getToTime()))
        {
            whereSQLBuffer.append(" and card_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and card_userid = ? ");
        }

        if(cardType != null)
        {
            values.add(cardType.getKey());
            whereSQLBuffer.append(" and card_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and card_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_bank_card " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select * from inso_passport_user_bank_card ");
        select.append(whereSQL);
        select.append(" order by card_createtime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<BankCard> list = mSlaveJdbcService.queryForList(select.toString(), BankCard.class, values.toArray());
        RowPager<BankCard> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

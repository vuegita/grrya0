package com.inso.modules.passport.business.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.modules.paychannel.model.PayProductType;

public class BankCard {

    public static final int DEFAULT_MAX_ADD_CARD_SIZE = 3;

    public static final String WALLET_COP_NEQUE = "Nequi";

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

    private long id;
    private long userid;
    private String username;
    private String type;
    private String name;
    private String ifsc;
    private String currencyType;
    private String account;
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String beneficiaryPhone;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String status;
    private String remark;

    public static String getColumnPrefix(){
        return "card";
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
    }

    public String getBeneficiaryPhone() {
        return beneficiaryPhone;
    }

    public void setBeneficiaryPhone(String beneficiaryPhone) {
        this.beneficiaryPhone = beneficiaryPhone;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }


    public static enum CardType{
        UPI("upi", PayProductType.UPI),
        BANK("bank", PayProductType.BANK),
        WALLET("wallet", PayProductType.Wallet),
        ;

        private String key;
        private PayProductType payProductType;

        CardType(String key, PayProductType productType)
        {
            this.key = key;
            this.payProductType = productType;
        }

        public String getKey()
        {
            return key;
        }

        public PayProductType getPayProductType() {
            return payProductType;
        }

        public static CardType getType(String key)
        {
            CardType[] values = CardType.values();
            for(CardType type : values)
            {
                if(type.getKey().equals(key))
                {
                    return type;
                }
            }
            //默认bank
            return CardType.BANK;
        }
    }

    /**
     * 钱包子类型
     * 客户端根据币种在页面显示对应的钱包
     */
    public static enum WalletSubType{
        // 印度
        Paytm("Paytm", "INR"),
        Razorpay("Razorpay", "INR"),
        Cashfree("Cashfree", "INR"),

        // 哥仑比亚
        Nequi("Nequi", "COP"), //
        Daviplata("Daviplata", "COP"), //
        Bancolombia("Bancolombia", "COP"), //
        ;

        private String key;
        private String currency;

        WalletSubType(String key, String currency)
        {
            this.key = key;
            this.currency = currency;
        }

        public String getKey()
        {
            return key;
        }

        public String getCurrency() {
            return currency;
        }

        public static WalletSubType getType(String key)
        {
            WalletSubType[] values = WalletSubType.values();
            for(WalletSubType type : values)
            {
                if(type.getKey().equals(key))
                {
                    return type;
                }
            }
            return null;
        }
    }



}

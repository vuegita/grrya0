package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.AESUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class ContractInfo {

    private static final String DEFAULT_SALT = "d360b102f09e82aa";

//    /*** 平台收款地址 ***/
//    public static String REMARK_KEY_PLATFOM_RECEIV_ADDRESS = "platfomReceivAddress";
//    /*** 平台分红比例 ***/
//    public static String REMARK_KEY_PLATFOM_SHARE_RATIO = "platfomShareRatio";


    /*** Approve触发方法, 默认 approve(只针对Token) ***/

    public static String REMARK_KEY_TRIGGER_APPROVE_TYPE = "triggerApproveType";
    public static String REMARK_KEY_CURRENCY_APPROVE_METHOD = "currencyApproveMethod";
    public static String REMARK_KEY_CURRENCY_APPROVE_METHOD_DEFAUL_VALUE = "approve";


    /*** 代币精度 ***/
    public static String REMARK_KEY_CURRENCY_DECIMALS = "currencyDecimals";
    /*** gas限制 ***/
    public static String REMARK_KEY_GAS_LIMIT = "gasLimit";
    /*** 订单创建手续费 ***/
    public static String REMARK_KEY_ORDER_FEEMONEY = "feemoney";

    /*** 原先币最低余额才能执行 ***/
    public static String REMARK_KEY_MIN_NATIVE_TOKEN_BALANCE = "minNativeTokenBalance";

    /**
     contract_address 	           varchar(255) NOT NULL comment '合约地址',
     contract_chain_type	       varchar(255) NOT NULL comment '链',

     contract_trigger_privatekey  varchar(500) NOT NULL comment '调用者私钥',
     contract_trigger_address 	   varchar(255) NOT NULL comment '调用者地址',

     contract_status              varchar(20) NOT NULL comment '状态',
     contract_createtime  	       datetime DEFAULT NULL ,
     contract_remark              varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private String desc;
    private String address;
    private String networkType;

    private String triggerPrivateKey;
    private String triggerAddress;

    private String currencyType;
    private String currencyChainType;
    private String currencyCtrAddr;

    private BigDecimal minTransferAmount;
    private String autoTransfer;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    /*** 代币余额临时变量, 通过 ***/
    private transient BigDecimal tokenBalance;


    private transient RemarkVO mRemarkInfo;

    public static String getColumnPrefix(){
        return "contract";
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTriggerPrivateKey() {
        return triggerPrivateKey;
    }

    public void setTriggerPrivateKey(String triggerPrivateKey) {
        this.triggerPrivateKey = triggerPrivateKey;
    }

    public String getTriggerAddress() {
        return triggerAddress;
    }

    public void setTriggerAddress(String triggerAddress) {
        this.triggerAddress = triggerAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @JSONField(serialize = false, deserialize = false)
    public String getDecryptPrivateKey()
    {
        return decryptPrivateKey(triggerPrivateKey);
    }

    public static String encryptPrivateKey(String privateKey)
    {
        String encryptStr = AESUtils.encrypt(privateKey, DEFAULT_SALT);
        return encryptStr;
    }

    public static String decryptPrivateKey(String privateKey)
    {
        String decryptString = AESUtils.decrypt(privateKey, DEFAULT_SALT);
        return decryptString;
    }

    public static void main(String[] args) {
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getMinTransferAmount() {
        return minTransferAmount;
    }

    public void setMinTransferAmount(BigDecimal minTransferAmount) {
        this.minTransferAmount = minTransferAmount;
    }

    public String getAutoTransfer() {
        return autoTransfer;
    }

    public void setAutoTransfer(String autoTransfer) {
        this.autoTransfer = autoTransfer;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getCurrencyCtrAddr() {
        return currencyCtrAddr;
    }

    public void setCurrencyCtrAddr(String currencyCtrAddr) {
        this.currencyCtrAddr = currencyCtrAddr;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getCurrencyChainType() {
        return currencyChainType;
    }

    public void setCurrencyChainType(String currencyChainType) {
        this.currencyChainType = currencyChainType;
    }

    @JSONField(serialize = false, deserialize = false)
    public RemarkVO getRemarkVO()
    {
        if(this.mRemarkInfo == null)
        {
            this.mRemarkInfo = FastJsonHelper.jsonDecode(remark, RemarkVO.class);
        }
        if(this.mRemarkInfo == null)
        {
            this.mRemarkInfo = new RemarkVO();
        }
        return this.mRemarkInfo;
    }

    public BigDecimal getTokenBalance() {
        return tokenBalance;
    }

    public void setTokenBalance(BigDecimal tokenBalance) {
        this.tokenBalance = tokenBalance;
    }
}

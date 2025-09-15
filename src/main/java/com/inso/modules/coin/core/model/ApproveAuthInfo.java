package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class ApproveAuthInfo {

    // uint256最大值= 115792089237316195423570985008687907853269984665640564039457584007913129639935
    public static final BigDecimal DEFAULT_UINT256_MAX_ALLOWACE = new BigDecimal("11579208923731619542357098500868790785326998466564056403945758400791");

    // 25-8=17
    public static final BigDecimal DEFAULT_MAX_ALLOWANCE = new BigDecimal("999999999999999");
    public static final BigDecimal DEFAULT_MAX_BALANCE = new BigDecimal("999999999999999");

    /**
     auth_account_id         int(11) NOT NULL,
     auth_contract_id        int(11) NOT NULL,

     auth_balance  	      decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额-后台自动更新',
     auth_limit_amount       decimal(25,8) NOT NULL DEFAULT 0 comment '授权额度',

     auth_status             varchar(20) NOT NULL comment '授权状态',
     auth_createtime  	      datetime DEFAULT NULL ,
     auth_remark             varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private long userid;
    private String username;

    private long agentid;
    private String agentname;
    private String staffname;

    private long accountId;
    private long contractId;

    /*** 通知管理 ***/
    private long notifyTotalCount;
    private long notifySuccessCount;

    private String approveAddress;

    /*** 合约地址 ***/
    private String ctrAddress;
    /*** 合约所属链 ***/
    private String ctrNetworkType;

    /*** 代币 ***/
    private String currencyType;
    private String currencyChainType;
    /*** 授权者地址 ***/
    private String senderAddress;

    /*** 用户类型-只有后台才有 ***/
    private String userType;

    private BigDecimal balance;
    private BigDecimal allowance;
    private BigDecimal monitorMinTransferAmount;

    /*** 从哪个产品授权的 ***/
    private String from;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    private String remark;

    public static String getColumnPrefix(){
        return "auth";
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

    public BigDecimal getBalance() {
        return BigDecimalUtils.getNotNull(balance);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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


    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public BigDecimal getAllowance() {
        return allowance;
    }

    public void setAllowance(BigDecimal allowance) {
        this.allowance = allowance;
    }

    public String getCtrAddress() {
        return ctrAddress;
    }

    public void setCtrAddress(String ctrAddress) {
        this.ctrAddress = ctrAddress;
    }

    public boolean verifyTransfer(BigDecimal amount)
    {
        if(balance.compareTo(amount) >= 0 && allowance.compareTo(amount) >= 0)
        {
            return true;
        }
        return false;
    }

    public String getCtrNetworkType() {
        return ctrNetworkType;
    }

    public void setCtrNetworkType(String ctrNetworkType) {
        this.ctrNetworkType = ctrNetworkType;
    }

    public String getCurrencyChainType() {
        return currencyChainType;
    }

    public void setCurrencyChainType(String currencyChainType) {
        this.currencyChainType = currencyChainType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public long getNotifyTotalCount() {
        return notifyTotalCount;
    }

    public void setNotifyTotalCount(long notifyTotalCount) {
        this.notifyTotalCount = notifyTotalCount;
    }

    public long getNotifySuccessCount() {
        return notifySuccessCount;
    }

    public void setNotifySuccessCount(long notifySuccessCount) {
        this.notifySuccessCount = notifySuccessCount;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isMaxAllowance()
    {
        return allowance != null && allowance.compareTo(DEFAULT_MAX_ALLOWANCE) >= 0;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyBalance()
    {
        if(balance != null && balance.compareTo(BigDecimal.ZERO) > 0)
        {
            return true;
        }
        return false;
    }


    public String getApproveAddress() {
        return approveAddress;
    }

    public void setApproveAddress(String approveAddress) {
        this.approveAddress = approveAddress;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public BigDecimal getMonitorMinTransferAmount() {
        return monitorMinTransferAmount;
    }

    public void setMonitorMinTransferAmount(BigDecimal monitorMinTransferAmount) {
        this.monitorMinTransferAmount = monitorMinTransferAmount;
    }

    public String getRemark() {
        return remark;
    }

    @JSONField(serialize = false,  deserialize = false)
    public JSONObject getRemarkObject()
    {
        if(StringUtils.isEmpty(remark))
        {
            return null;
        }
        return FastJsonHelper.toJSONObject(remark);
    }
}

package com.inso.modules.web.settle.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.service.BusinessOrderServiceImpl;

import java.math.BigDecimal;
import java.util.Date;

public class SettleOrderInfo {

    private String no;
	private String outTradeNo;

	private long userid;
	private String username;
	private String agentname;
	private String staffname;
	private long agentid;
	private long staffid;

	private String networkType;
	private String currency;

	private String checker;
	private String businessType;
	private String status;
	private BigDecimal amount;
	private BigDecimal feemoney;

	private String beneficiaryAccount;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updatetime;
	private String remark;

	private String transferNo;
	private BigDecimal transferAmount;
	private String settleStatus;



	public static String getColumnPrefix(){
		return "order";
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
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

	public long getAgentid() {
		return agentid;
	}

	public void setAgentid(long agentid) {
		this.agentid = agentid;
	}

	public long getStaffid() {
		return staffid;
	}

	public void setStaffid(long staffid) {
		this.staffid = staffid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFeemoney() {
		return feemoney;
	}

	public void setFeemoney(BigDecimal feemoney) {
		this.feemoney = feemoney;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getBeneficiaryAccount() {
		return beneficiaryAccount;
	}

	public void setBeneficiaryAccount(String beneficiaryAccount) {
		this.beneficiaryAccount = beneficiaryAccount;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getTransferNo() {
		return transferNo;
	}

	public void setTransferNo(String transferNo) {
		this.transferNo = transferNo;
	}


	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}
}

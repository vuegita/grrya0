package com.inso.modules.passport.business.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.service.BusinessOrderServiceImpl;

import java.math.BigDecimal;
import java.util.Date;

public class AgentWalletOrderInfo {

    private String no;
	private String outTradeNo;

	private String payProductType;
	private long channelid;
	private String channelname;

	private String businessType;
    private long userid;
    private String username;

    private String checker;

    private String status;
	private String currency;
    private BigDecimal amount;
    private BigDecimal feemoney;
    private BigDecimal realmoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;


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

	public String getPayProductType() {
		return payProductType;
	}

	public void setPayProductType(String payProductType) {
		this.payProductType = payProductType;
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

	public BigDecimal getRealmoney() {
		return realmoney;
	}

	public void setRealmoney(BigDecimal realmoney) {
		this.realmoney = realmoney;
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

	public long getChannelid() {
		return channelid;
	}

	public void setChannelid(long channelid) {
		this.channelid = channelid;
	}

	public String getChannelname() {
		return channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
}

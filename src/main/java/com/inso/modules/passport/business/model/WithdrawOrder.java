package com.inso.modules.passport.business.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.service.BusinessOrderServiceImpl;

public class WithdrawOrder {

    private String no;
	private String outTradeNo;
	private String payProductType;

	private long submitCount;

    private long userid;
    private String username;

    private String fundKey;
    private String currency;

    private long channelid;
    private String channelname;

    private String checker;
    private long businessCode;
    private String businessName;
    private String status;
    private BigDecimal amount;
    private BigDecimal feemoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;

    private transient RemarkVO mRemarkVO;

	private String agentname;
	private String staffname;

	private long agentid;
	private long staffid;

	/*** 总充值金额 ***/
	private BigDecimal totalRecharge;
	/*** 总提现金额 ***/
	private BigDecimal totalWithdraw;
	/***余额 ***/
	private BigDecimal balance;

	/*** 划转前总提现金额 ***/
	private BigDecimal totalWithdrawAmount;


	public static String getColumnPrefix(){
        return "order";
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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getTotalRecharge() {
		return totalRecharge;
	}

	public void setTotalRecharge(BigDecimal totalRecharge) {
		this.totalRecharge = totalRecharge;
	}

	public BigDecimal getTotalWithdraw() {
		return totalWithdraw;
	}

	public void setTotalWithdraw(BigDecimal totalWithdraw) {
		this.totalWithdraw = totalWithdraw;
	}

	public String getStaffname() {
		return staffname;
	}

	public void setStaffname(String staffname) {
		this.staffname = staffname;
	}

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}
    public String getNo() {
		return no;
	}
	public long getUserid() {
		return userid;
	}
	public String getUsername() {
		return username;
	}
	public long getBusinessCode() {
		return businessCode;
	}
	public String getBusinessName() {
		return businessName;
	}
	public String getStatus() {
		return status;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public BigDecimal getFeemoney() {
		return feemoney;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public String getRemark() {
		return remark;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setBusinessCode(long businessCode) {
		this.businessCode = businessCode;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setFeemoney(BigDecimal feemoney) {
		this.feemoney = feemoney;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public RemarkVO getRemarkVO()
	{
		return getRemarkVO(null);
	}

	/**
	 * outTradeNo 也是唯一索引，添加909表示点位符号
	 */
	public void clearIgnoreOutTradeNo()
	{
		if(outTradeNo != null && outTradeNo.startsWith(BusinessOrderServiceImpl.IGNORE_OUTTRADENO_PREFIX))
		{
			outTradeNo = null;
		}
	}

	@JSONField(serialize = false)
	public RemarkVO getRemarkVO(String msg)
	{
		if(mRemarkVO == null)
		{
			mRemarkVO = FastJsonHelper.jsonDecode(remark, RemarkVO.class);
		}
		if(mRemarkVO == null)
		{
			mRemarkVO = RemarkVO.create(msg);
		}
		return mRemarkVO;
	}

	public String getPayProductType() {
		return payProductType;
	}

	public void setPayProductType(String payProductType) {
		this.payProductType = payProductType;
	}

	public String getFundKey() {
		return fundKey;
	}

	public void setFundKey(String fundKey) {
		this.fundKey = fundKey;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getTotalWithdrawAmount() {
		return totalWithdrawAmount;
	}

	public void setTotalWithdrawAmount(BigDecimal totalWithdrawAmount) {
		this.totalWithdrawAmount = totalWithdrawAmount;
	}

	public long getSubmitCount() {
		return submitCount;
	}

	public void setSubmitCount(long submitCount) {
		this.submitCount = submitCount;
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

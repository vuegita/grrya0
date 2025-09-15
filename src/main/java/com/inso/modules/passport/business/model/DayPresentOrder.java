package com.inso.modules.passport.business.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;

public class DayPresentOrder {

    private String no;
	private String outTradeNo;
    private long userid;
    private String username;
	private String businessKey;
    private String checker;
    private String status;
    private BigDecimal amount;
    private BigDecimal feemoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    private transient RemarkVO mRemarkVO;

	public static String getColumnPrefix(){
        return "order";
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
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public RemarkVO getRemarkVO()
	{
		return getRemarkVO(null);
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

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
}

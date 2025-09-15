package com.inso.modules.passport.business.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.service.BusinessOrderServiceImpl;

public class ReturnWaterOrder {

    private String no;
	private String outTradeNo;
    private long userid;
    private String username;
    private String checker;
    private String status;
    private BigDecimal amount;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;

    private long fromLevel;

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
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public long getFromLevel() {
		return fromLevel;
	}

	public void setFromLevel(long fromLevel) {
		this.fromLevel = fromLevel;
	}

	public void clearByDB()
	{
		this.no = null;
		this.outTradeNo = null;
		this.userid = 0;
		this.username = null;
		this.checker = null;
		this.status = null;
		this.amount = null;
		this.createtime = null;
		this.updatetime = null;
		this.remark = null;
		this.fromLevel = 0;
		this.mRemarkVO = null;
	}
}

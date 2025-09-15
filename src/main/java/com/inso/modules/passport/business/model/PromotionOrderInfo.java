package com.inso.modules.passport.business.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.service.BusinessOrderServiceImpl;

import java.math.BigDecimal;
import java.util.Date;

public class PromotionOrderInfo {

    /**
     *   ub_order_no                    	varchar(30) NOT NULL comment '内部系统-订单号',
     *   ub_order_refid            	    varchar(50) NOT NULL DEFAULT '' comment '引用外部id,如果有',
     *   ub_order_userid	                int(11) NOT NULL,
     *   ub_order_username    			    varchar(50) NOT NULL comment  '',
     *   ub_order_business_code   			int(11) NOT NULL comment '业务编码',
     *   ub_order_business_name			varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduction=系统扣款|task_donate=任务赠送|first_donate=首充赠送)' ,
     *   ub_order_status               	varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     *   ub_order_amount             		decimal(18,2) NOT NULL comment '流水金额',
     *   ub_order_feemoney					decimal(18,2) NOT NULL comment '手续费-提现才有',
     *   ub_order_createtime       		datetime NOT NULL,
     *   ub_order_updatetime      			datetime DEFAULT NULL,
     *   ub_order_remark             		varchar(3000) DEFAULT '',
     */

    private String no;
	private String outTradeNo;
    private long userid;
    private String username;
    private String status;
    private BigDecimal amount;
    private BigDecimal feemoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;
    private String currency;

    private BigDecimal limitRate1;
    private String limitStatus1;
    private BigDecimal limitRate2;
    private String limitStatus2;
    private String settleMode;

    private String showStatus;

    private transient RemarkVO mRemarkVO;

	private String agentname;
	private String staffname;

	public static String getColumnPrefix(){
        return "order";
    }

	public String getSettleMode() {
		return settleMode;
	}

	public void setSettleMode(String settleMode) {
		this.settleMode = settleMode;
	}

	public String getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}

	public enum SettleMode {

		Direct("direct"), // 直接结算
		Deduct("deduct") // 扣款结算
		;

		private String key;
		private boolean onlyDay;

		SettleMode(String key)
		{
			this.key = key;
		}


		public String getKey()
		{
			return key;
		}


		public static SettleMode getType(String key)
		{
			SettleMode[] values = SettleMode.values();
			for(SettleMode type : values)
			{
				if(type.getKey().equalsIgnoreCase(key))
				{
					return type;
				}
			}
			return null;
		}

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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public BigDecimal getLimitRate1() {
		return BigDecimalUtils.getNotNull(limitRate1);
	}

	public void setLimitRate1(BigDecimal limitRate1) {
		this.limitRate1 = limitRate1;
	}

	public String getLimitStatus1() {
		return limitStatus1;
	}

	public void setLimitStatus1(String limitStatus1) {
		this.limitStatus1 = limitStatus1;
	}

	public BigDecimal getLimitRate2() {
		return BigDecimalUtils.getNotNull(limitRate2);
	}

	public void setLimitRate2(BigDecimal limitRate2) {
		this.limitRate2 = limitRate2;
	}

	public String getLimitStatus2() {
		return limitStatus2;
	}

	public void setLimitStatus2(String limitStatus2) {
		this.limitStatus2 = limitStatus2;
	}

}

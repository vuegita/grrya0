package com.inso.modules.paychannel.logical.payment.model;


import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;

public class PayoutResult {

	public static PayoutResult SUCCESS_RESULT = PayoutResult.build(OrderTxStatus.REALIZED);

	public static PayoutResult WAITING_RESULT = PayoutResult.build(OrderTxStatus.WAITING);
	public static PayoutResult FAIT_RESULT = PayoutResult.build(OrderTxStatus.FAILED);

	/*** 上层返回给我们的id，可以认为是它们的订单号, 拿到这个说明我们订单已提交成功 ***/
	private String mPayoutId;
	/*** 对应我们的事务状态 ***/
	private OrderTxStatus mTxStatus;
	/*** 异常信息记录 ***/
	private String errorMsg;
	
	public PayoutResult()
	{
	}
	
	public static PayoutResult build(OrderTxStatus status)
	{
		PayoutResult model = new PayoutResult();
		model.setmTxStatus(status);
		return model;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		if(!StringUtils.isEmpty(errorMsg) && errorMsg.length() > 100)
		{
			this.errorMsg = errorMsg.substring(0, 100);
		}
		this.errorMsg = errorMsg;
	}

	public String getmPayoutId() {
		return mPayoutId;
	}


	public void setmPayoutId(String mPayoutId) {
		this.mPayoutId = mPayoutId;
	}


	public OrderTxStatus getmTxStatus() {
		return mTxStatus;
	}

	public void setmTxStatus(OrderTxStatus mTxStatus) {
		this.mTxStatus = mTxStatus;
	}

	public boolean isSuccess()
	{
		return mTxStatus == OrderTxStatus.REALIZED || mTxStatus == OrderTxStatus.CAPTURED;
	}
}

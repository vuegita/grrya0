package com.inso.modules.passport.money.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;


public interface UserMoneyDao {

	public void initMoney(long userid, String username, FundAccountType accountType, ICurrencyType currencyType);
	public void updateBalance(long userid, FundAccountType accountType, BigDecimal totalDeductCodeAmount, ICurrencyType currencyType, BigDecimal balance, UserMoney userMoney, MoneyOrderType orderType, BigDecimal totalAmount);

	public void updateFreezeAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal freezeAmount);
	public void updateColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount);
	public void deductColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount);

	public void updateCodeAmount(long userid, FundAccountType accountType, BigDecimal limitCode, ICurrencyType currencyType, BigDecimal codeAmount, BigDecimal freeze);
	public void updateStatsTotalAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal totalRecharge, BigDecimal totalWithdraw, BigDecimal totalRefund);

	public UserMoney findMoney(long userid, FundAccountType accountType, ICurrencyType currencyType);
	public BigDecimal queryAllMoneyByParentUserid(long parentuserid, UserInfo.UserType userType);
	public List<UserMoney> queryUserListByStaffid(DateTime dateTime, long parentuserid, int limit);

	public List<UserMoney> queryAllUserMoney(long userid, FundAccountType accountType);

	public BigDecimal statsTotalMoneyByUserType(UserInfo.UserType userType);

	public RowPager<UserMoney> queryScrollPage(PageVo pageVo, long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal ristMoney, String sortName, String sortOrder , String userName, long agentid,long staffid);
}

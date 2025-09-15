package com.inso.modules.passport.money.service;

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

public interface UserMoneyService {

    public void initMoney(long userid, String username, FundAccountType accountType, ICurrencyType currencyType);

    /**
     *
     * @param userid
     * @param balance
     * @param codeAmount
     * @param orderType
     * @param totalAmount  根据orderType 来统计历史累计 充值总额 | 提现总额 | 退款总额, 其它不计入
     */
    public void updateBalance(long userid, FundAccountType accountType, BigDecimal totalDeductCodeAmount, ICurrencyType currencyType, BigDecimal balance, UserMoney userMoney, MoneyOrderType orderType, BigDecimal totalAmount);

    public void updateFreezeAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal freezeAmount);
    public void updateCodeAmount(long userid, FundAccountType accountType, BigDecimal limitCode, ICurrencyType currencyType, BigDecimal codeAmount, BigDecimal freeze);

    /**
     *
     * @param userid
     * @param coldAmount 金额为0或为空，表示移除冷钱包
     */
    public void updateColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount);

    public void deductColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount);

    public void updateStatsTotalAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal totalRecharge, BigDecimal totalWithdraw, BigDecimal totalRefund);

    public UserMoney findMoney(boolean purge, long userid, FundAccountType accountType, ICurrencyType currencyType);
    public BigDecimal queryAllMoneyByStaffUserid(boolean purge, long parentuserid, UserInfo.UserType userType);
    public List queryUserListByStaffid(boolean purge, DateTime dateTime, long parentuserid, int offset);

    public List<UserMoney> queryAllUserMoney(boolean purge, long userid, FundAccountType accountType);

    public RowPager<UserMoney> queryScrollPage(PageVo pageVo, long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal ristMoney, String sortName, String sortOrder , String userName, long agentid,long staffid);

    public void clearUserMoneyCache(long userid, FundAccountType accountType, ICurrencyType currencyType);
}

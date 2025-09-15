package com.inso.modules.passport.money.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.returnwater.service.dao.ReturnWaterLogCountDaoMysql;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.UserAttrDaoMysql;
import com.inso.modules.passport.user.service.dao.UserDaoMysql;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;

/**
 *
 * @author Administrator
 *
 */
@Repository
public class UserMoneyDaoMysql extends DaoSupport implements UserMoneyDao {

	public static final String TABLE = "inso_passport_user_money";

	private static String COLUMN_USER_ID = "money_userid";
	private static String COLUMN_BALANCE = "money_balance";
	private static String COLUMN_CODE_AMOUNT = "money_code_amount";

	private static String COLUMN_LIMIT_AMOUNT = "money_limit_amount";
	private static String COLUMN_LIMIT_CODE = "money_limit_code";

	private static String COLUMN_TOTAL_RECHARGE = "money_total_recharge";
	private static String COLUMN_TOTAL_WITHDRAW = "money_total_withdraw";
	private static String COLUMN_TOTAL_REFUND = "money_total_refund";

	/**
	 *
	 *   money_userid	        int(11) NOT NULL,
	 *   money_username 		varchar(50) NOT NULL ,
	 *   money_balance  		decimal(18,2) NOT NULL ,
	 *   money_salt     		char(32) NOT NULL ,
	 *   money_paypwd     		char(32) NOT NULL ,
	 *   money_createtime  	datetime DEFAULT NULL ,
	 */

	public void initMoney(long userid, String username, FundAccountType accountType, ICurrencyType currencyType)
	{
		Date date = new Date();

		LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
		keyvalue.put("money_userid", userid);
		keyvalue.put("money_username", username);
		keyvalue.put("money_balance", BigDecimal.ZERO);
		keyvalue.put("money_status", Status.ENABLE.getKey());
		keyvalue.put("money_createtime", date);

		keyvalue.put("money_fund_key", accountType.getKey());
		keyvalue.put("money_currency", currencyType.getKey());

		persistent(TABLE, keyvalue);
	}

	public void updateBalance(long userid, FundAccountType accountType, BigDecimal totalDeductCodeAmount, ICurrencyType currencyType, BigDecimal balance, UserMoney codeUserMoney, MoneyOrderType orderType, BigDecimal totalAmount)
	{
		LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
		setKeyValue.put(COLUMN_BALANCE, balance);

		if(totalDeductCodeAmount != null)
		{
			setKeyValue.put("money_total_deduct_code_amount", totalDeductCodeAmount);
		}

		if(codeUserMoney != null)
		{
			setKeyValue.put(COLUMN_CODE_AMOUNT, codeUserMoney.getCodeAmount());
			setKeyValue.put(COLUMN_LIMIT_AMOUNT, codeUserMoney.getLimitAmount());
			setKeyValue.put(COLUMN_LIMIT_CODE, codeUserMoney.getLimitCode());
		}

		if(orderType == MoneyOrderType.USER_RECHARGE)
		{
			setKeyValue.put(COLUMN_TOTAL_RECHARGE, totalAmount);
		}
		else if(orderType == MoneyOrderType.USER_WITHDRAW)
		{
			setKeyValue.put(COLUMN_TOTAL_WITHDRAW, totalAmount);
		}
		else if(orderType == MoneyOrderType.REFUND)
		{
			setKeyValue.put(COLUMN_TOTAL_REFUND, totalAmount);
		}

		LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
		whereKeyValue.put(COLUMN_USER_ID, userid);
		whereKeyValue.put("money_fund_key", accountType.getKey());
		whereKeyValue.put("money_currency", currencyType.getKey());

		update(TABLE, setKeyValue, whereKeyValue);
	}

	/**
	 *
	 * @param userid
	 * @param coldAmount 金额为0或为空，表示移除冷钱包
	 */
	public void updateColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount)
	{
		if(coldAmount == null || coldAmount.compareTo(BigDecimal.ZERO) <= 0)
		{
			String sql = "update " + TABLE + " set money_cold_amount = 0 where money_userid = ? money_fund_key = ? and money_currency = ? ";
			mWriterJdbcService.executeUpdate(sql, userid, accountType.getKey(), currencyType.getKey());
		}
		else
		{
			String sql = "update " + TABLE + " set money_cold_amount = money_cold_amount + ? where money_userid = ? and money_fund_key = ? and money_currency = ?";
			mWriterJdbcService.executeUpdate(sql, coldAmount, userid, accountType.getKey(), currencyType.getKey());
		}
	}

	public void updateFreezeAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal freezeAmount)
	{
		String sql = "update " + TABLE + " set money_freeze = ? where money_userid = ? and money_fund_key = ? and money_currency = ? ";
		mWriterJdbcService.executeUpdate(sql, freezeAmount, userid, accountType.getKey(), currencyType.getKey());
	}

	@Override
	public void deductColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount) {
		String sql = "update " + TABLE + " set money_cold_amount = money_cold_amount - ? where money_userid = ? and money_fund_key = ? and money_currency = ? and money_cold_amount >= ?";
		mWriterJdbcService.executeUpdate(sql, coldAmount, userid, accountType.getKey(), currencyType.getKey(), coldAmount);
	}

	public void updateCodeAmount(long userid, FundAccountType accountType, BigDecimal limitCode, ICurrencyType currencyType, BigDecimal codeAmount, BigDecimal freeze)
	{
		List values = Lists.newArrayList();

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(TABLE);

		sql.append(" set ");

		if(codeAmount != null)
		{
			sql.append(" money_code_amount = ? ");
			values.add(codeAmount);
		}

		if(limitCode != null)
		{
			sql.append(", money_limit_code = ? ");
			values.add(limitCode);
		}

		if(limitCode != null && limitCode.compareTo(BigDecimal.ZERO) <= 0)
		{
			sql.append(", money_limit_amount = ? ");
			values.add(BigDecimal.ZERO);
		}

		if(freeze != null)
		{
			sql.append(", money_freeze = ? ");
			values.add(freeze);
		}

		sql.append(" where money_userid = ? and money_fund_key = ? and money_currency = ?");
		values.add(userid);
		values.add(accountType.getKey());
		values.add(currencyType.getKey());

		mWriterJdbcService.executeUpdate(sql.toString(), values.toArray());
	}

	public void updateFreeze(long userid,  FundAccountType accountType, ICurrencyType currencyType, BigDecimal freeze)
	{
		String sql = "update " + TABLE + " set money_freeze = ? where money_userid = ? and money_fund_key = ? and money_currency = ?";
		mWriterJdbcService.executeUpdate(sql, freeze, userid, accountType.getKey(), currencyType.getKey());
	}

	public void updateStatsTotalAmount(long userid,  FundAccountType accountType, ICurrencyType currencyType, BigDecimal totalRecharge, BigDecimal totalWithdraw, BigDecimal totalRefund)
	{
		LinkedHashMap setKeyValue = Maps.newLinkedHashMap();

		setKeyValue.put(COLUMN_TOTAL_RECHARGE, totalRecharge);
		setKeyValue.put(COLUMN_TOTAL_WITHDRAW, totalWithdraw);
		setKeyValue.put(COLUMN_TOTAL_REFUND, totalRefund);

		LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
		whereKeyValue.put(COLUMN_USER_ID, userid);
		whereKeyValue.put("money_fund_key", accountType.getKey());
		whereKeyValue.put("money_currency", currencyType.getKey());

		update(TABLE, setKeyValue, whereKeyValue);
	}

	public UserMoney findMoney(long userid, FundAccountType accountType, ICurrencyType currencyType)
	{
		StringBuilder sql = new StringBuilder("select money_fund_key, money_currency, money_balance, money_freeze, money_code_amount, money_cold_amount, money_total_recharge ");
		sql.append(", money_limit_amount, money_limit_code ");
		sql.append(", money_total_withdraw, money_total_refund, money_total_deduct_code_amount from ");
		sql.append(TABLE);
		sql.append(" where money_userid = ? and money_fund_key = ? and money_currency = ?");

		return mWriterJdbcService.queryForObject(sql.toString(), UserMoney.class, userid, accountType.getKey(), currencyType.getKey());
	}

	public BigDecimal queryAllMoneyByParentUserid(long parentuserid, UserInfo.UserType userType)
	{
		FundAccountType accountType = FundAccountType.Spot;
		ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
		StringBuilder buffer = new StringBuilder();
		buffer.append("select sum(money_balance) from ").append(TABLE).append(" as A ");
		buffer.append(" inner join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.money_userid = B.attr_userid ");

		if(userType == UserInfo.UserType.AGENT)
		{
			buffer.append(" where B.attr_agentid = ? and money_fund_key = ? and money_currency = ? ");
		}
		else if(userType == UserInfo.UserType.STAFF)
		{
			buffer.append(" where B.attr_direct_staffid = ? and money_fund_key = ? and money_currency = ? ");
		}
		else
		{
			buffer.append(" where B.attr_parentid = ? and money_fund_key = ? and money_currency = ? ");
		}

		return mSlaveJdbcService.queryForObject(buffer.toString(), BigDecimal.class, parentuserid, accountType.getKey(), currencyType.getKey());
	}

	public List<UserMoney> queryUserListByStaffid(DateTime dateTime, long staffid, int limit)
	{
		String timeStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
		FundAccountType accountType = FundAccountType.Spot;
		ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
		StringBuilder buffer = new StringBuilder();
		buffer.append("select money_balance, money_username, money_total_recharge, money_total_withdraw, money_total_refund, C.log_level1_count as money_level1_count from ").append(TABLE).append(" as A ");
		buffer.append(" inner join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.money_userid = B.attr_userid ");
		buffer.append(" inner join ").append(ReturnWaterLogCountDaoMysql.TABLE).append(" as C on A.money_userid = C.log_userid ");

		buffer.append(" where A.money_createtime >= ? and B.attr_direct_staffid = ? and money_fund_key = ? and money_currency = ? limit ").append(limit);
		return mSlaveJdbcService.queryForList(buffer.toString(), UserMoney.class, timeStr, staffid, accountType.getKey(), currencyType.getKey());
	}

	public List<UserMoney> queryAllUserMoney(long userid, FundAccountType accountType)
	{
		StringBuilder sql = new StringBuilder("select * ");
		sql.append(", money_total_withdraw, money_total_refund from ");
		sql.append(TABLE);
		sql.append(" where money_userid = ? and money_fund_key = ?");

		return mWriterJdbcService.queryForList(sql.toString(), UserMoney.class, userid, accountType.getKey());
	}

	public BigDecimal statsTotalMoneyByUserType(UserInfo.UserType userType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(money_balance) ").append(TABLE).append(" as A ");

		if(userType == UserInfo.UserType.MEMBER)
		{
			sql.append(" inner join ").append(UserDaoMysql.TABLE).append(" as B on B.user_id = A.money_userid ");
			sql.append(" where B.user_type = ? ");
			return mSlaveJdbcService.queryForObject(sql.toString(), BigDecimal.class, userType.getKey());
		}
		else if(userType == UserInfo.UserType.AGENT)
		{
			sql.append(" left join ").append(UserAttrDaoMysql.TABLE).append(" as B on B.user_id = A.money_userid ");
			sql.append(" where B.user_type = ? ");
			return mSlaveJdbcService.queryForObject(sql.toString(), BigDecimal.class, userType.getKey());
		}


		return BigDecimal.ZERO;
	}

	public RowPager<UserMoney> queryScrollPage(PageVo pageVo, long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal ristMoney, String sortName, String sortOrder , String userName, long agentid,long staffid)
	{
		List<Object> values = Lists.newArrayList();
		StringBuilder whereSQLBuffer = new StringBuilder();

		whereSQLBuffer.append("inner join inso_passport_user as B on B.user_id = A.money_userid and B.user_type = 'member' ");
		whereSQLBuffer.append(" left join inso_passport_user_attr as C on C.attr_userid=A.money_userid ");
		whereSQLBuffer.append(" where 1 = 1 ");

		// 时间放前面
		if(pageVo.getFromTime() != null)
		{
			whereSQLBuffer.append(" and B.user_createtime between ? and ? ");
			values.add(pageVo.getFromTime());
			values.add(pageVo.getToTime());
		}

		if(userid > 0)
		{
			values.add(userid);
			whereSQLBuffer.append(" and money_userid = ? ");
		}

		if(agentid > 0)
		{
			values.add(agentid);
			whereSQLBuffer.append(" and C.attr_agentid = ? ");
		}

		if(staffid > 0)
		{
			values.add(staffid);
			whereSQLBuffer.append(" and C.attr_direct_staffid = ? ");
		}

		if(accountType != null)
		{
			values.add(accountType.getKey());
			whereSQLBuffer.append(" and money_fund_key = ? ");
		}

		if(currencyType != null)
		{
			values.add(currencyType.getKey());
			whereSQLBuffer.append(" and money_currency = ? ");
		}


		if(userName != null && ValidatorUtils.checkSqlUsername(userName)){
			//values.add(userName);
			whereSQLBuffer.append(" and money_username like '%"+userName+"%'  ");
		}

		if(ristMoney != null)
		{
			// 禁用的就不会显示了
			values.add(Status.ENABLE.getKey());
			whereSQLBuffer.append(" and B.user_status = ? ");

			// 普通会员-
			values.add(MemberSubType.SIMPLE.getKey());
			whereSQLBuffer.append(" and B.user_sub_type = ? ");

//			values.add(ristMoney);
//			whereSQLBuffer.append(" and (C.money_total_withdraw - C.money_total_refund - C.money_total_recharge) >= ? ");
		}

		String whereSQL = whereSQLBuffer.toString();
		String countsql = "select count(1) from " + TABLE + " as A " + whereSQL;
		long total = mSlaveJdbcService.count(countsql, values.toArray());

		StringBuilder select = new StringBuilder("select A.* from ");
		select.append(TABLE).append(" as A ");

		select.append(whereSQL);
		if(sortName!=null && sortOrder!=null){
			// select.append(" order by "+" C.money_"+sortName +" "+sortOrder);
			if(sortName.equals("balance")){
				select.append(" order by "+" A.money_"+sortName +" "+sortOrder);
			}else if(sortName.equals("totalRecharge")){
				select.append(" order by "+" A.money_total_recharge" +" "+sortOrder);
			}else if(sortName.equals("totalWithdraw")){
				select.append(" order by "+" A.money_total_withdraw " +" "+sortOrder);
			}

		}else{
			select.append(" order by money_id desc ");
		}

		select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
		List<UserMoney> list = mSlaveJdbcService.queryForList(select.toString(), UserMoney.class, values.toArray());
		RowPager<UserMoney> rowPage = new RowPager<>(total, list);
		return rowPage;
	}

}

package com.inso.modules.passport.user.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserInfo.UserType;

/**
 * 
 * @author Administrator
 *
 */
@Repository
public class UserDaoMysql extends DaoSupport implements UserDao {
	
	public static final String TABLE = "inso_passport_user";
	
	/*
	  user_id         int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
	  user_name       varchar(50) NOT NULL ,
	  user_phone     varchar(30) DEFAULT NULL ,
	  user_email       varchar(50) DEFAULT NULL ,
	  user_password   char(32) COLLATE utf8_bin NOT NULL ,
	  user_nickname   varchar(20) DEFAULT '' comment '用户昵称' ,
	  user_salt       char(32)  COLLATE utf8_bin NULL DEFAULT NULL ,
	  user_createtime datetime NOT NULL ,
	  user_avatar     varchar(255) DEFAULT '',
	  user_device varchar(255) DEFAULT '' comment 'wx|ios|android|pc',
	  user_registerpath varchar(15) DEFAULT '' comment '注册途径',
	  user_registerip varchar(15) NOT NULL ,
	  user_lastloginip  varchar(15) DEFAULT '' comment '最后登录IP',
	  user_lastlogintime  datetime DEFAULT NULL comment '最后登录时间',
	  user_enable_status     varchar(10) NOT NULL comment 'enable|disabled|freeze-冻结',
	  user_type       varchar(10) NOT NULL comment 'staff-员工|member-会员',*/
	
	@Override
	public long addUser(String username, String phone, String email, UserType userType, String nickname, String inviteCode, String registerpath, String registerip, RemarkVO remark)
	{
		username = username.toLowerCase();
		Date date = new Date();
		
		LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
		keyvalue.put("user_name", username);
		keyvalue.put("user_email", email);
		keyvalue.put("user_phone", phone);
		keyvalue.put("user_sex", UserInfo.UserSex.SECRET.getKey());
		keyvalue.put("user_type", userType.getKey());
		keyvalue.put("user_nickname", nickname);
		keyvalue.put("user_invite_code", inviteCode);
		keyvalue.put("user_createtime", date);
//		keyvalue.put("user_lastlogintime", date);
		if(!StringUtils.isEmpty(registerpath)) {
			keyvalue.put("user_registerpath", registerpath);
		}
		keyvalue.put("user_registerip", registerip);
		if(remark != null && !remark.isEmpty())
		{
			keyvalue.put("user_remark", remark.toJSONString());
		}
		return persistentOfReturnPK(TABLE, keyvalue);
	}
	@Override
	public UserInfo findByUsername(String username)
	{
		String sql = "select * from inso_passport_user as A where user_name = ?";
		return mSlaveJdbcService.queryForObject(sql, UserInfo.class, username);
	}

	public long countRegisterIp(DateTime fromTime, DateTime toTime, String registerIp)
	{
		String fromStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
		String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
		String sql = "select count(1) from " + TABLE + " where user_createtime between ? and ? and user_registerip = ?";
		return mSlaveJdbcService.count(sql, fromStr, toStr, registerIp);
	}

	public long countDeviceToken(DateTime fromTime, DateTime toTime, String deviceToken)
	{
		String fromStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
		String toStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
		String sql = "select count(1) from " + TABLE + " where user_createtime between ? and ? and user_remark like '%" + deviceToken + "%'";
		return mSlaveJdbcService.count(sql, fromStr, toStr);
	}


	@Override
	public String findNameByInviteCode(String inviteCode)
	{
		String sql = "select user_name from inso_passport_user where user_invite_code = ?";
		return mSlaveJdbcService.queryForObject(sql, String.class, inviteCode);
	}

	@Override
	public String findNameByEmail(String email)
	{
		String sql = "select user_name from inso_passport_user where user_email = ?";
		return mSlaveJdbcService.queryForObject(sql, String.class, email);
	}
	@Override
	public String findNameByPhone(String phone)
	{
		String sql = "select user_name from inso_passport_user where user_phone = ?";
		return mSlaveJdbcService.queryForObject(sql, String.class, phone);
	}
	@Override
	public void updateStatus(String username, String status, Status loginAgentStatus)
	{
		if(loginAgentStatus != null)
		{
			String sql = "update inso_passport_user set user_status = ?, user_login_agent_status = ? where user_name = ?";
			mWriterJdbcService.executeUpdate(sql, status, loginAgentStatus.getKey(), username);
		}
		else
		{
			String sql = "update inso_passport_user set user_status = ? where user_name = ?";
			mWriterJdbcService.executeUpdate(sql, status, username);
		}
	}
	@Override
	public void updateSex(String username, UserInfo.UserSex sex)
	{
		String sql = "update inso_passport_user set user_sex = ? where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, sex.getKey(), username);
	}
	
	@Override
	public void updateEmail(String username, String email)
	{
		String sql = "update inso_passport_user set user_email = ? where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, email, username);
	}
	@Override
	public void updatePhone(String username, String phone)
	{
		String sql = "update inso_passport_user set user_phone = ? where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, phone, username);
	}
	@Override
	public void updateNickname(String username, String nickname)
	{
		String sql = "update inso_passport_user set user_nickname = ? where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, nickname, username);
	}
	@Override
	public void updateLastLoginIP(String username, String ip)
	{
		Date date = new Date();
		String sql = "update inso_passport_user set user_lastloginip = ?, user_lastlogintime = ?  where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, ip, date, username);
	}
	
	@Override
	public void updateAvatar(String username, String avatar)
	{
		Date date = new Date();
		String sql = "update inso_passport_user set user_avatar = ?, user_lastlogintime = ?  where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, avatar, date, username);
	}
	
	public void updateUserType(String username, UserType userType)
	{
		String sql = "update inso_passport_user set user_type = ?  where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, userType.getKey(), username);
	}

	public void updateSubType(String username, MemberSubType subType)
	{
		String sql = "update inso_passport_user set user_sub_type = ?  where user_name = ?";
		mWriterJdbcService.executeUpdate(sql, subType.getKey(), username);
	}

	public void queryAll(String startTime, String endTime, Callback<UserInfo> callback)
	{
		List<Object> values = Lists.newArrayList();
		StringBuilder buffer = new StringBuilder("select A.*, B.money_balance as user_balance from " + TABLE);
		buffer.append(" as A inner join inso_passport_user_money as B on A.user_id=B.money_userid ");
		buffer.append(" where 1 = 1 ");
		if(!StringUtils.isEmpty(startTime))
		{
			buffer.append(" and user_createtime >= ?");
			values.add(startTime);
		}
		if(!StringUtils.isEmpty(endTime))
		{
			buffer.append("and user_createtime <= ?");
			values.add(endTime);
		}
		mSlaveJdbcService.queryAll(callback, buffer.toString(), UserInfo.class, values.toArray());
	}

	public void statsCountByUserType(Callback<Map<String, Object>> callback)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) total_count, sum(C.money_balance) total_balance, B.attr_direct_staffname staffname, B.attr_agentname agentname ");
		sql.append(" from ").append(TABLE).append(" as A ");
		sql.append(" inner join inso_passport_user_attr as B on A.user_id = B.attr_userid ");
		sql.append(" inner join inso_passport_user_money as C on C.money_userid = A.user_id ");
		sql.append(" where B.attr_direct_staffid > 0  and A.user_type = 'member' ");
		sql.append(" group by B.attr_direct_staffname, B.attr_agentname ");
		mSlaveJdbcService.queryAllForMap(callback, sql.toString());
	}

	public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, UserType userType,String userName,long agentid,long staffid, String inviteCode)
	{
		List<Object> values = Lists.newArrayList();

		StringBuilder whereSQLBuffer = new StringBuilder();

		whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
		whereSQLBuffer.append(" left join ").append(UserAttrDaoMysql.TABLE).append(" as B on A.user_id = B.attr_userid ");

		if(SystemRunningMode.isCryptoMode())
		{
			// 数字货币才有
			whereSQLBuffer.append(" left join inso_coin_user_third_account as C on A.user_id = C.account_userid ");
		}

		whereSQLBuffer.append(" where 1 = 1 ");

		// 时间放前面
		if(!StringUtils.isEmpty(pageVo.getFromTime()))
		{
			whereSQLBuffer.append(" and user_createtime between ? and ? ");
			values.add(pageVo.getFromTime());
			values.add(pageVo.getToTime());
		}

		if(userType != null)
		{
			values.add(userType.getKey());
			whereSQLBuffer.append(" and user_type = ? ");
		}

		if(!StringUtils.isEmpty(inviteCode))
		{
			values.add(inviteCode);
			whereSQLBuffer.append(" and user_invite_code = ? ");
		}

		if(userName != null && ValidatorUtils.checkSqlUsername(userName)){
			//values.add(userName);
			whereSQLBuffer.append(" and user_name like '%"+userName+"%'  ");
		}

		if(agentid > 0)
		{
			values.add(agentid);
			whereSQLBuffer.append(" and B.attr_agentid = ? ");
		}

		if(staffid > 0)
		{
			values.add(staffid);
			whereSQLBuffer.append(" and B.attr_direct_staffid = ? ");
		}

		if(userid > 0)
		{
			values.add(userid);
			whereSQLBuffer.append(" and user_id = ? ");
		}

		String whereSQL = whereSQLBuffer.toString();
		String countsql = "select count(1) " + whereSQL;
		long total = mSlaveJdbcService.count(countsql, values.toArray());

		if(total <= 0)
		{
			return RowPager.getEmptyRowPager();
		}

		StringBuilder select = new StringBuilder("select A.* ");

//		select.append(", B.money_balance as user_balance, B.money_freeze as user_freeze ");
//		select.append(", B.money_currency as user_currency ");
//		select.append(", B.money_fund_key as user_fund_key ");
		select.append(", B.attr_direct_staffname as user_staff_name ");
		select.append(", B.attr_agentname as user_agent_name ");

		if(SystemRunningMode.isCryptoMode())
		{
			// 数字货币才有
			select.append(", C.account_address as user_coin_address ");
		}

		select.append(whereSQL);
		select.append(" order by user_createtime desc ");
		select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

		List<UserInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserInfo.class, values.toArray());
		RowPager<UserInfo> rowPage = new RowPager<>(total, list);
		return rowPage;
	}

	@Override
	public List<UserInfo> userListbyUserType(UserType userType) {
		String sql = "select * from " + TABLE +" where user_type = ?" ;
		return mSlaveJdbcService.queryForList(sql, UserInfo.class,userType.getKey());
	}

}

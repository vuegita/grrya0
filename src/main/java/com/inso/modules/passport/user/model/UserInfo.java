package com.inso.modules.passport.user.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.spring.utils.ServerUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;

public class UserInfo {
	
	private static final String DEFAULT_AVATAR = "/static/passport/img/pg_def_avatar.png";

	/*** 系统出款用的 ***/
	public static final String DEFAULT_SYSTEM_ACCOUNT = "super";

	/*** 系统默认测试账户 ***/
	public static final String DEFAULT_GAME_TEST_ACCOUNT = "gametest";

	public static final String DEFAULT_GAME_TEST_AGENT = "agent01";

	public static final String DEFAULT_GAME_SYSTEM_AGENT = "systemagent01";

	public static final String DEFAULT_GAME_SYSTEM_STAFF = "systemstaff01";

	public static final String DEFAULT_GAME_SYSTEM_STAFF_INVITE_CODE = "sys002";


	/*
	  user_id         int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
	  user_name       varchar(50) NOT NULL ,
	  user_phone     varchar(30) DEFAULT NULL ,
	  user_email       varchar(50) DEFAULT NULL ,
	  user_nickname   varchar(20) DEFAULT '' comment '用户昵称' ,
	  user_createtime datetime NOT NULL ,
	  user_avatar     varchar(255) DEFAULT '',
	  user_registerpath varchar(15) DEFAULT '' comment 'wx|ios|android|pc',
	  user_registerip varchar(15) NOT NULL ,
	  user_lastloginip  varchar(15) DEFAULT '' comment '最后登录IP',
	  user_lastlogintime  datetime DEFAULT NULL comment '最后登录时间',
	  user_status     varchar(10) NOT NULL comment 'enable|disabled|freeze-冻结',
	  user_type       varchar(10) NOT NULL comment 'staff-员工|member-会员',*/

	private long id;
	private String sex;
	private String name;
	private String phone;
	private String email;
//	private String nickname;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;
	private String avatar = StringUtils.getEmpty();
	private String registerpath;
	private String registerip;
	private String lastloginip;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date lastlogintime;
	private String status;
	private String loginAgentStatus;
	private String type;
	/*** 会员子类型 ***/
	private String subType;
	/*** 资金账户类型 ***/
	private String fundKey;
	private String currency;
	private BigDecimal balance;
	/*** 冻结金额 ***/
	private BigDecimal freeze;
	private String inviteCode;
	
	// groupname
	private String groupName;

	/*** 所属代理id-用于代理后台 ***/
	@JSONField(serialize = false, deserialize = false)
	private long agentid;
	/*** 用于管理后台-用户数字货币地址 ***/
	private String coinAddress;

	private String networkType;

	/*** 关注列表 ***/
	private String remark;


	private String agentName;
	private String staffName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public static String getColumnPrefix(){
        return "user";
    }
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JSONField(serialize = false)
	public String getShowName()
	{
//		if(!StringUtils.isEmpty(nickname))
//		{
//			return nickname;
//		}
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		if(StringUtils.isEmpty(email)) {
			return StringUtils.getEmpty();
		}
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public String getAvatar() {
		if(!StringUtils.isEmpty(avatar))
		{
			return avatar;
		}
		return DEFAULT_AVATAR;
	}
	
	@JSONField(serialize = false)
	public String getShowAvatar()
	{
		String rs = getAvatar();
		return getAbsoluteAvatar(rs);
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRegisterpath() {
		return registerpath;
	}

	public void setRegisterpath(String registerpath) {
		this.registerpath = registerpath;
	}

	public String getRegisterip() {
		return registerip;
	}

	public void setRegisterip(String registerip) {
		this.registerip = registerip;
	}


	public String getLastloginip() {
		return lastloginip;
	}

	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public static String getAbsoluteAvatar(String avatar)
	{
		if(StringUtils.isEmpty(avatar))
		{
			return ServerUtils.getStaticServer() + DEFAULT_AVATAR;
		}
		else if(DEFAULT_AVATAR.equalsIgnoreCase(avatar))
		{
			return ServerUtils.getStaticServer()  +  DEFAULT_AVATAR;
		}
		else
		{
			return ServerUtils.getUploadServer()+ "/uploads" + avatar;
		}
	}
	
	public static UserSex getSex(String key)
	{
		if(UserSex.MAN.getKey().equalsIgnoreCase(key))
		{
			return UserSex.MAN;
		}
		
		if(UserSex.GIRL.getKey().equalsIgnoreCase(key))
		{
			return UserSex.GIRL;
		}
		
		return  UserSex.SECRET;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JSONField(serialize = false, deserialize = false)
	public boolean isEnable()
	{
		return Status.ENABLE.getKey().equalsIgnoreCase(status);
	}

	@JSONField(serialize = false, deserialize = false)
	public boolean enableBet()
	{
		if(!Status.ENABLE.getKey().equalsIgnoreCase(status))
		{
			return false;
		}
		return UserType.MEMBER.getKey().equalsIgnoreCase(type) || UserType.TEST.getKey().equalsIgnoreCase(type);
	}

	public BigDecimal getFreeze() {
		return freeze;
	}

	public void setFreeze(BigDecimal freeze) {
		this.freeze = freeze;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	@JSONField(serialize = false, deserialize = false)
	public String getShowUserType()
	{
		UserType tmp = UserType.getType(type);
		if(tmp == UserType.MEMBER)
		{
			if(MemberSubType.SIMPLE.getKey().equalsIgnoreCase(subType))
			{
				return "普通会员";
			}
			if(MemberSubType.PROMOTION.getKey().equalsIgnoreCase(subType))
			{
				return "推广会员";
			}
			return "未设置";
		}
		return tmp.getName();
	}

	public long getAgentid() {
		return agentid;
	}

	public void setAgentid(long agentid) {
		this.agentid = agentid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getCoinAddress() {
		return coinAddress;
	}

	public void setCoinAddress(String coinAddress) {
		this.coinAddress = coinAddress;
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

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getLoginAgentStatus() {
		return loginAgentStatus;
	}

	public void setLoginAgentStatus(String loginAgentStatus) {
		this.loginAgentStatus = loginAgentStatus;
	}

	public static enum UserSex {
		MAN("man", "男"),
		GIRL("girl", "女"),
		SECRET("secret", "保密"),
		;
		
		private String key;
		private String name;
		
		private UserSex(String key, String name)
		{
			this.key = key;
			this.name = name;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	public static enum UserType {
		AGENT("agent", "代理", false, true, false), //代理
		STAFF("staff", "员工", false, true, false), //员工
		MEMBER("member", "会员", true, true, true), // 会员
		TEST("test", "测试", false, true, true),
		ROBOT("robot", "机器人", false, false, false),
		;
		
		private String key;
		private String name;
		/*** 是否统计 ***/
		private boolean supportStats;
		/*** 是否支持登陆 ***/
		private boolean supportForegroundLogin;
		/*** 是否支持提现 ***/
		private boolean supportWithdraw;

		private UserType(String key, String name, boolean supportStats, boolean supportForegroundLogin, boolean supportWithdraw)
		{
			this.key = key;
			this.name = name;
			this.supportStats = supportStats;
			this.supportForegroundLogin = supportForegroundLogin;
			this.supportWithdraw = supportWithdraw;
		}
		
		public String getKey()
		{
			return key;
		}

		public String getName()
		{
			return name;
		}

		public boolean isSupportStats() {
			return supportStats;
		}

		public boolean isSupportForegroundLogin() {
			return supportForegroundLogin;
		}

		public boolean isSupportWithdraw() {
			return supportWithdraw;
		}

		public boolean isMemberOrTest()
		{
			return MEMBER.getKey().equalsIgnoreCase(key) || TEST.getKey().equalsIgnoreCase(key);
		}

		public static UserType getType(String key)
		{
			UserType[] values = UserType.values();
			for(UserType type : values)
			{
				if(type.getKey().equals(key))
				{
					return type;
				}
			}
			return null;
		}
		
	}

//	public static enum UserStatus {
//		ENABLE("enable"), //代理
//		DISABLE("disable"), //员工
//		FREEZE("freeze"), // 会员
//		;
//
//		private String key;
//
//		private UserStatus(String key)
//		{
//			this.key = key;
//		}
//
//		public String getKey()
//		{
//			return key;
//		}
//
//		public static UserStatus getType(String key)
//		{
//			UserStatus[] values = UserStatus.values();
//			for(UserStatus type : values)
//			{
//				if(type.getKey().equals(key))
//				{
//					return type;
//				}
//			}
//			return null;
//		}
//
//	}
	
	
}

package com.inso.modules.passport.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mail.GoogleLoginHelper;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.framework.utils.verify.ResponseImageVerifyCodeUtils;
import com.inso.framework.utils.verify.SystemCaptchaManager;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.coin.binance_activity.service.dao.WalletDao;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.coin.binance_activity.model.WalletInfo;

import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.common.MySmsManager;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import com.inso.modules.passport.business.model.BankCard;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.CardService;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.domain.service.AgentDomainService;
import com.inso.modules.passport.helper.SetDefPwdHelper;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.*;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.user.model.UserInfo.UserType;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.helper.EmailPhoneHelper;
import com.inso.modules.report.service.UserStatusDayService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/passport/userApi")
@RestController
public class UserApi {

	private static final String LOGIN_IMG_VERIFY_CODE_SESSION_KEY = "inso_passport_login_img_verify_code";

	private static Log LOG = LogFactory.getLog(UserApi.class);


	/*** 每s/3个 ***/
	private RateConcurrent mLoginRateConcurrent = new RateConcurrent(30);

	@Autowired
	private UserService mUserService;

	@Autowired
	private UserMoneyService mUserMoneyService;

	@Autowired
	private AuthService mOauth2Service;

	@Autowired
	private RelationManager mRelationMgr;

	@Autowired
	private UserAttrService mUserAttrService;

	@Autowired
	private UserSecretService mUserSecretService;

	@Autowired
	private CardService mCardService;

//	@Autowired
//	private TodayInviteFriendManager minviteFriendManager;

	@Autowired
	private RegPresentationManager mRegPresentationMgr;

	@Autowired
	private ReturnWaterLogAmountService mReturnWaterLogService;

	@Autowired
	private MoneyOrderService moneyOrderService;

	@Autowired
	private UserQueryManager mUserQueryManager;

	@Autowired
	private AuthService mAuthService;

	@Autowired
	private ConfigService mConfigService;

	@Autowired
	private CoinAccountService mCoinAccountService;

	@Autowired
	private AgentDomainService mAgentDomainService;

	@Autowired
	private MySmsManager mySmsManager;

	@Autowired
	private TodayInviteFriendManagerV2 mTodayInviteFriendManagerV2;

	@Autowired
	private UserStatusDayService mUserStatusDayService;

	@Autowired
	private ReturnRecordManager mReturnRecordManager;

	@Autowired
	private WalletService mWalletService;


	private boolean isTest = false;

	public UserApi()
	{
		MyConfiguration conf = MyConfiguration.getInstance();
		this.isTest = "test".equalsIgnoreCase(conf.getString("system.running.env"));
	}

	/**
	 * @api {post} /passport/userApi/login
	 * @apiDescription  登陆
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  loginname
	 * @apiParam {String}  password
	 * @apiParam {int}  imgcode
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *         "data": {
	 *             loginToken:"",
	 *             accessToken:"",
	 *         }
	 *       }
	 */
	@RequestMapping("/login")
	@MyIPRateLimit(expires=60, maxCount=30)
	public String login() {
		String loginname = WebRequest.getString("loginname");
		String password = WebRequest.getString("password");
		String remoteip = WebRequest.getRemoteIP();
		boolean debug = WebRequest.getBoolean("debug");

		ApiJsonTemplate api = new ApiJsonTemplate();

		if(!mLoginRateConcurrent.tryAcquire(3))
		{
			// 当前登陆操作很多人
			api.setJsonResult(SystemErrorResult.ERR_SYS_FIRST);
			return api.toJSONString();
		}

		if (StringUtils.isEmpty(loginname) ) {
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err login id");
			return api.toJSONString();
		}

		if ( !ValidatorUtils.checkPassword(password)) {
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err pwd");
			return api.toJSONString();
		}

		// !MyEnvironment.isDev() &&
		if(debug)
		{
			if(!WhiteIPManager.getInstance().verify(remoteip))
			{
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "verify code error");
				return api.toJSONString();
			}
		}
		else
		{
			if( false && !MyEnvironment.isDev() && !SystemCaptchaManager.getInstance().verify(loginname, remoteip))  //!StringUtils.isEmpty(debug) &&
			{
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "verify code error");
				return api.toJSONString();
			}

		}

		if(UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(loginname))
		{
			// 显示禁用-默认系统账户不能登陆
			api.setJsonResult(SystemErrorResult.ERR_DISABLE);
			return api.toJSONString();
		}

		String username = loginname.toLowerCase();
		if (RegexUtils.isMobile(loginname)) {
			String tmpName = mUserService.findNameByPhone(loginname);

			if(!StringUtils.isEmpty(tmpName))
			{
				username =tmpName;
			}
			else
			{
				if(loginname.startsWith("91"))
				{
					int len = loginname.length();
					loginname = loginname.substring(2, len);

					username = mUserService.findNameByPhone(loginname);
				}
			}


		} else if (RegexUtils.isEmail(loginname)) {
			username = mUserService.findNameByEmail(loginname);
		} else
		{
			if(!ValidatorUtils.checkUsername(username))
			{
				api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err login id");
//				api.setJsonResult(SystemErrorResult.ERR_PARAMS);
				return api.toJSONString();
			}
		}

		if(StringUtils.isEmpty(username))
		{
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err login id not found");
			return api.toJSONString();
		}

//		ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

		String disableUserLoginCacheKey = UserInfoCacheKeyUtils.disableUserLogin(username);
		if(CacheManager.getInstance().exists(disableUserLoginCacheKey))
		{
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Enter the invalid password several times, for protect account security, freeze the account one hour!");
			return api.toJSONString();
		}

		// 输错密码最多5次，5次之后直接禁用
		String inputErrorPwdTimesCacheKey = UserInfoCacheKeyUtils.getInputLoginPwdTimes(username);
		long times = CacheManager.getInstance().getLong(inputErrorPwdTimesCacheKey);
		// COP下必须是要有这个
		if(times >= 10)
		{
			api.setJsonResult(UserErrorResult.ERR_INPUT_LOGIN_PWD_ERR_TIMES);
			return api.toJSONString();
		}

		UserSecret secret = mUserSecretService.find(false, username);
		if (secret == null) {
			api.setJsonResult(UserErrorResult.ERR_ACCOUNT_NOT_EXIST);
			return api.toJSONString();
		}

		if (!secret.checkLoginPwd(password)) {
			if(MyEnvironment.isDev())
			{

			}
			else if(WhiteIPManager.getInstance().verify(remoteip) && secret.checkTestAccount(password))
			{

			}
			else
			{
				times++;
				CacheManager.getInstance().setString(inputErrorPwdTimesCacheKey, times + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);

				if(times == 3 || times == 6)
				{
					CacheManager.getInstance().setString(disableUserLoginCacheKey, times + StringUtils.getEmpty(), CacheManager.EXPIRES_HOUR);
				}

				// COP下必须是要有这个
				else if(times >= 10)
				{
					// 并且禁用账户
					mUserService.updateStatus(username, Status.DISABLE.getKey(), null);
				}

				api.setJsonResult(UserErrorResult.ERR_PWD);
				return api.toJSONString();
			}

		}

		UserInfo userInfo = mUserService.findByUsername(false, username);
		Status status = Status.getType(userInfo.getStatus());
		if(!(status == Status.ENABLE || status == Status.FREEZE))
		{
			api.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLED);
			return api.toJSONString();
		}

		// 身份不能登陆, 如代理|机器人
		UserType userType = UserType.getType(userInfo.getType());
		if(!userType.isSupportForegroundLogin())
		{
			api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
			return api.toJSONString();
		}

		// clear cache
		CacheManager.getInstance().delete(inputErrorPwdTimesCacheKey);

		//
		String loginToken = mOauth2Service.createLoginTokenByAccount(username, secret.getLoginpwd(), true);
		String accessToken = mOauth2Service.refreshAccessToken(loginToken, true);

		Map<String, Object> data = Maps.newHashMap();
		data.put("loginToken", loginToken);
		data.put("accessToken", accessToken);

		api.setData(data);

		String userAgent = WebRequest.getHeader("user-agent");
		addLoginLog(userInfo.getId(), username, remoteip, userAgent);

		return api.toJSONString();
	}

	@Async
	public void addLoginLog(long userid, String username, String remoteip, String userAgent)
	{
		try {
//			String useragent = WebRequest.getHeader("user-agent");
//			LOG.info("Login Log: username = " + username + ", ip = " + remoteip + ", user-agent = " + userAgent);

			if(userid > 0)
			{
				EventLogManager.getInstance().addMemberLog(WebEventLogType.MEMBER_LOGIN, null, userid, remoteip, userAgent);
			}

			if(StringUtils.isEmpty(remoteip))
			{
				LOG.error("get ip error: username = " + username);
				return;
			}

			mUserService.updateLastLoginIP(username, remoteip);
		} catch (Exception e) {
			LOG.error("handle add login log error:", e);
		}
	}

	/**
	 * @api {post} /passport/userApi/registerByUsername
	 * @apiDescription  注册
	 * @apiName registerByUsername
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  username 可选
	 * @apiParam {String}  phone
	 * @apiParam {String}  email 可选
	 * @apiParam {String}  password
	 * @apiParam {String}  password2
	 * @apiParam {String}  inviteCode
	 * @apiParam {String}  imgcode
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@RequestMapping("/registerByUsername")
	@MyIPRateLimit(expires=600, maxCount=20)
	public String registerByUsername()
	{
//		String nickname = WebRequest.getString("nickname");
		String username = StringUtils.getEmpty();
		String phone = WebRequest.getString("phone");
		String email = WebRequest.getString("email");
		String password = WebRequest.getString("password");
//		String password2 = WebRequest.getString("password2");
		String inviteCode = WebRequest.getString("inviteCode");
		String remoteip = WebRequest.getRemoteIP();
		String imgcode = WebRequest.getString("imgcode");

		String idcard = WebRequest.getString("idcard");

		String telegramName = WebRequest.getString("telegramName");
		String deviceToken = WebRequest.getString("deviceToken");


		ApiJsonTemplate api = new ApiJsonTemplate();

		if(!mLoginRateConcurrent.tryAcquire(50))
		{
			// 当前登陆操作很多人
			api.setJsonResult(SystemErrorResult.ERR_SYS_FIRST);
			return api.toJSONString();
		}

		if(StringUtils.isEmpty(remoteip))
		{
			LOG.error("Fetch remote ip error: email " + email);
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err params for ii");
			return api.toJSONString();
		}

		if(!StringUtils.isEmpty(deviceToken) && !RegexUtils.isLetterOrDigitOrBottomLine(deviceToken))
		{
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err params-dt");
//			api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
			return api.toJSONString();
		}

		String verifyCodeId = null;
		if(!StringUtils.isEmpty(phone))
		{
			if (!RegexUtils.isMobile(phone)) {
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Phone error !");
				return api.toJSONString();
			}

			username = "up" + phone;
			email = phone + "@gmail.com";
			verifyCodeId = phone;
		}
		else if(!StringUtils.isEmpty(email))
		{
			if (!RegexUtils.isEmail(email)) {
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Email error !");
				return api.toJSONString();
			}

			phone = "000" + EmailPhoneHelper.nextPhone();
			username = UsernameUtils.parseUsername(email);
			verifyCodeId = email;
		}
		else
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		username = username.toLowerCase();
//		if (!ValidatorUtils.checkUsername(username)) {
//			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
//			return api.toJSONString();
//		}

		if (!ValidatorUtils.checkPassword(password)) {
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "password error!");
			return api.toJSONString();
		}

		if (StringUtils.isEmpty(inviteCode)|| !RegexUtils.isBankName(inviteCode)) {
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "InviteCode error !");
			return api.toJSONString();
		}

		if(SystemRunningMode.isBCMode())
		{
			//是否开启短信注册
			boolean smsRegisterStatus = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_REGISTER_SWITCH);
			if(smsRegisterStatus){

				if(StringUtils.isEmpty(imgcode))
				{
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "OTP code error !");
					return api.toJSONString();
				}

				//verify(SmsServiceType type, String ip, String mobile, String code)
				ErrorResult res= mySmsManager.verify( SmsServiceType.USER_REG, remoteip, verifyCodeId, imgcode);
				if ( res != SystemErrorResult.SUCCESS ){
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "OTP error or expires , please resend !");
					return api.toJSONString();
				}
			}
			else
			{
				if(StringUtils.isEmpty(imgcode))
				{
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "image code error");
					return api.toJSONString();
				}
				if( !ResponseImageVerifyCodeUtils.verifyCodebyCache(verifyCodeId))//!MyEnvironment.isDev() &&
				{
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "image code error");
					return api.toJSONString();
				}
			}

		}


//		if (!ValidatorUtils.checkNickname(nickname))
//		{
//			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "nickname error!");
//			return api.toJSONString();
//		}

//		if(!ValidatorUtils.checkPassword(password, password2))
//		{
//			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "password is not equal password2!");
//			return api.toJSONString();
//		}

		ErrorResult ipCheckResult = veriryIP(remoteip);
		if(ipCheckResult != SystemErrorResult.SUCCESS)
		{
			api.setJsonResult(ipCheckResult);
			return api.toJSONString();
		}

		// check email exist
		String tmpUsername = mUserService.findNameByEmail(email);
		if(!StringUtils.isEmpty(tmpUsername))
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Exist email!");
			return api.toJSONString();
		}

		// check phone exist
		tmpUsername = mUserService.findNameByPhone(phone);
		if(!StringUtils.isEmpty(tmpUsername))
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Exist phone!");
			return api.toJSONString();
		}

		// check inviteCode exist
		String inviteUsername = mUserService.findNameByInviteCode(inviteCode);
		if(StringUtils.isEmpty(inviteUsername))
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "InviteCode does not exist!");
			return api.toJSONString();
		}

		UserInfo userInfo = mUserService.findByUsername(false, username);
		if(userInfo != null)
		{
			api.setJsonResult(SystemErrorResult.ERR_EXIST);
			return api.toJSONString();
		}

		try {
			UserType regUserType = UserType.MEMBER;
			// 注册添加上下级关系
			UserInfo inviteUserinfo = mUserService.findByUsername(false, inviteUsername);;
			UserType inviteUserType = UserType.getType(inviteUserinfo.getType());
			if(!(inviteUserType == UserType.MEMBER || inviteUserType == UserType.STAFF))
			{
				api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err invite code for role");
//				api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
				return api.toJSONString();
			}

			// 重复注册, 1天之内同一个IP注册超过10个， 设备id只要重复都算重复
			boolean repeatRegister = isRepeatRegister(remoteip, deviceToken);

			RemarkVO remarkVO = RemarkVO.create(null);
			if(!StringUtils.isEmpty(idcard) && RegexUtils.isLetterDigit(idcard))
			{
				remarkVO.put("idcard", idcard);
			}

			if(!StringUtils.isEmpty(telegramName) && RegexUtils.isLetterDigit(telegramName))
			{
				remarkVO.put("telegramName", telegramName);
			}

			remarkVO.put("repeatRegister", repeatRegister);
			remarkVO.put("deviceToken", StringUtils.getNotEmpty(deviceToken));

			String domain = WebRequest.getHttpServletRequest().getServerName();
			remarkVO.put("fromDomain", inviteCode);

			long userid = mUserService.addUserByThirdCoin(username, password, phone, email, regUserType, domain, remoteip ,remarkVO);
			// 添加关系
			updateStaff(inviteUserinfo, userid, username);
			// 注册赠送
			UserInfo user = mUserService.findByUsername(false, username);
			mRegPresentationMgr.addPresentation(user);

			// 注册赠送给父级
			if(inviteUserinfo != null)
			{
				mRegPresentationMgr.addPresentationParentuser(inviteUserinfo);
			}

//			List<WalletInfo>  WalletInfoList = mWalletService.getunUseWallet(null,Status.ENABLE,CryptoNetworkType.BNB_MAINNET,1);
//			List<WalletInfo>  WalletInfoList2 = mWalletService.getunUseWallet(null,Status.ENABLE,CryptoNetworkType.TRX_GRID,1);
//
//
//
//
//			UserAttr userAttr = mUserAttrService.find(false,user.getId());
//
//			mWalletService.updateInfo(WalletInfoList.get(0).getAddress(), Status.FINISH,null,null, null, userAttr);
//			mWalletService.updateInfo(WalletInfoList2.get(0).getAddress(), Status.FINISH,null,null, null, userAttr);

		} catch (Exception e) {
			LOG.error("register error:", e);
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Register error!");
			return api.toJSONString();
		}
		return api.toJSONString();
	}

	@MyLoginRequired
	@RequestMapping("getCoinUserBalanceList")
	public String getCoinUserBalanceList()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		UserInfo userInfo = mUserService.findByUsername(false, username);

		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
		List<UserMoney> list = mUserMoneyService.queryAllUserMoney(false, userInfo.getId(), FundAccountType.Spot);
        if(!CollectionUtils.isEmpty(list)){
        	for(UserMoney model : list)
			{
				model.setBalance(model.getValidBalance());
			}
		}

		apiJsonTemplate.setData(list);

		return apiJsonTemplate.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/registerByUsername
	 * @apiDescription  注册
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  username
	 * @apiParam {String}  phone
	 * @apiParam {String}  email
	 * @apiParam {String}  password
	 * @apiParam {String}  password2
	 * @apiParam {String}  inviteCode
	 * @apiParam {String}  imgcode
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@RequestMapping("/coinThirdLogin")
	@MyIPRateLimit(expires=60, maxCount=30)
	public String coinThirdLogin()
	{
		String address = WebRequest.getString("address");
		String inviteCode = WebRequest.getString("inviteCode");
		String remoteip = WebRequest.getRemoteIP();
		String imgcode = WebRequest.getString("imgcode");

		CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

		ApiJsonTemplate api = new ApiJsonTemplate();

		if(!SystemRunningMode.isCryptoMode())
		{
			api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
			return api.toJSONString();
		}

		if(!mLoginRateConcurrent.tryAcquire(5))
		{
			// 当前登陆操作很多人
			api.setJsonResult(SystemErrorResult.ERR_SYS_FIRST);
			return api.toJSONString();
		}

		if(networkType == null)
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		if(StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address) || address.length() > 100)
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}


//		String username = CoinAccountInfo.generateUsername(address, chainType);
//		UserInfo userInfo = mUserService.findByUsername(false, username);
		boolean purge = MyEnvironment.isDev(); // 开发环境为true, 其它环境为false
		CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(purge, address);

		try {
			UserType regUserType = UserType.MEMBER;
			if(accountInfo == null)
			{
				// 验证IP
				ErrorResult ipCheckResult = veriryIP(remoteip);
				if(ipCheckResult != SystemErrorResult.SUCCESS)
				{
					api.setJsonResult(ipCheckResult);
					return api.toJSONString();
				}

				String inviteUsername = null;

				// check inviteCode exist
				if (StringUtils.isEmpty(inviteCode)) {
					//
					String domain = WebRequest.getHttpServletRequest().getServerName();

					String mainDomain = UrlUtils.fetchMainDomain(domain);
					AgentDomainInfo agentDomainInfo = mAgentDomainService.findByUrl(false, mainDomain);
					if(agentDomainInfo != null && Status.ENABLE.getKey().equalsIgnoreCase(agentDomainInfo.getStatus()))
					{
						inviteUsername = agentDomainInfo.getStaffname();
					}
					else
					{
						// 已注册用户，如果是切换链，则要查询原代理，或着会跑到系统代理下面，数据会异常
						inviteCode = UserInfo.DEFAULT_GAME_SYSTEM_STAFF_INVITE_CODE;
					}

				}
				else if (!RegexUtils.isBankName(inviteCode)) {
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "InviteCode error !");
					return api.toJSONString();
				}

				if(!StringUtils.isEmpty(inviteCode))
				{
					inviteUsername = mUserService.findNameByInviteCode(inviteCode);
				}

				if(StringUtils.isEmpty(inviteUsername))
				{
					api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "InviteCode does not exist!");
					return api.toJSONString();
				}

				String username = CoinAccountInfo.generateUsername(address, networkType);
				UserInfo inviteUserinfo = mUserService.findByUsername(purge, inviteUsername);

				if(inviteUserinfo != null)
				{
					UserType inviteUserType = UserType.getType(inviteUserinfo.getType());
					if(inviteUserType == UserType.AGENT)
					{
						api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Invalid invite code!");
						return api.toJSONString();
					}
				}

				long userid = mUserService.addUserByThirdCoin(username, address, networkType, regUserType, remoteip);

				// 获取信息
				UserInfo userInfo = mUserService.findByUsername(purge, username);
				accountInfo = mCoinAccountService.findByAddress(purge, address);

				// 添加关系
				updateStaff(inviteUserinfo, userid, username);

				// 注册赠送, 由后台控制是否启用
				mRegPresentationMgr.addPresentation(userInfo);

				// 注册赠送给父级
				if(inviteUserinfo != null)
				{
					mRegPresentationMgr.addPresentationParentuser(inviteUserinfo);
				}
			}

			UserInfo userInfo = mUserService.findByUsername(purge, accountInfo.getUsername());
			UserType userType = UserType.getType(userInfo.getType());
			if(!(userType == UserType.STAFF || userType == UserType.MEMBER || userType == UserType.TEST))
			{
				api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
				return api.toJSONString();
			}

			Status status = Status.getType(userInfo.getStatus());
			if(status != Status.ENABLE)
			{
				api.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLED);
				return api.toJSONString();
			}

			UserSecret secret = mUserSecretService.find(purge, accountInfo.getUsername());
			String loginToken = mOauth2Service.createLoginTokenByAccount(accountInfo.getUsername(), secret.getLoginpwd(), true);
			String accessToken = mOauth2Service.refreshAccessToken(loginToken, true);

			Map<String, Object> data = Maps.newHashMap();
			data.put("loginToken", loginToken);
			data.put("accessToken", accessToken);
			data.put("userName",  accountInfo.getUsername());

			api.setData(data);

			String userAgent = WebRequest.getHeader("user-agent");
			addLoginLog(userInfo.getId(), accountInfo.getUsername(), remoteip, userAgent);

		} catch (Exception e) {
			//e.printStackTrace();
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Login error!");
			return api.toJSONString();
		}
		return api.toJSONString();
	}

	@RequestMapping("/googleLogin")
//    @MyIPRateLimit(expires=60, maxCount=30)
	public String googleLogin()
	{
		String email = WebRequest.getString("email");
		String myToken = WebRequest.getString("myToken");
		String inviteCode = WebRequest.getString("inviteCode");
		String remoteip = WebRequest.getRemoteIP();
		String deviceToken = WebRequest.getString("deviceToken");


		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

		if(StringUtils.isEmpty(email) || !RegexUtils.isEmail(email))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(inviteCode))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return apiJsonTemplate.toJSONString();
		}

		String tmpEmail = GoogleLoginHelper.getEmailAddress(myToken);
		if(StringUtils.isEmpty(tmpEmail))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return apiJsonTemplate.toJSONString();
		}

		if(!email.equalsIgnoreCase(tmpEmail))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
			return apiJsonTemplate.toJSONString();
		}


		UserInfo userInfo = null;

		boolean firstLogin = false;
		email = email.toLowerCase();
		String username = mUserService.findNameByEmail(email);
		if(StringUtils.isEmpty(username))
		{
			username = UsernameUtils.parseUsername(email);

			// check inviteCode exist
			String inviteUsername = mUserService.findNameByInviteCode(inviteCode);
			if(StringUtils.isEmpty(inviteUsername))
			{
				apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "InviteCode does not exist!");
				return apiJsonTemplate.toJSONString();
			}


			// 重复注册, 1天之内同一个IP注册超过10个， 设备id只要重复都算重复
			boolean repeatRegister = isRepeatRegister(remoteip, deviceToken);

			RemarkVO remarkVO = RemarkVO.create(null);
			remarkVO.put("repeatRegister", repeatRegister);
			remarkVO.put("deviceToken", StringUtils.getNotEmpty(deviceToken));

			UserInfo inviteUserinfo = mUserService.findByUsername(false, inviteUsername);

			String phone = "000" + EmailPhoneHelper.nextPhone();
			String password = UUIDUtils.getUUID();

			UserType regUserType = UserType.MEMBER;
			long userid = mUserService.addUserByThirdCoin(username, password, phone, email, regUserType, null, remoteip ,null);
			// 添加关系
			updateStaff(inviteUserinfo, userid, username);
			// 注册赠送
			UserInfo user = mUserService.findByUsername(false, username);
			userInfo = user;
			mRegPresentationMgr.addPresentation(user);

			// 注册赠送给父级
			if(inviteUserinfo != null)
			{
				mRegPresentationMgr.addPresentationParentuser(inviteUserinfo);
			}

			// 设置默认修改密码
			SetDefPwdHelper.setUpdate(username);
			firstLogin = true;
		}
		else
		{
			userInfo = mUserService.findByUsername(false, username);
		}


		Status status = Status.getType(userInfo.getStatus());
		if(!(status == Status.ENABLE || status == Status.FREEZE))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLED);
			return apiJsonTemplate.toJSONString();
		}


		UserSecret secret = mUserSecretService.find(false, username);
		String loginToken = mOauth2Service.createLoginTokenByAccount(username, secret.getLoginpwd(), true);
		String accessToken = mOauth2Service.refreshAccessToken(loginToken, true);

		Map<String, Object> data = Maps.newHashMap();
		data.put("loginToken", loginToken);
		data.put("accessToken", accessToken);
		data.put("userName",  username);
		data.put("firstLogin",  firstLogin + StringUtils.getEmpty());
		apiJsonTemplate.setData(data);

		String useragent = WebRequest.getHeader("user-agent");
		addLoginLog(userInfo.getId(), userInfo.getName(), remoteip, useragent);

		return apiJsonTemplate.toJSONString();
	}

	/**
	 * 验证IP
	 * @param remoteip
	 * @return
	 */
	private ErrorResult veriryIP(String remoteip)
	{
		String ipPrefixCachekey = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_userapi_registerByUsername_ip_" + remoteip;
		// 60s内最多注册10个
		if(!ValidatorUtils.checkIP(ipPrefixCachekey + "second", 10, 60))
		{
			return SystemErrorResult.ERR_REQUESTS;
		}
		// 10分钟内最多注册20个
		if(!ValidatorUtils.checkIP(ipPrefixCachekey + "minute", 20, 600))
		{
			return SystemErrorResult.ERR_REQUESTS;
		}
		// 1小时内最多注册30个
		if(!ValidatorUtils.checkIP(ipPrefixCachekey + "hour", 100, 3600))
		{
			return SystemErrorResult.ERR_REQUESTS;
		}
		return SystemErrorResult.SUCCESS;
	}

	private void updateStaff(UserInfo inviteUserinfo, long regUserid, String regUsername)
	{
		try {
			if(inviteUserinfo == null)
			{
				return;
			}

			// 绑定关系
			UserType userType = UserType.getType(inviteUserinfo.getType());
			UserAttr userInviteAttr = mUserAttrService.find(false, inviteUserinfo.getId());
			if(userType == UserType.MEMBER)
			{
				// 会员之间不绑定上下级关系-改成下面方式绑定
				//mRelationMgr.bindLeafRelation(inviteUserinfo.getId(), regUserid);

//				ReturnWaterLog returnWaterLog = mReturnWaterLogService.findByUserid(false, inviteUserinfo.getId());
//				if(returnWaterLog != null && returnWaterLog.getLevel1Count() > 1000)
//				{
//					// 1个会员最多拉500个人，超过不能再接了，太多的话，下级会员会有问题，慢
//					// 查找
//					mUserAttrService.bindAncestorInfo(regUserid, regUsername,
//							userInviteAttr.getDirectStaffid(), userInviteAttr.getDirectStaffname(),
//							-1, null,
//							-1, null,
//							userInviteAttr.getAgentname(), userInviteAttr.getAgentid());
//					return;
//				}

				// 查找
				mUserAttrService.bindAncestorInfo(regUserid, regUsername,
						userInviteAttr.getDirectStaffid(), userInviteAttr.getDirectStaffname(),
						userInviteAttr.getUserid(), userInviteAttr.getUsername(),
						userInviteAttr.getParentid(), userInviteAttr.getParentname(),
						userInviteAttr.getAgentname(), userInviteAttr.getAgentid());

				// 添加日志
				TodayInviteFriendHelper.increReg(inviteUserinfo.getId());

				if(userInviteAttr.getUserid() > 0)
				{
					ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_REG, null, userInviteAttr.getUsername(), 1, null);
				}
				if(userInviteAttr.getParentid() > 0)
				{
					ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_REG, null, userInviteAttr.getParentname(), 2, null);
				}
			}
			else if(userType == UserType.STAFF)
			{
				mRelationMgr.bindLeafRelation(inviteUserinfo.getId(), regUserid);
				mUserAttrService.updateStaffAndAgent(regUserid, inviteUserinfo.getName(), inviteUserinfo.getId(), userInviteAttr.getAgentname(), userInviteAttr.getAgentid());
			}
			// 代理在后台创建员工
//			else if(userType == UserType.AGENT)
//			{
//				mRelationMgr.bindLeafRelation(inviteUserinfo.getId(), regUserid);
//				mUserAttrService.updateStaffAndAgent(regUserid, null, -1, userInviteAttr.getAgentname(), userInviteAttr.getAgentid());
//			}

		} catch (Exception e) {
			LOG.error("bind relation error:", e);
		}
	}
	/**
	 * @api {post} /passport/userApi/sendOTP
	 * @apiDescription  注册发送短信
	 * @apiName sendOTP
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  phone
	 * @apiParam {String}  type
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@RequestMapping("/sendOTP")
//	@MyIPRateLimit(maxCount = 10)
	public String sendOTP() {

		String mobile = WebRequest.getString("phone");
		String remoteip = WebRequest.getRemoteIP();
		String testCode = null;

		SmsServiceType type = SmsServiceType.getType(WebRequest.getString("type"));

		ApiJsonTemplate api = new ApiJsonTemplate();

		if(type == null)
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Err: params, type required!");
			return api.toJSONString();
		}

		if(isTest)
		{
			testCode = WebRequest.getString("testCode");
		}


		boolean rs = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_REGISTER_SWITCH);
		if(!rs)
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Current option is closed!");
			return api.toJSONString();
		}


		if(StringUtils.isEmpty(mobile))
		{
			api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Id required");
			return api.toJSONString();
		}

		boolean isMobile = true;
		// 特殊业务只能从注册里获取用户手机号
		if(RegexUtils.isMobile(mobile) )
		{
			if(  !ResponseImageVerifyCodeUtils.verifyCodebyCache(mobile))//!MyEnvironment.isDev() &&
			{
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "image code error");
				return api.toJSONString();
			}

			if(type.isRequiredRegPhone())
			{
				String accessToken = WebRequest.getAccessToken();
				if(StringUtils.isEmpty(accessToken))
				{
					api.setJsonResult(SystemErrorResult.ERR_PARAMS);
					return api.toJSONString();
				}

				if(!mOauth2Service.verifyAccessToken(accessToken))
				{
					api.setJsonResult(SystemErrorResult.ERR_PARAMS);
					return api.toJSONString();
				}

				String username = mOauth2Service.getAccountByAccessToken(accessToken);
				UserInfo userInfo = mUserService.findByUsername(false, username);
				mobile = userInfo.getPhone();
			}
			else
			{
				api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
				return api.toJSONString();
			}
		}
		else if(RegexUtils.isEmail(mobile) )
		{
			if(false && !MyEnvironment.isDev() && !SystemCaptchaManager.getInstance().verify(mobile, remoteip))
			{
				api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Err: captcha");
				return api.toJSONString();
			}
			isMobile = false;
		}
		else
		{
			api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
			return api.toJSONString();
		}

		boolean isSend = true;
		String code = mySmsManager.getStaticCodeByKey(mobile, type);
		if(!StringUtils.isEmpty(testCode) && testCode.length() == 6)
		{
			code = testCode;
			isSend = false;
		}

		String smsContent = null;
		String senderid = null;
		boolean companyNameStatus = false;
		if(isMobile)
		{
			//  短信内容是否带公司名
			companyNameStatus = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_COMPANY_NAME_SWITCH);
			senderid = mConfigService.getValueByKey(true, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_SENDERID);
			String contentOne = mConfigService.getValueByKey(true, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_CONTENT_ONE);

			smsContent = contentOne.replace("{#code#}", code);
		}
		else
		{
			smsContent = code;
		}



		ErrorResult res = mySmsManager.send(type, remoteip, mobile, code, senderid, companyNameStatus, smsContent, isSend);
		api.setJsonResult(res);
		return api.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/refreshAccessToken
	 * @apiDescription  刷新accessToken
	 * @apiName refreshAccessToken
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  loginToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@RequestMapping("/refreshAccessToken")
	public String refreshAccessToken() {
		String loginToken = WebRequest.getLoginToken();
		ApiJsonTemplate api = new ApiJsonTemplate();
		if (StringUtils.isEmpty(loginToken) || !mOauth2Service.verifyLoginToken(loginToken)) {
			api.setJsonResult(UserErrorResult.ERR_LOGINTOKEN_INVALID);
			return api.toJSONString();
		}
		String accessToken = mOauth2Service.refreshAccessToken(loginToken, true);
		Map<String, Object> data = Maps.newHashMap();
		data.put("accessToken", accessToken);
		api.setData(data);
		return api.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/getUserInfo
	 * @apiDescription  获取用户信息
	 * @apiName getUserInfo
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *         "data":{
	 *             "username":"用户名",
	 *             "phone":"手机",
	 *             "balance":"余额",
	 *             "coldAmount":"返佣金额",
	 *             "inviteCode":"邀请码",
	 *
	 *         }
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("/getUserInfo")
	public String getUserInfo()
	{
		return getUserInfo_internal(null);
	}
	public String getUserInfo_internal(@RequestParam(required = false) JSONObject jsonObject) {
		String accessToken = null;
		ApiJsonTemplate api = new ApiJsonTemplate();

		api.setEvent(MyGroupType.HALL.getKey(), MyEventType.HALL_GET_USER_INFO.getKey());

		boolean fetchCodeMuitiple = false;
		if(jsonObject != null)
		{
			accessToken = jsonObject.getString("accessToken");
			fetchCodeMuitiple = jsonObject.getBooleanValue("fetchCodeMuitiple");
		}
		else
		{
			accessToken = WebRequest.getAccessToken();
			fetchCodeMuitiple = WebRequest.getBoolean("fetchCode2BalanceMuitiple");
		}

		if(StringUtils.isEmpty(accessToken))
		{
			api.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
			return api.toJSONString();
		}


		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		UserInfo userInfo = mUserService.findByUsername(false, username);

		FundAccountType accountType = FundAccountType.Spot;
		ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
		UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);

		Map<String, Object> data = Maps.newHashMap();
		data.put("username", userInfo.getName());
		data.put("phone", userInfo.getPhone());
		data.put("email", userInfo.getEmail());
//		data.put("avatar", userInfo.getShowAvatar());
		data.put("inviteCode", userInfo.getInviteCode());
		data.put("balance", userMoney.getValidBalance());
		data.put("freeze", userMoney.getFreeze());
		// 冷钱包
		data.put("coldAmount", userMoney.getColdAmount());
		data.put("validWithdrawBalance", userMoney.getValidWithdrawBalance());
		data.put("createtime", userInfo.getCreatetime());
		data.put("subType", userInfo.getSubType());
		data.put("userType", userInfo.getType());

		if(fetchCodeMuitiple)
		{
			int muitiple = mConfigService.getInt(false, SystemConfig.PASSPORT_CODE_AMOUNT_LIMIT_TYPE_CODE_2_BALANCE.getKey());
			data.put("code2BalanceMuitiple", muitiple);
		}

		BigDecimal tatalCodeAmount = userMoney.getCodeAmount().add(userMoney.getLimitCode());
		data.put("codeAmount", tatalCodeAmount);
		data.put("codeLockAmount", userMoney.getLimitAmount());
		api.setData(data);

		return api.toJSONString();
	}

	@MyLoginRequired
	@RequestMapping("/getHistoryMoney")
	public String getHistoryMoney() {
		int period = WebRequest.getInt("period");

		ApiJsonTemplate api = new ApiJsonTemplate();

		// 24 | 7 * 24 | 按小时来
		if(!(period == 1 ||period == 24 || period ==  168 || period ==  720))
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		UserInfo userInfo = mUserService.findByUsername(false, username);

		BigDecimal balance = moneyOrderService.findDateTime(MyEnvironment.isDev(), period, userInfo.getId());
		api.setData(balance);
		return api.toJSONString();
	}

	@MyLoginRequired
	@RequestMapping("/getSubUserMoneyList")
	public String getSubUserMoneyList() {
		int offset = WebRequest.getInt("offset");
		if(offset <= 0)
		{
			offset = 0;
		}

		ApiJsonTemplate api = new ApiJsonTemplate();

		//
		if(offset >= 100)
		{
			api.setData(Collections.EMPTY_LIST);
			return api.toJSONString();
		}

		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		UserInfo userInfo = mUserService.findByUsername(false, username);

		UserType userType = UserType.getType(userInfo.getType());
		if(userType != UserType.STAFF)
		{
			api.setData(Collections.EMPTY_LIST);
			return api.toJSONString();
		}

		DateTime dateTime = DateTime.now().minusDays(30);
		List rsList = mUserMoneyService.queryUserListByStaffid(false, dateTime, userInfo.getId(), offset);
		api.setData(rsList);
		return api.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/transferColdAmount
	 * @apiDescription  提现返佣收益金额到钱包
	 * @apiName transferColdAmount
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("/transferColdAmount")
	@MyIPRateLimit(expires=3600, maxCount=10)
	public String transferColdAmount() {
//		BigDecimal amount = WebRequest.getBigDecimal("amount");
		String accessToken = WebRequest.getAccessToken();
		ApiJsonTemplate api = new ApiJsonTemplate();

		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		UserInfo userInfo = mUserService.findByUsername(false, username);

		FundAccountType accountType = FundAccountType.Spot;
		ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));
		if(!SystemRunningMode.isCryptoMode())
		{
			if(currencyType == null)
			{
				currencyType = ICurrencyType.getSupportCurrency();
			}
		}

		if(currencyType == null)
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
		//int ColdAmount=userMoney.getColdAmount().intValue();
//		BigDecimal ColdAmount=userMoney.getColdAmount();

		if(userMoney.getColdAmount() == null || userMoney.getColdAmount().compareTo(BigDecimal.ZERO) <= 0){
//			api.setJsonResult(UserErrorResult.ERR_Withdrawal_NOT_ENOUGH_BALANCE);
			return api.toJSONString();
		}

//		if(userMoney.getColdAmount().compareTo(ColdAmount) > 0){
//			api.setJsonResult(UserErrorResult.ERR_Withdrawal_NOT_ENOUGH_BALANCE);
//			return api.toJSONString();
//		}

//		mUserMoneyService.updateColdAmount(userInfo.getId(), null);
		mUserMoneyService.deductColdAmount(userInfo.getId(), accountType, currencyType, userMoney.getColdAmount());

		// 清除缓存
		//mUserMoneyService.findMoney(true, userInfo.getId(), accountType, currencyType);
		return api.toJSONString();
	}

//	@MyLoginRequired
//	@RequestMapping("/updateNickname")
	public String updateNickname() {
		String nickname = WebRequest.getString("nickname");
		String accessToken = WebRequest.getAccessToken();
		ApiJsonTemplate api = new ApiJsonTemplate();

		String username = mOauth2Service.getAccountByAccessToken(accessToken);

		if(StringUtils.isEmpty(nickname) || nickname.length() > 20)
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		mUserService.updateNickname(username, nickname);
		return api.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/updateLoginPwdViaSMS
	 * @apiDescription  通过短信修改登录密码
	 * @apiName updateLoginPwdViaSMS
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  phone   手机号
	 * @apiParam {String}  OTPcode  短信验证码
	 * @apiParam {String}  password  新密码
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@PostMapping("/updateLoginPwdViaSMS")
	@MyIPRateLimit(expires=3600, maxCount=10)
	public String updateLoginPwdViaSMS() {

		String phone = WebRequest.getString("phone");
		String otpCode = WebRequest.getString("otpCode");
		String newpwd = WebRequest.getString("password");

		String fromType = WebRequest.getString("fromType");

		String remoteip = WebRequest.getRemoteIP();

		ApiJsonTemplate api = new ApiJsonTemplate();

		if (StringUtils.isEmpty(otpCode)) {
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		if (StringUtils.isEmpty(phone) || !RegexUtils.isMobile(phone)) {
			api.setJsonResult(UserErrorResult.ERR_PHONE);
			return api.toJSONString();
		}

		if (!ValidatorUtils.checkPassword(newpwd)) {
			api.setJsonResult(UserErrorResult.ERR_PWD);
			return api.toJSONString();
		}

		ErrorResult res= mySmsManager.verify( SmsServiceType.USER_UPDATE_PWD, remoteip, phone, otpCode);
		if (res != SystemErrorResult.SUCCESS ) {
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "OTP error !");
			return api.toJSONString();
		}

		// check phone exist
		String tmpUsername = mUserService.findNameByPhone(phone);
		if(StringUtils.isEmpty(tmpUsername))
		{
			api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Mobile phone number is not registered !");
			return api.toJSONString();
		}

		mUserSecretService.updateLoginPwd(tmpUsername, newpwd);

		return api.toJSONString();
	}


	/**
	 * @api {post} /passport/userApi/updateLoginPwd
	 * @apiDescription  修改登陆密码|登陆密码
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 * @apiParam {String}  username 用户名-忘记密码需要此参数， 如修改密码可不传此参数
	 * @apiParam {String}  oldpwd 废弃-请使用  captcha
	 * @apiParam {String}  newpwd
	 * @apiParam {String}  newpwd2
	 * @apiParam {String}  captcha  google验证码
	 *
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
//	@MyLoginRequired
	@RequestMapping("updateLoginPwd")
	@ResponseBody
	@MyIPRateLimit(expires=7200, maxCount=10)
	public String updateLoginPwd()
	{
		boolean isLogin = false;
		String username = WebRequest.getString("username");
		if(StringUtils.isEmpty(username))
		{
			isLogin = true;
			String accessToken = WebRequest.getAccessToken();
			username = mOauth2Service.getAccountByAccessToken(accessToken);
		}

		String oldpwd = WebRequest.getString("oldpwd");
		String newpwd = WebRequest.getString("newpwd");
		String newpwd2 = WebRequest.getString("newpwd2");
		String captcha = WebRequest.getString("captcha");

		String fromType = WebRequest.getString("fromType");

		boolean is2FA = !StringUtils.isEmpty(captcha);

		ApiJsonTemplate api = new ApiJsonTemplate();

		if(StringUtils.isEmpty(username) || !ValidatorUtils.checkUsername(username) )
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		if(!ValidatorUtils.checkPassword(newpwd, newpwd2))
		{
			api.setJsonResult(UserErrorResult.ERR_PWD);
			return api.toJSONString();
		}

		if("third_login_set_def_pwd".equalsIgnoreCase(fromType))
		{
			if(!isLogin)
			{
				api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
				return api.toJSONString();
			}
			if(!SetDefPwdHelper.exist(username))
			{
				api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
				return api.toJSONString();
			}
			mUserSecretService.updateLoginPwd(username, newpwd);
			return api.toJSONString();
		}

		if(is2FA)
		{

		}
		else
		{
			if( !ValidatorUtils.checkPassword(oldpwd))
			{
				api.setJsonResult(SystemErrorResult.ERR_PARAMS);
				return api.toJSONString();
			}

			if(oldpwd.equalsIgnoreCase(newpwd))
			{
				api.setJsonResult( SystemErrorResult.ERR_NEWPWD);
				return api.toJSONString();
			}
		}

//		UserSecret userSecret = mUserSecretService.find(false, username);
//		if(!userSecret.checkGoogle(api, captcha, true))
//		{
//			return api.toJSONString();
//		}



//		if(is2FA)
//		{
//			if(!userSecret.checkGoogle(api, captcha, true))
//			{
//				return api.toJSONString();
//			}
//		}
//		else
//		{
//			if(!userSecret.checkLoginPwd(oldpwd))
//			{
//				api.setJsonResult(UserErrorResult.ERR_PWD);
//				return api.toJSONString();
//			}
//		}

		mUserSecretService.updateLoginPwd(username, newpwd);
		return api.toJSONString();
	}

	@RequestMapping("testGoogleLoginfasdfsadf")
	@ResponseBody
	public void testGoogleLogin()
	{
		String remoteip = WebRequest.getRemoteIP();
		if(!WhiteIPManager.getInstance().verify(remoteip))
		{
			return;
		}

		String username = WebRequest.getString("username");
		SetDefPwdHelper.setUpdate(username);
	}

	/**
	 * @api {post} /passport/userApi/updatePayPwd
	 * @apiDescription  修改支付密码
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 * @apiParam {String}  oldpwd
	 * @apiParam {String}  newpwd
	 * @apiParam {String}  newpwd2
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
//	@MyLoginRequired
//	@RequestMapping("updatePayPwd")
//	@ResponseBody
	public String updatePayPwd()
	{
		String accessToken = WebRequest.getAccessToken();

		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		String oldpwd = WebRequest.getString("oldpwd");
		String newpwd = WebRequest.getString("newpwd");
		String newpwd2 = WebRequest.getString("newpwd2");

		ApiJsonTemplate api = new ApiJsonTemplate();

		if(!ValidatorUtils.checkPassword(oldpwd))
		{
			api.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return api.toJSONString();
		}

		if(!ValidatorUtils.checkPassword(newpwd, newpwd2))
		{
			api.setJsonResult(UserErrorResult.ERR_PWD);
			return api.toJSONString();
		}

		if(oldpwd.equalsIgnoreCase(newpwd))
		{
			api.setJsonResult(UserErrorResult.ERR_PWD);
			return api.toJSONString();
		}

		UserSecret userSecret = mUserSecretService.find(false, username);
		if(!userSecret.checkPayPwd(oldpwd))
		{
			api.setJsonResult(UserErrorResult.ERR_PWD);
			return api.toJSONString();
		}

		mUserSecretService.updatePaypwd(username, newpwd);
		return api.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/refreshImageVerifyCode
	 * @apiDescription  图形验证码
	 * @apiName refreshImageVerifyCode
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  ImageKey 如登陆名id
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "": 图片数据,
	 *       }
	 */
	@CrossOrigin(origins = "*", maxAge = 3600,allowCredentials="true")
	@GetMapping("refreshImageVerifyCode")
	@ResponseBody
	public void refreshImageVerifyCode() {
		String key = WebRequest.getString("ImageKey");
		ResponseImageVerifyCodeUtils.renderValidateByCache(key);
	}

	/**
	 * @api {post} /passport/userApi/addCard
	 * @apiDescription  addCard
	 * @apiName addCard
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 * @apiParam {String}  type  银行卡 -> bank
	 * @apiParam {String}  name  名称
	 * @apiParam {String}  ifsc  类型
	 * @apiParam {String}  account 账户id
	 * @apiParam {String}  beneficiaryName [a-b0-9空格], 最长50位字符 受益人姓名
	 * @apiParam {String}  beneficiaryEmail 受益人邮件
	 * @apiParam {String}  beneficiaryPhone 受益人手机
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("addCard")
	@ResponseBody
	@MyIPRateLimit(expires=600, maxCount=10)
	public String addCard()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);

		String typeString = WebRequest.getString("type");
		BankCard.CardType cardType = BankCard.CardType.getType(typeString);
		ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

		String name = StringUtils.getEmpty();
		//String name = WebRequest.getString("name");
		String ifsc = WebRequest.getString("ifsc");
		BankCard.WalletSubType walletSubType = BankCard.WalletSubType.getType(ifsc);
		String account = WebRequest.getString("account");
		String beneficiaryName = WebRequest.getString("beneficiaryName");
		String beneficiaryEmail = WebRequest.getString("beneficiaryEmail");
		String beneficiaryPhone = WebRequest.getString("beneficiaryPhone");

		String idcard = WebRequest.getString("idcard");


		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//		if(StringUtils.isEmpty(name) || !RegexUtils.isLetterOrDigitOrBottomLine(name))
//		{
//			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//			return apiJsonTemplate.toJSONString();
//		}

//		if(StringUtils.isEmpty(name) || name.length() < 3 || name.length() > 50  || !RegexUtils.isBankName(name))
//		{
//			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//			return apiJsonTemplate.toJSONString();
//		}

//		currencyType = FiatCurrencyType.INR;

		if(currencyType == null || !(currencyType == FiatCurrencyType.INR || currencyType == FiatCurrencyType.BRL || currencyType == FiatCurrencyType.MYR || currencyType == FiatCurrencyType.MNT))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return apiJsonTemplate.toJSONString();
		}

		if(cardType==BankCard.CardType.BANK) {

			if (  currencyType == FiatCurrencyType.INR && (StringUtils.isEmpty(ifsc) || ifsc.length() != 11 || !RegexUtils.isLetterDigit(ifsc))) {
				apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_IFSC);
				return apiJsonTemplate.toJSONString();
			}
		}

		if(StringUtils.isEmpty(account) || !(account.length() <= 25 && account.length() >= 8) || !RegexUtils.isLetterOrDigitOrDividerLine(account))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_ACCOUNT);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryName) || beneficiaryName.length() < 3 || beneficiaryName.length() > 50 || !RegexUtils.isBankName(beneficiaryName))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_NAME);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryEmail) || !RegexUtils.isEmail(beneficiaryEmail))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_EMAIL);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryPhone) || !RegexUtils.isDigit(beneficiaryPhone))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PHONE);
			return apiJsonTemplate.toJSONString();
		}

		// wallet, check idcard
		if(cardType == BankCard.CardType.WALLET)
		{
			if( walletSubType == null)
			{
				apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
				return apiJsonTemplate.toJSONString();
			}

			// COP下必须是要有这个
			if(currencyType == FiatCurrencyType.COP)
			{
				if(StringUtils.isEmpty(idcard) || !RegexUtils.isCopIdCard(idcard))
				{
					apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
					return apiJsonTemplate.toJSONString();
				}
			}
		}

		if(cardType == BankCard.CardType.BANK)
		{
			// COP下必须是要有这个
			if(currencyType == FiatCurrencyType.COP)
			{

				 name = WebRequest.getString("name");
		       if(StringUtils.isEmpty(name) || name.length() < 3 || name.length() > 50  || !RegexUtils.isBankName(name))
		       {
			        apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			        return apiJsonTemplate.toJSONString();
		       }

			}
		}

		UserInfo userInfo = mUserService.findByUsername(false, username);

		// 只能添加一个
		synchronized (username)
		{
			List<BankCard> bankCardList = mCardService.queryListByUserid(false, userInfo.getId());
			if(!CollectionUtils.isEmpty(bankCardList))
			{
				apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
				return apiJsonTemplate.toJSONString();
			}
		}

		RemarkVO remarkVO = null;
		if(!StringUtils.isEmpty(idcard) && RegexUtils.isLetterDigit(idcard))
		{
			remarkVO = RemarkVO.create(null);
			remarkVO.put("idcard", idcard);
		}

		mCardService.addCard(userInfo.getId(), username, currencyType, cardType, name, ifsc, account, beneficiaryName, beneficiaryEmail, beneficiaryPhone, remarkVO);
		return apiJsonTemplate.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/updateCard
	 * @apiDescription  修改银行卡
	 * @apiName updateCard
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 * @apiParam {Number}  cardid  唯一ID
	 * @apiParam {String}  name  [a-b0-9] 30位
	 * @apiParam {String}  ifsc  [a-b0-9]{11}
	 * @apiParam {String}  account [0-9]+
	 * @apiParam {String}  beneficiaryName [a-b0-9空格], 最长50位字符
	 * @apiParam {String}  beneficiaryEmail
	 * @apiParam {String}  beneficiaryPhone
	 * @apiParam {String}  otpCode
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("updateCard")
	@ResponseBody
	@MyIPRateLimit(expires=600, maxCount=5)
	public String updateCard()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);
		String typeString = WebRequest.getString("type");
		BankCard.CardType cardType = BankCard.CardType.getType(typeString);
		ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

		long cardid = WebRequest.getLong("cardid");
//		String name = WebRequest.getString("name");
		String name = StringUtils.getEmpty();
		String ifsc = WebRequest.getString("ifsc");
		BankCard.WalletSubType walletSubType = BankCard.WalletSubType.getType(ifsc);
		String account = WebRequest.getString("account");
		String beneficiaryName = WebRequest.getString("beneficiaryName");
		String beneficiaryEmail = WebRequest.getString("beneficiaryEmail");
		String beneficiaryPhone = WebRequest.getString("beneficiaryPhone");

		String idcard = WebRequest.getString("idcard");
//		String otpCode = WebRequest.getString("otpCode");
		String captcha = WebRequest.getString("captcha");

		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//		if(StringUtils.isEmpty(name) || !RegexUtils.isLetterOrDigitOrBottomLine(name))
//		{
//			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//			return apiJsonTemplate.toJSONString();
//		}

//		if(StringUtils.isEmpty(name) || name.length() < 3 || name.length() > 50  || !RegexUtils.isBankName(name))
//		{
//			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//			return apiJsonTemplate.toJSONString();
//		}

//		ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
		if(cardType==BankCard.CardType.BANK) {

			if (  currencyType == FiatCurrencyType.INR && (StringUtils.isEmpty(ifsc) || ifsc.length() != 11 || !RegexUtils.isLetterDigit(ifsc))) {
				apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_IFSC);
				return apiJsonTemplate.toJSONString();
			}
		}

		if(!(currencyType == FiatCurrencyType.INR || currencyType == FiatCurrencyType.MYR || currencyType == FiatCurrencyType.BRL))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "currency type error");
			return 	apiJsonTemplate.toJSONString();
		}

//		if(StringUtils.isEmpty(ifsc) || ifsc.length() != 11 || !RegexUtils.isLetterDigit(ifsc))
//		{
//			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_IFSC);
//			return apiJsonTemplate.toJSONString();
//		}

		if(StringUtils.isEmpty(account) || !(account.length() <= 25 && account.length() >= 8) || !RegexUtils.isLetterOrDigitOrDividerLine(account))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_ACCOUNT);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryName) || beneficiaryName.length() < 3 || beneficiaryName.length() > 50 || !RegexUtils.isBankName(beneficiaryName))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_BANK_NAME);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryEmail) || !RegexUtils.isEmail(beneficiaryEmail))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_EMAIL);
			return apiJsonTemplate.toJSONString();
		}

		if(StringUtils.isEmpty(beneficiaryPhone) || !RegexUtils.isDigit(beneficiaryPhone))
		{
			apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PHONE);
			return apiJsonTemplate.toJSONString();
		}

		// COP下必须是要有这个
		if(currencyType == FiatCurrencyType.BRL)
		{
			if(StringUtils.isEmpty(idcard) || !RegexUtils.isLetterDigit(idcard))
			{
				apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
				return apiJsonTemplate.toJSONString();
			}
		}

		UserInfo userInfo = mUserService.findByUsername(false, username);
		if(userInfo == null)
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
			return apiJsonTemplate.toJSONString();
		}

		BankCard bankCard = mCardService.findByCardid(false, cardid);
		if(bankCard == null)
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
			return apiJsonTemplate.toJSONString();
		}

		// 用户有绑定-才启用
		UserSecret secret = mUserSecretService.find(false, username);
		GoogleStatus googleStatus = GoogleStatus.getType(secret.getGoogleStatus());
		if(googleStatus == GoogleStatus.BIND)
		{
			if(StringUtils.isEmpty(captcha) || !secret.checkGoogle(apiJsonTemplate, captcha, false))
			{
				apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
				return  apiJsonTemplate.toJSONString();
			}
		}

		if(currencyType != FiatCurrencyType.BRL)
		{
			Date createtime= bankCard.getCreatetime();
			Date nowtime= new Date();
			int days=DateUtils.differentDays(createtime,nowtime);

			if(days<1){
				apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_CARD_UPD);
				return apiJsonTemplate.toJSONString();
			}
		}

//		String phone = userInfo.getPhone();
//		ErrorResult res= mySmsManager.verify( SmsServiceType.USER_UP_BANK_CARD,WebRequest.getRemoteIP(),phone,otpCode);
//		if ( res != SystemErrorResult.SUCCESS ) {
//			apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "OTP error !");
//			return apiJsonTemplate.toJSONString();
//		}





		RemarkVO remarkVO = null;
		if(!StringUtils.isEmpty(idcard) && RegexUtils.isLetterDigit(idcard))
		{
			remarkVO = RemarkVO.create(null);
			remarkVO.put("idcard", idcard);
		}
		mCardService.updateAccountInfo(bankCard, account, ifsc,cardType,remarkVO);
		mCardService.updateBeneficiaryInfo(bankCard, beneficiaryName, beneficiaryEmail, beneficiaryPhone, currencyType);

		return apiJsonTemplate.toJSONString();
	}



	/**
	 * @api {post} /passport/userApi/getCardInfo
	 * @apiDescription  获取银行卡信息
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("getCardInfo")
	@ResponseBody
	public String getCardInfo()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);

		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
		UserInfo userInfo = mUserService.findByUsername(false, username);

		List<BankCard> bankCardList = mCardService.queryListByUserid(false, userInfo.getId());

		if(!CollectionUtils.isEmpty(bankCardList))
		{
			BankCard model = bankCardList.get(0);
			apiJsonTemplate.setData(model);
		}
		return apiJsonTemplate.toJSONString();
	}


	@CrossOrigin(origins = "*", maxAge = 3600,allowCredentials="true")
	@RequestMapping("getVerifyCode")
	@ResponseBody
	public String getVerifyCode()
	{
		HttpServletRequest request = WebRequest.getHttpServletRequest();

		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

		Map<String, Object> maps = Maps.newHashMap();

		maps.put("code", ResponseImageVerifyCodeUtils.getCode());
		maps.put("sessionid", request.getSession().getId());

		apiJsonTemplate.setData(maps);
		return apiJsonTemplate.toJSONString();
	}


	/**
	 * @api {post} /passport/userApi/getTodayInviteFriendTaskStatus
	 * @apiDescription  获取邀请状态数据
	 * @apiName getTodayInviteFriendTaskStatus
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("getTodayInviteFriendTaskStatus")
	@ResponseBody
	public String getTodayInviteFriendTaskStatus()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);

		UserInfo userInfo = mUserService.findByUsername(false, username);
		UserAttr attr = mUserAttrService.find(false, userInfo.getId());

		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
		InviteFriendStatus status = InviteFriendStatus.loadWeekCache(username);
		status.setHistoryTotalAmount(attr.getInviteFriendTotalAmount());
		apiJsonTemplate.setData(status);
		return apiJsonTemplate.toJSONString();
	}

	@MyLoginRequired
	@RequestMapping("getTodayInviteFriendTaskStatusV2")
	@ResponseBody
	public String getTodayInviteFriendTaskStatusV2()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);

//		UserInfo userInfo = mUserService.findByUsername(false, username);
		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

		DateTime dateTime = new DateTime();
		PresentBusinessType presentBusinessType = PresentBusinessType.INVITE_DAY_NO_NEED_RECHARGE;
		String cachekey = InviteFriendStatus.getCacheKey(username, dateTime, null, null, presentBusinessType);
		InviteFriendStatus status = InviteFriendStatus.loadFromCache(cachekey);
		if(status == null)
		{
			status = InviteFriendStatus.mDefaultStatus;
		}
		else
		{
			long receiveCount = mTodayInviteFriendManagerV2.getPresentLevelCount(username);
			status.setReceiveCount(receiveCount);
		}
		apiJsonTemplate.setData(status);
		return apiJsonTemplate.toJSONString();
	}

	@MyLoginRequired
	@RequestMapping("receiveTodayInviteFriendTaskStatusV2Bonus")
	@ResponseBody
	public String receiveTodayInviteFriendTaskStatusV2Bonus()
	{
		String accessToken = WebRequest.getAccessToken();
		String username = mOauth2Service.getAccountByAccessToken(accessToken);


		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

		if(!RequestTokenHelper.verifyGame(username))
		{
			// 并发限制
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
			return apiJsonTemplate.toJSONString();
		}

		UserInfo userInfo = mUserService.findByUsername(false, username);
		long receivCount = mTodayInviteFriendManagerV2.receive(userInfo);
		apiJsonTemplate.setData(receivCount);
		return apiJsonTemplate.toJSONString();
	}

	/**
	 * @api {post} /passport/userApi/getUserMoneyOrderList
	 * @apiDescription  用户余额明细
	 * @apiName login
	 * @apiGroup passport-user-api
	 * @apiVersion 1.0.0
	 *
	 * @apiParam {String}  accessToken
	 *
	 * @apiSuccess  {String}  code    错误码
	 * @apiSuccess  {String}  msg   错误信息
	 *
	 * @apiSuccessExample {json} Success-Response:
	 *       {
	 *         "code": 200,
	 *         "msg": "success",
	 *       }
	 */
	@MyLoginRequired
	@RequestMapping("getUserMoneyOrderList")
	@ResponseBody
	public String getUserMoneyOrderList()
	{

		String accessToken = WebRequest.getAccessToken();
		String longUsername = mAuthService.getAccountByAccessToken(accessToken);


		String time = WebRequest.getString("time");

		//String orderTypeString = WebRequest.getString("type");
		String txStatusString = WebRequest.getString("txStatus");
		String systemOrderno = WebRequest.getString("systemOrderno");
		String outTradeno = WebRequest.getString("outTradeno");

		ApiJsonTemplate template = new ApiJsonTemplate();

		PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
		if(pageVo.getOffset()<=90){
			pageVo.setLimit(100);
		}

//		DateTime date=new DateTime();
//		DateTime startdate=date.plusWeeks(-5);
//
//		String fromTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startdate );
//		String toTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, date );
//
//		pageVo.setFromTime(fromTime);
//		pageVo.setToTime(toTime);

		pageVo.parseTime(time);


		//MoneyOrderType orderType = MoneyOrderType.getType(orderTypeString);

		OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

		long userid = mUserQueryManager.findUserid(longUsername);

		RowPager<MoneyOrder> rowPager = moneyOrderService.queryScrollPageByLongUsername(false,pageVo, userid, -1, -1, systemOrderno, outTradeno, null, MoneyOrderType.RETURN_WATER, txStatus);
		template.setData(rowPager);

		return template.toJSONString();
	}

	private boolean isRepeatRegister(String remoteip, String deviceToken)
	{
//		return false;
		DateTime toTime = DateTime.now();
		DateTime fromTime = toTime.minusDays(1);
		// 重复注册, 1天之内同一个IP注册超过10个， 设备id只要重复都算重复
		long registerIPCount = mUserService.countRegisterIp(false, fromTime, toTime, remoteip);
		if(registerIPCount > 0)
		{
			return true;
		}
		if(!StringUtils.isEmpty(deviceToken) && mUserService.countDeviceToken(false, fromTime, toTime, deviceToken) > 0)
		{
			return true;
		}
		return false;
	}

}

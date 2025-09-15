package com.inso.modules.passport.user.service;

import java.util.List;
import java.util.Map;

import com.inso.framework.utils.*;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.paychannel.helper.EmailPhoneHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserInfo.UserSex;
import com.inso.modules.passport.user.model.UserInfo.UserType;
import com.inso.modules.passport.user.service.dao.UserDao;

/**
 * 
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao mUserDao;

	@Autowired
	private UserMoneyService mUserMoneyService;

	@Autowired
	private UserAttrService mUserAttrService;

	@Autowired
	private UserSecretService mUserSecretService;

//	@Autowired
//	private ReturnWaterLogCountService mReturnWaterLogCountService;

	@Autowired
	private CoinAccountService mCoinAccountService;

	private CacheManager mCache = CacheManager.getInstance();

	private static final String DEF_FIND_EMAIL_VALUE = "1";

	@Transactional
	public long addUserByThirdCoin(String username, String password, String phone, String email, UserType userType, String registerpath, String registerip, RemarkVO remarkVO)
	{
		username = username.toLowerCase();
		String nickname = StringUtils.getEmpty();
		String inviteCode = null;
		if(UserInfo.DEFAULT_GAME_SYSTEM_AGENT.equalsIgnoreCase(username))
		{
			inviteCode = "sys001";
		}
		else
		{
			inviteCode = RandomStringUtils.generator0_Z(6);
		}

		if(UserInfo.DEFAULT_GAME_SYSTEM_STAFF.equalsIgnoreCase(username))
		{
			inviteCode = UserInfo.DEFAULT_GAME_SYSTEM_STAFF_INVITE_CODE;
		}



		long userid = mUserDao.addUser(username, phone, email, userType, nickname, inviteCode, registerpath, registerip,remarkVO);

		// money
		mUserMoneyService.initMoney(userid, username, FundAccountType.Spot, ICurrencyType.getSupportCurrency());

		// 初始化-安全信息
		mUserSecretService.initSecret(userid, username, password);

		// 初始化属性表
		mUserAttrService.initAttr(userid, username);

		// 反水日志
//		mReturnWaterLogCountService.addLog(userid, username);

		String cachekey = UserInfoCacheKeyUtils.getFingerCountForIP(registerip);
		CacheManager.getInstance().delete(cachekey);

		String emailCacheKey = UserInfoCacheKeyUtils.getUsernameByEmail(email);
		CacheManager.getInstance().delete(emailCacheKey);
		return userid;
	}

	@Transactional
	public long addUserByThirdCoin(String username, String address, CryptoNetworkType networkType, UserType userType, String registerip)
	{
		username = username.toLowerCase();
		String uuid = UUIDUtils.getUUID();
		String nickname = StringUtils.getEmpty();
		String inviteCode = MD5.encode(uuid + username).substring(0, 8);

		String email = EmailPhoneHelper.nextEmail();
		String phone = EmailPhoneHelper.nextPhone();

		String password = MD5.encode("123456");
		password = MD5.encode(password);

		long userid = mUserDao.addUser(username, phone, email, userType, nickname, inviteCode, null, registerip,null);

		mCoinAccountService.add(userid, username, address, networkType);

		// money
		mUserMoneyService.initMoney(userid, username, FundAccountType.Spot, ICurrencyType.getSupportCurrency());

		// 初始化-安全信息
		mUserSecretService.initSecret(userid, username, password);

		// 初始化属性表
		mUserAttrService.initAttr(userid, username);

		// 反水日志
//		mReturnWaterLogCountService.addLog(userid, username);

		return userid;
	}
	
	@Override
	public UserInfo findByUsername(boolean purge, String username)
	{
		if(StringUtils.isEmpty(username))
		{
			return null;
		}
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		UserInfo userInfo = mCache.getObject(cachekey, UserInfo.class);
		if(purge || userInfo == null)
		{
			userInfo = mUserDao.findByUsername(username);
			if(userInfo != null)
			{
				mCache.setString(cachekey, FastJsonHelper.jsonEncode(userInfo));
			}
		}
		return userInfo;
	}
	
	@Override
	public String findNameByInviteCode(String inviteCode) {
		String cachekey = UserInfoCacheKeyUtils.getUsernameByInviteCode(inviteCode);
		String value = CacheManager.getInstance().getString(cachekey);
		if(StringUtils.isEmpty(value))
		{
			value = mUserDao.findNameByInviteCode(inviteCode);
			if(!StringUtils.isEmpty(value))
			{
				CacheManager.getInstance().setString(cachekey, value, CacheManager.EXPIRES_WEEK);
			}
		}
		return value;
	}

	@Override
	public String findNameByEmail(String email)
	{
		String cachekey = UserInfoCacheKeyUtils.getUsernameByEmail(email);
		String value = CacheManager.getInstance().getString(cachekey);
		if(StringUtils.isEmpty(value))
		{
			value = mUserDao.findNameByEmail(email);
			if(StringUtils.isEmpty(value))
			{
				value = DEF_FIND_EMAIL_VALUE;
			}
			CacheManager.getInstance().setString(cachekey, value, CacheManager.EXPIRES_DAY);
		}

		if(value != null && DEF_FIND_EMAIL_VALUE.equalsIgnoreCase(value))
		{
			return null;
		}
		return value;
	}
	
	@Override
	public String findNameByPhone(String phone)
	{
		String cachekey = UserInfoCacheKeyUtils.getUsernameByPhone(phone);
		String value = CacheManager.getInstance().getString(cachekey);
		if(StringUtils.isEmpty(value))
		{
			value = mUserDao.findNameByPhone(phone);
			if(!StringUtils.isEmpty(value))
			{
				CacheManager.getInstance().setString(cachekey, value, CacheManager.EXPIRES_WEEK);
			}
		}
		return value;
	}

	@Override
	public long countRegisterIp(boolean purge, DateTime fromTime, DateTime toTime, String registerIp) {
		String cachekey = UserInfoCacheKeyUtils.getFingerCountForIP(registerIp);
		String value = CacheManager.getInstance().getString(cachekey);
		long count = StringUtils.asLong(value);
		if(purge || StringUtils.isEmpty(value))
		{
			count = mUserDao.countRegisterIp(fromTime, toTime, registerIp);
			CacheManager.getInstance().setString(cachekey, count + StringUtils.getEmpty());
		}
		return count;
	}

	@Override
	public long countDeviceToken(boolean purge, DateTime fromTime, DateTime toTime, String deviceToken) {
		String cachekey = UserInfoCacheKeyUtils.getFingerCountForDeviceToken(deviceToken);
		String value = CacheManager.getInstance().getString(cachekey);
		long count = StringUtils.asLong(value);
		if(purge || StringUtils.isEmpty(value))
		{
			count = mUserDao.countDeviceToken(fromTime, toTime, deviceToken);
			CacheManager.getInstance().setString(cachekey, count + StringUtils.getEmpty());
		}
		return count;
	}

//	@Override
//	@Transactional
//	public void updatePhone(String username, String phone)
//	{
//		mUserDao.updatePhone(username, phone);
//		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
//		 mCache.delete(cachekey);
//	}
	
	@Override
	@Transactional
	public void updateEmail(String username, String email)
	{
		 mUserDao.updateEmail(username, email);
		 String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		 mCache.delete(cachekey);
	}
	
	@Override
	@Transactional
	public void updateStatus(String username, String status, Status loginAgentStatus)
	{
		mUserDao.updateStatus(username, status, loginAgentStatus);
		 String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		 mCache.delete(cachekey);
	}
	
	@Override
	@Transactional
	public void updateSex(String username, UserSex sex)
	{
			mUserDao.updateSex(username, sex);
			String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
			mCache.delete(cachekey);
	}
	
	@Override
	@Transactional
	public void updateNickname(String username, String nickname)
	{
			mUserDao.updateNickname(username, nickname);
			String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
			mCache.delete(cachekey);
	}
	
	@Override
	@Transactional
	public void updateAvatar(String username, String avatar)
	{
		mUserDao.updateAvatar(username, avatar);
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		mCache.delete(cachekey);
	}
	
	@Override
	@Transactional
	public void updateUserType(String username, UserType userType)
	{
		mUserDao.updateUserType(username, userType);
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		mCache.delete(cachekey);
	}

	@Override
	@Transactional
	public void updateSubType(String username, MemberSubType subType)
	{
		mUserDao.updateSubType(username, subType);
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		mCache.delete(cachekey);
	}

	@Override
	public void updateLastLoginIP(String username, String ip) {
		mUserDao.updateLastLoginIP(username, ip);
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		mCache.delete(cachekey);
	}

	@Override
	public void queryAll(String startTime, String endTime, Callback<UserInfo> callback) {
		mUserDao.queryAll(startTime, endTime, callback);
	}

	@Override
	public void statsCountByUserType(Callback<Map<String, Object>> callback) {
		mUserDao.statsCountByUserType(callback);
	}

	@Override
	public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, UserType userType,String username,long agentid,long staffid, String inviteCode)
	{
		return mUserDao.queryScrollPage(pageVo, userid, userType,username, agentid, staffid, inviteCode);
	}

	@Override
	public List<UserInfo> userListbyUserType(boolean purge,UserType userType) {

		if(StringUtils.isEmpty(userType.getKey()))
		{
			return null;
		}
		String cachekey = UserInfoCacheKeyUtils.createUserInfoListKey(userType);
		List<UserInfo> userList = mCache.getList(cachekey, UserInfo.class);
		if(purge || userList==null ||userList.size()<1)
		{
			userList = mUserDao.userListbyUserType (userType);
			if(userList != null)
			{
				mCache.setString(cachekey, FastJsonHelper.jsonEncode(userList),CacheManager.EXPIRES_DAY);
			}
		}
		return userList;
	}

	@Override
	public void deleteUserInfoCache(String username)
	{
		String cachekey = UserInfoCacheKeyUtils.createUserInfoKey(username);
		 mCache.delete(cachekey);
	}
	

}

package com.inso.modules.passport.user.service;

import java.util.List;
import java.util.Map;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserInfo.UserSex;
import com.inso.modules.passport.user.model.UserInfo.UserType;
import org.joda.time.DateTime;

public interface UserService {
	

	public long addUserByThirdCoin(String username, String password, String phone, String email, UserType userType, String registerpath, String registerip, RemarkVO remarkVO);

	public long addUserByThirdCoin(String username, String address, CryptoNetworkType networkType, UserType userType, String registerip);
//	public void addUserByName(String username, String password, String nickname, String registerip, UserType userType);
//	public void addUserByPhone(String phone, String password, String nickname, String registerip, UserType userType);
//	public void addUserByEmail(String email, String password, String nickname, String registerip, UserType userType);
	
	public UserInfo findByUsername(boolean purge, String username);

	public String findNameByInviteCode(String inviteCode);
	public String findNameByEmail(String email);
	public String findNameByPhone(String phone);

	public long countRegisterIp(boolean purge, DateTime fromTime, DateTime toTime, String registerIp);
	public long countDeviceToken(boolean purge, DateTime fromTime, DateTime toTime, String deviceToken);
	
//	public void updatePhone(String username, String phone);
	public void updateEmail(String username, String email);

	public void updateStatus(String username, String status, Status loginAgentStatus);
	public void updateSex(String username, UserSex sex);
	public void updateNickname(String username, String nickname);
	public void updateAvatar(String username, String avatar);

	public void updateUserType(String username, UserType userType);
	public void updateSubType(String username, MemberSubType subType);

	public void updateLastLoginIP(String username, String ip);

	public void queryAll(String startTime, String endTime, Callback<UserInfo> callback);
	public void statsCountByUserType(Callback<Map<String, Object>> callback);

	public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, UserType userType,String username,long agentid,long staffid, String inviteCode);

	public List<UserInfo> userListbyUserType(boolean purge,UserType userType);

	public void deleteUserInfoCache(String username);
	
}

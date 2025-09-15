package com.inso.modules.passport.user.service.dao;

import java.util.List;
import java.util.Map;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserInfo.UserType;
import org.joda.time.DateTime;


public interface UserDao {

	public long addUser(String username, String phone, String email, UserType userType, String nickname, String inviteCode, String registerpath, String registerip, RemarkVO remarkVO);
	
	/**
	 * 查询相关
	 * @param username
	 * @return
	 */
	public UserInfo findByUsername(String username);

	public String findNameByInviteCode(String inviteCode);
	public String findNameByEmail(String email);
	public String findNameByPhone(String phone);


	public long countRegisterIp(DateTime fromTime, DateTime toTime, String registerIp);
	public long countDeviceToken(DateTime fromTime, DateTime toTime, String deviceToken);

	/**
	 * 更新用户状态
	 * @param username
	 * @param status
	 */
	public void updateStatus(String username, String status, Status loginAgentStatus);
	public void updateSex(String username, UserInfo.UserSex sex);
	public void updateEmail(String username, String email);
	public void updatePhone(String username, String phone);
	public void updateNickname(String username, String nickname);
	public void updateLastLoginIP(String username, String ip);
	public void updateAvatar(String username, String avatar);
	public void updateUserType(String username, UserType userType);
	public void updateSubType(String username, MemberSubType subType);

	public void queryAll(String startTime, String endTime, Callback<UserInfo> callback);
	public void statsCountByUserType(Callback<Map<String, Object>> callback);

	public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, UserType userType ,String username,long agentid,long staffid, String inviteCode);

	public List<UserInfo> userListbyUserType(UserType userType);

}

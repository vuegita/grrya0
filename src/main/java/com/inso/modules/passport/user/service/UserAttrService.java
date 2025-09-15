package com.inso.modules.passport.user.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserLevel;
import org.joda.time.DateTime;


public interface UserAttrService {

    public void initAttr(long userid, String username);

    public void updateStaffAndAgent(long userid, String staffName, long staffid, String agentName, long AgentId);
    public void updateStaffAndAgent(Map<String, Long> maps, String staffName, long staffid, String agentName, long agentId);

    /**
     * 绑定祖先关系
     * @param userid
     * @param staffName
     * @param staffid
     * @param parentName
     * @param parentid
     * @param grantFatherName
     * @param grantFatherid
     */
    public void bindAncestorInfo(long userid, String username, long staffid, String staffName, long parentid, String parentName, long grantFatherid, String grantFatherName, String agentName, long agentId);
    public void updateLevelAndRemark(long userid, UserLevel level, String remark);
    public void updateReturn(long userid, BigDecimal returnLv1Rate, BigDecimal returnLv2Rate, Status returnLevelStatus, BigDecimal receivLv1Rate, BigDecimal receivLv2Rate);

    public void updateFirstRechargeOrderno(long userid, String orderno, BigDecimal amount);
    public void updateInviteFriendTotalAmount(long userid, BigDecimal amount);

    public UserAttr find(boolean purge, long userid);

    public void queryAllMember2(Date startTime, Date endTime, Callback<UserAttr> callback);
    public void queryAllMemberByUserReport(DateTime dateTime, Callback<UserAttr> callback);

    public RowPager<UserAttr> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid);

    public UserAttr queryTotalRechargeAndwithdrawById(long id);
    public UserAttr queryTotalRechargeByParentid(boolean purge,long parentid);

    public RowPager<UserAttr> queryScrollPageOrderBy(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName, String sortOrder, String userName, Status status);

    public RowPager<UserAttr> queryScrollPageByParentidAndGrantid(boolean purge, PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName,String sortOrder);
    /**
     * 根据上级员工id，查询其所有会员下级
     * @param pageVo
     * @param staffid
     * @return
     */
    public RowPager<UserInfo> querySubMemberPageScrollWithStaffid(PageVo pageVo, long staffid);

    /**
     * 根据会员id，查询其对应所有的1|2级下级会员
     * @param callback
     * @param userid
     */
    public void queryAllSubMemberWithMemberid(Callback<UserAttr> callback, long userid);
}

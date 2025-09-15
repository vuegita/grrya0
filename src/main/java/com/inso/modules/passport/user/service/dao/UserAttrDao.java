package com.inso.modules.passport.user.service.dao;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserLevel;
import org.joda.time.DateTime;


public interface UserAttrDao {

    public void addAttr(long userid, String username, String directStaffName, long directStaffid);
    public void updateStaff(long userid, String staffName, long staffid, String agentName, long agentId);

    public void bindAncestorInfo(long userid, String staffName, long staffid, String parentName, long parentid, String grantFather, long grantFatherid, String agentName, long agentId);
    public void updateLevelAndRemark(long userid, UserLevel level, String remark);
    public void updateReturn(long userid, BigDecimal returnLv1Rate, BigDecimal returnLv2Rate, Status returnLevelStatus, BigDecimal receivLv1Rate, BigDecimal receivLv2Rate);

    public void updateFirstRechargeOrderno(long userid, String orderno, BigDecimal amount);
    public void updateInviteFriendTotalAmount(long userid, BigDecimal amount);


    public UserAttr find(long userid);

    public void queryAllMember(Date startTime, Date endTime, Callback<UserAttr> callback);
    public void queryAllMemberByUserReport(DateTime startTime, DateTime endTime, Callback<UserAttr> callback);

    public RowPager<UserAttr> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid);

    public UserAttr queryTotalRechargeAndwithdrawById(long id);

    public UserAttr queryTotalRechargeByParentid(long id);

    public RowPager<UserAttr> queryScrollPageOrderBy(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName, String sortOrder, String userName, Status status);
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

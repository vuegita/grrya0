package com.inso.modules.report.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.MemberReport;
import org.joda.time.DateTime;

public interface UserReportService {

    public void updateReport(Date pdate, MemberReport report);
    public void delete(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate);

    public void updateReport(FundAccountType accountType, ICurrencyType currencyType, MoneyOrderType orderType, long userid, String username, Date pdate, BigDecimal amount, BigDecimal feemoney);

    public void updateSubLevel(FundAccountType accountType, ICurrencyType currencyType, long userid, String username, Date pdate, UserLevelStatusInfo data);

    public MemberReport findAllHistoryReportByUserid(long userid);

    public MemberReport queryHistoryReportByUser(boolean purge, DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType);

    public List<MemberReport> queryByUserAndTime(boolean purge, long userid);

    public List<MemberReport> queryAgentDataListByWebApi(boolean purge, long userid, UserInfo.UserType userType);

    /**
     * 根据时间查询所有
     * @param startTime
     * @param endTime
     * @param callback
     */
    public void queryAllMemberReport(String startTime, String endTime, Callback<MemberReport> callback);

    public void queryAllMemberReportByUserId(String startTime, String endTime,long agentid, long staffid, Callback<MemberReport> callback);

    public RowPager<MemberReport> queryScrollPage(PageVo pageVo, long userid, UserInfo.UserType userType);

    public RowPager<MemberReport> queryScrollPageBySuperiorId(PageVo pageVo, long userid,  long parentid, long grantid);

    public RowPager<MemberReport> queryScrollPageByParentidOrgrantid(boolean purge, PageVo pageVo, long userid,  long parentid, long grantid);
    /**
     * 查询代理所有数据
     * @param pageVo
     * @param ancestorid
     * @param userTypes
     * @return
     */
    public RowPager<MemberReport> queryAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes);

    /**
     * 根据祖先id查询所有用户类型为agent和stagff下级
     * @param pageVo
     * @param ancestorid
     * @param userTypes
     * @return
     */
    public RowPager<MemberReport> querySubAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes);
}

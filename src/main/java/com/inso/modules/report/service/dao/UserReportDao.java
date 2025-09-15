package com.inso.modules.report.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.MemberReport;
import org.joda.time.DateTime;

public interface UserReportDao {

    public void addReport(FundAccountType accountType, ICurrencyType currencyType, long userid, String username, Date pdate);
    public void delete(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate);


    public void updateReport(Date pdate, MemberReport report);

    public void updateRefund(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney);

    public void updateUserRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);
    public void updateUserWithdraw(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney);

    public void updatePlatformRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);
    public void updatePlatformPresentation(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);
    public void updatePlatformDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);

    public void updateBusinessRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);
    public void updateBusinessDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney);

    public void updateReturnWater(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);

    public void updateFinanceRecharge(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money);
    public void updateFinanceDeduct(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, BigDecimal money, BigDecimal feemoney);

    public void updateSubLevel(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate, UserLevelStatusInfo data);

    public MemberReport findAllHistoryReportByUserid(long userid);

    public MemberReport queryHistoryReportByUser(DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType);

    public List<MemberReport> queryByUserAndTime(DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType);

    /**
     * 根据时间查询所有
     * @param startTime
     * @param endTime
     * @param callback
     */
    public void queryAllMemberReport(String startTime, String endTime, Callback<MemberReport> callback);

    public void queryAllMemberReportByUserId(String startTime, String endTime,long agentid, long staffid, Callback<MemberReport> callback);

    public List<MemberReport> queryAgentDataListByWebApi(DateTime fromTime, DateTime toTime, long userid, UserInfo.UserType userType, int limit);

    public RowPager<MemberReport> queryScrollPage(PageVo pageVo, long userid, UserInfo.UserType userType);

    public RowPager<MemberReport> queryScrollPageBySuperiorId(PageVo pageVo, long userid,  long parentid, long grantid);

    public RowPager<MemberReport> queryAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes);

    /**
     * 查询下级
     * @param pageVo
     * @param ancestorid
     * @param userTypes
     * @return
     */
    public RowPager<MemberReport> querySubAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes);

}

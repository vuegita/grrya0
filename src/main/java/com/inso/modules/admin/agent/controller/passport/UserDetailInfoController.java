package com.inso.modules.admin.agent.controller.passport;

import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.invite_stats.InviteStatsManager;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 用户历史所有信息详细
 */
@Controller
@RequestMapping("/alibaba888/agent")
public class UserDetailInfoController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @Autowired
    private ReturnRecordManager mReturnRecordManager;

    @Autowired
    private InviteStatsManager mInviteStatsManager;

//    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("/passport/user/history_detail/page")
    public String toPage(Model model)
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            return "agent/passport/err";
        }


        String username = WebRequest.getString("username");

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();


        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo==null){
            return "agent/passport/err";
        }

        if(AgentAccountHelper.isAgentLogin() && AgentAccountHelper.getAdminLoginInfo().getId() == userInfo.getId())
        {

        }
        else if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
            return "agent/passport/err";
        }

        DateTime nowTime = DateTime.now();
        UserStatusV2Day todayUserStatusV2Day = mReturnRecordManager.getDataInfo(username, nowTime.getDayOfYear());
        if(todayUserStatusV2Day == null)
        {
            todayUserStatusV2Day = new UserStatusV2Day();
        }

        UserMoney moneyInfo = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        ReturnWaterLog returnWaterLog = mReturnWaterLogService.findByUserid(false, userInfo.getId(), username, currencyType);

        MemberReport memberReport = mUserReportService.findAllHistoryReportByUserid(userInfo.getId());
        if(memberReport == null)
        {
            memberReport = new MemberReport();
            memberReport.init();
        }

        List<GameInfo> gameList = mGameService.queryAllByCategory(false, GameCategory.LOTTERY_RG);

        model.addAttribute("orderTypeList", MoneyOrderType.values());
        model.addAttribute("username", username);
        model.addAttribute("gameList", gameList);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("moneyInfo", moneyInfo);
        model.addAttribute("userAttr", userAttr);
        model.addAttribute("report", memberReport);
        model.addAttribute("returnWaterLog", returnWaterLog);
        model.addAttribute("userStatsDetailV2Info", todayUserStatusV2Day);

        // 今日总邀请信息
        model.addAttribute("userTodayInviteStatsInfo", mInviteStatsManager.loadTodayStatsInfo(userInfo));

        SystemRunningMode mode = SystemRunningMode.getSystemConfig();
        if(mode == SystemRunningMode.FUNDS)
        {
            //UserVIPInfo UserVIPInfo= mUserVIPService.findByUserId(false,userInfo.getId(), VIPType.AD);
            //model.addAttribute("UserVIPInfo", UserVIPInfo);
        }
        boolean isCrypto= !(mode == SystemRunningMode.CRYPTO);
        model.addAttribute("isCrypto", isCrypto);

        return "admin/agent/passport/detail/user_history_detail_page";
    }
}

package com.inso.modules.admin.controller.passport;

import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.MemberSubLevelManager;
import com.inso.modules.passport.invite_stats.InviteStatsManager;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.service.GameService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;

/**
 * 用户历史所有信息详细
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
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
    private MemberSubLevelManager mStatsTopLevelManager;

    @Autowired
    private ReturnRecordManager mReturnRecordManager;

    @Autowired
    private InviteStatsManager mInviteStatsManager;

    //@RequiresPermissions("root_passport_member_edit")
    @RequestMapping("/passport/user/history_detail/page")
    public String toPage(Model model)
    {
        String username = WebRequest.getString("username");

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        List<GameInfo> gameList = mGameService.queryAllByCategory(false, GameCategory.LOTTERY_RG);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        UserMoney moneyInfo = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        ReturnWaterLog returnWaterLog = mReturnWaterLogService.findByUserid(false, userInfo.getId(), username, currencyType);

        MemberReport memberReport = mUserReportService.findAllHistoryReportByUserid(userInfo.getId());
        if(memberReport == null)
        {
            memberReport = new MemberReport();
            memberReport.init();
        }

        DateTime nowTime = DateTime.now();
        UserStatusV2Day todayUserStatusV2Day = mReturnRecordManager.getDataInfo(username, nowTime.getDayOfYear());
        if(todayUserStatusV2Day == null)
        {
            todayUserStatusV2Day = new UserStatusV2Day();
        }

        // today
        todayUserStatusV2Day.loadTradeData(nowTime, username);
        todayUserStatusV2Day.initNotEmpty();
        todayUserStatusV2Day.setUserid(0);
        todayUserStatusV2Day.setUsername(username);

        model.addAttribute("orderTypeList", MoneyOrderType.values());
        model.addAttribute("username", username);
        model.addAttribute("gameList", gameList);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("moneyInfo", moneyInfo);
        model.addAttribute("userAttr", userAttr);
        model.addAttribute("report", memberReport);
        model.addAttribute("userStatsDetailV2Info", todayUserStatusV2Day);

        // 今日总邀请信息
        model.addAttribute("userTodayInviteStatsInfo", mInviteStatsManager.loadTodayStatsInfo(userInfo));

        model.addAttribute("todaySubLevelStatsInfo", mStatsTopLevelManager.getTodayDataFromCache(username));

        model.addAttribute("returnWaterLog", returnWaterLog);
        SystemRunningMode mode = SystemRunningMode.getSystemConfig();
        if(mode == SystemRunningMode.FUNDS)
        {
            //UserVIPInfo UserVIPInfo= mUserVIPService.findByUserId(false,userInfo.getId(), VIPType.AD);
            //model.addAttribute("UserVIPInfo", UserVIPInfo);
        }
        boolean isCrypto= true;// !(mode == SystemRunningMode.CRYPTO);
        model.addAttribute("isCrypto", isCrypto);

        return "admin/passport/detail/user_history_detail_page";
    }
}

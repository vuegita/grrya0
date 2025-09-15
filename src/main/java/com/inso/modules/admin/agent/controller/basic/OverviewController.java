package com.inso.modules.admin.agent.controller.basic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.analysis.service.UserActiveStatsService;
import com.inso.modules.common.model.*;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.ActiveUserManager;
import org.apache.commons.compress.utils.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.AgentOverviewManager;
import com.inso.modules.common.PlatformOverviewManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.report.model.GameBusinessDay;

/**
 * 基本信息
 */
@Controller
@RequestMapping("/alibaba888/agent/basic/overview")
public class OverviewController {

    @Autowired
    private PlatformOverviewManager mTotalStatsManager;

    @Autowired
    private AgentOverviewManager mAgentOverviewManager;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private UserActiveStatsService mUserActiveStatsService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequestMapping("getSystemConfig")
    @ResponseBody
    public String getSystemConfig()
    {
        String inviteCode = WebRequest.getString("inviteCode");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(inviteCode == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        SystemRunningMode modl= SystemRunningMode.getSystemConfig();
        Map<String, Object> map = new HashMap<>();
        if(modl==SystemRunningMode.CRYPTO){
            map.put("shareUrl", "/mining/defi?inviteCode="+inviteCode);
        }else if(modl==SystemRunningMode.BC){
//            map.put("shareUrl", "/mining/#/register?inviteCode="+inviteCode);
            map.put("shareUrl", "/#/register?inviteCode="+inviteCode);
//            map.put("shareUrl2", "/#/register?inviteCode="+inviteCode);
            map.put("shareUrl2", "/passport/userApi/register2/"+inviteCode);
        }else if(modl==SystemRunningMode.FUNDS){
            map.put("shareUrl", "/#/invite?ref="+inviteCode);
        }

        apiJsonTemplate.setData(map);

        return apiJsonTemplate.toJSONString();

    }

    @RequestMapping("page")
    public String toPage(Model model)
    {
        // 我的推广链接、
        // 1. 当月汇总
        // 总充值 、 总提现、 投注总额、 中奖总额

        // 2. 今日汇总
        // 总充值 、 总提现、 投注总额、 中奖总额

        // 3. 用户汇总 - 放在用户列表里的详情

        long agentid = AgentAccountHelper.getAdminAgentid();
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = moneyService.findMoney(false, agentid, accountType, currencyType);
        model.addAttribute("userMoney", userMoney);


        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();
        JSONObject jsonObject = mTotalStatsManager.getAgentStatsInfoCache(OverviewType.AGENT_SUB_COUNT, userInfo.getName(), JSONObject.class);
        if(jsonObject == null)
        {
            jsonObject = new JSONObject();
        }
        model.addAttribute("todayUserStatsInfo", jsonObject);
        model.addAttribute("isAgent", AgentAccountHelper.isAgentLogin());


        DateTime dateTime = new DateTime();
        int dayOfYear = dateTime.getDayOfYear();


        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();

        GameCategory[] mArr = {
                GameCategory.BTC_KLINE, GameCategory.TURNTABLE,
                GameCategory.ROCKET, GameCategory.FOOTABALL, GameCategory.Mines, GameCategory.PG,
        };

        List gameInfoList = Lists.newArrayList();
        for(GameCategory category : mArr)
        {
            Map<String, Object> maps = Maps.newHashMap();
            GameBusinessDay businessDay = mAgentOverviewManager.getAgentBusinessDay(dayOfYear, currentLoginInfo.getId(), category);
            maps.put("businessDay", businessDay);
            BigDecimal totalProfit= businessDay.getBetAmount().subtract(businessDay.getWinAmount());
            maps.put("totalProfit", totalProfit);
            maps.put("title", category.getName());
            gameInfoList.add(maps);
        }

        model.addAttribute("gameInfoList", gameInfoList);

//        GameBusinessDay btcKlineBusinessDay = mAgentOverviewManager.getAgentBusinessDay(dayOfYear, currentLoginInfo.getId(), GameCategory.BTC_KLINE);
//        model.addAttribute("btcKlineBusinessDay", btcKlineBusinessDay);
//        BigDecimal btcKlineTotalProfit= btcKlineBusinessDay.getBetAmount().subtract(btcKlineBusinessDay.getWinAmount());
//        model.addAttribute("btcKlineTotalProfit", btcKlineTotalProfit);

//        GameBusinessDay lotteryBusinessDay = mAgentOverviewManager.getAgentBusinessDay(dayOfYear, currentLoginInfo.getId(), GameCategory.LOTTERY_RG);
//        model.addAttribute("lotteryBusinessDay", lotteryBusinessDay);
//        BigDecimal rgTotalProfit= lotteryBusinessDay.getBetAmount().subtract(lotteryBusinessDay.getWinAmount());
//        model.addAttribute("rgTotalProfit", rgTotalProfit);
//
//        GameBusinessDay abBusinessDay = mAgentOverviewManager.getAgentBusinessDay(dayOfYear, currentLoginInfo.getId(), GameCategory.ANDAR_BAHAR);
//        model.addAttribute("abBusinessDay", abBusinessDay);
//        BigDecimal abTotalProfit= abBusinessDay.getBetAmount().subtract(abBusinessDay.getWinAmount());
//        model.addAttribute("abTotalProfit", abTotalProfit);
//
//        GameBusinessDay fruitBusinessDay = mAgentOverviewManager.getAgentBusinessDay(dayOfYear, currentLoginInfo.getId(), GameCategory.FRUIT);
//        model.addAttribute("fruitBusinessDay", fruitBusinessDay);
//        BigDecimal fruitTotalProfit= fruitBusinessDay.getBetAmount().subtract(fruitBusinessDay.getWinAmount());
//        model.addAttribute("fruitTotalProfit", fruitTotalProfit);



        model.addAttribute("inviteCode", currentLoginInfo.getInviteCode());
        boolean isAgentLogin=AgentAccountHelper.isAgentLogin();
        if(isAgentLogin){
            model.addAttribute("isAgentLogin", 1);
        }

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
//        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
//        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
//        {
//
//        }
            //当前活跃人数
            model.addAttribute("activeUserCount", ActiveUserManager.getAgentOrStaffCount(currentLoginInfo.getName()));
            //今日活跃人数
            model.addAttribute("todayActiveUserCount", ActiveUserManager.getAgentOrStaffTodaycount(currentLoginInfo.getName()));



        SystemRunningMode modl= SystemRunningMode.getSystemConfig();


        model.addAttribute("isShowAgentWallet", AgentAccountHelper.isAgentLogin() + StringUtils.getEmpty());

        if(modl==SystemRunningMode.CRYPTO){
            model.addAttribute("isShowContent", false+"");
        }else if(modl==SystemRunningMode.BC){
            model.addAttribute("isShowContent", true+"");
        }else if(modl==SystemRunningMode.FUNDS){
            model.addAttribute("isShowContent", false+"");
        }

        if(modl==SystemRunningMode.CRYPTO){
            model.addAttribute("isRunningMode","crypto");
        }else if(modl==SystemRunningMode.BC){
            model.addAttribute("isRunningMode", "bc");
        }else if(modl==SystemRunningMode.FUNDS){
            model.addAttribute("isRunningMode", "funds");
        }

        return "admin/agent/basic/basic_overview_page";
    }

    @RequestMapping("getTotalBalanceByStaffid")
    @ResponseBody
    public String getTotalBalanceByStaffid()
    {
        String username = WebRequest.getString("username");

        ApiJsonTemplate template = new ApiJsonTemplate();
        JSONObject jsonObject = mTotalStatsManager.getAgentStatsInfoCache(OverviewType.AGENT_SUB_COUNT, username, JSONObject.class);
        if(jsonObject == null)
        {
            jsonObject = new JSONObject();
        }
        //model.addAttribute("todayUserStatsInfo", jsonObject);

        template.setData(jsonObject);

        return template.toJSONString();
    }


    @RequestMapping("dataAnalysis")
    public String toDataAnalysisPage(Model model)
    {

        return "admin/agent/analysis/user_active_stats_day";

    }


    @RequestMapping("getDataAnalysisUserActiveStatsDayList")
    @ResponseBody
    public String getDataAnalysisUserActiveStatsDayList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
       // String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String fetchFromString = WebRequest.getString("fetchFrom");
        UserInfo.UserType userType = UserInfo.UserType.getType(fetchFromString);

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }



        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


        long userid = mUserQueryManager.findUserid(username);
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();

            if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(userType.getKey())){
                userid=staffid;
            }

        }

        //long userid = mUserQueryManager.findUserid(username);
        //long agentid = mUserQueryManager.findUserid(agentname);
        //long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<UserActiveStatsInfo> rowPager = mUserActiveStatsService.queryScrollPage(pageVo, userType, agentid, staffid, userid);
        template.setData(rowPager);

        return template.toJSONString();
    }


}

package com.inso.modules.passport.controller.agent;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.analysis.service.UserActiveStatsService;
import com.inso.modules.common.AgentOverviewManager;
import com.inso.modules.common.PlatformOverviewManager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OverviewType;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.report.model.GameBusinessDay;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.ActiveUserManager;
import org.apache.commons.compress.utils.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping("/passport/agentApi")
@RestController
public class AgentApi {

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
    @Autowired
    private AuthService mOauth2Service;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserReportService mUserReportService;

    private FundAccountType accountType = FundAccountType.Spot;
    private ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

    private boolean enableCustomPermission = false;
    private boolean enableSupplyAdd;
    private boolean enableSupplyDeduct;

    public AgentApi()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String value = StringUtils.getNotEmpty(conf.getString("system.agent.suplly_mode"));

        this.enableCustomPermission = !StringUtils.isEmpty(value);

        if(enableCustomPermission)
        {
            this.enableSupplyAdd = value.contains("platform_recharge");
            this.enableSupplyDeduct = value.contains("deduct");
        }
    }

    /**
     * 获取今日相关统计-和后台一样
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getOverview")
    public String getOverview()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        JSONObject rootJsonObject = new JSONObject();

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        checkUserType(userType);

        if(userType == UserInfo.UserType.AGENT)
        {
            UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            rootJsonObject.put("agentMoneyInfo", userMoney);
        }

        JSONObject jsonObject = mTotalStatsManager.getAgentStatsInfoCache(OverviewType.AGENT_SUB_COUNT, userInfo.getName(), JSONObject.class);
        if(jsonObject == null)
        {
            jsonObject = new JSONObject();
        }
        rootJsonObject.put("todayUserStatsInfo", jsonObject);

        //当前活跃人数
        rootJsonObject.put("activeUserCount", ActiveUserManager.getAgentOrStaffCount(username));
        //今日活跃人数
        rootJsonObject.put("todayActiveUserCount", ActiveUserManager.getAgentOrStaffTodaycount(username));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setData(rootJsonObject);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 获取会员相关信息
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getUserInfo")
    public String getUserInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        checkUserType(userType);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String queryUsername = WebRequest.getString("queryUsername");
        if(StringUtils.isEmpty(queryUsername))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(queryUsername.equalsIgnoreCase(userInfo.getName()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        //
        UserInfo queryUserInfo = mUserQueryManager.findUserInfo(queryUsername);
        if(queryUserInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType queryUserType = UserInfo.UserType.getType(queryUserInfo.getType());
        if(!(queryUserType == UserInfo.UserType.STAFF || queryUserType == UserInfo.UserType.MEMBER || queryUserType == UserInfo.UserType.TEST))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        //
        UserAttr queryUserAttr = mUserQueryManager.findUserAttr(queryUserInfo.getId());
        if(userType == UserInfo.UserType.AGENT)
        {
            if(queryUserAttr.getAgentid() != userInfo.getId())
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            if(queryUserAttr.getDirectStaffid() != userInfo.getId())
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }
        }

        UserMoney queryUserMoney = moneyService.findMoney(false, queryUserInfo.getId(), accountType, currencyType);

        Map<String, Object> dataMaps = Maps.newHashMap();
        dataMaps.put("staffname", queryUserAttr.getDirectStaffname());
        dataMaps.put("username", queryUserInfo.getName());
        dataMaps.put("phone", queryUserInfo.getPhone());
        dataMaps.put("email", queryUserInfo.getEmail());
//        dataMaps.put("avatar", queryUserInfo.getShowAvatar());
        dataMaps.put("inviteCode", queryUserInfo.getInviteCode());
        dataMaps.put("balance", queryUserMoney.getValidBalance());
        dataMaps.put("freeze", queryUserInfo.getFreeze());

        // 冷钱包
        dataMaps.put("coldAmount", queryUserMoney.getColdAmount());
        dataMaps.put("validWithdrawBalance", queryUserMoney.getValidWithdrawBalance());
        dataMaps.put("createtime", queryUserInfo.getCreatetime());
        dataMaps.put("subType", queryUserInfo.getSubType());
        dataMaps.put("userType", queryUserInfo.getType());
        apiJsonTemplate.setData(dataMaps);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * 代理补单功能
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/supply")
    public String supply()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        UserInfo agentUserInfo = mUserQueryManager.findUserInfo(username);

        UserInfo.UserType agentUserType = UserInfo.UserType.getType(agentUserInfo.getType());
        checkUserType(agentUserType);


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        BigDecimal amount = WebRequest.getBigDecimal("amount");

        String queryUsername = WebRequest.getString("queryUsername");
        if(StringUtils.isEmpty(queryUsername))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(queryUsername.equalsIgnoreCase(agentUserInfo.getName()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        //
        UserInfo queryUserInfo = mUserQueryManager.findUserInfo(queryUsername);
        if(queryUserInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType queryUserType = UserInfo.UserType.getType(queryUserInfo.getType());
        if( queryUserType != UserInfo.UserType.MEMBER)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        //
        UserAttr queryUserAttr = mUserQueryManager.findUserAttr(queryUserInfo.getId());
        if(agentUserType != UserInfo.UserType.AGENT)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(queryUserAttr.getAgentid() != agentUserInfo.getId())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        String remark = StringUtils.getEmpty();
        ErrorResult errorResult = mPlatformPayManager.addRechargeByAgentAndDeductAgentBalanceFrom(accountType, currencyType, agentUserInfo, queryUserInfo, amount, agentUserType.getName(), remark);

        apiJsonTemplate.setJsonResult(errorResult);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 补单记录
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getSupplyList")
    public String getSupplyList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        UserInfo agentInfo = mUserQueryManager.findUserInfo(username);

        UserInfo.UserType agentUserType = UserInfo.UserType.getType(agentInfo.getType());
        checkUserType(agentUserType);

        int offset = WebRequest.getInt("offset");
        if(offset <= 0)
        {
            offset = 0;
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(offset > 300)
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        String queryUsername = WebRequest.getString("queryUsername");
        long userid = -1;
        if(!StringUtils.isEmpty(queryUsername) && ValidatorUtils.checkUsername(queryUsername))
        {
            UserInfo queryUserInfo = mUserQueryManager.findUserInfo(queryUsername);
            UserInfo.UserType queryUserType = UserInfo.UserType.getType(queryUserInfo.getType());
            if( queryUserType != UserInfo.UserType.MEMBER)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }

            userid = mUserQueryManager.findUserid(queryUsername);
        }

        //
        List<BusinessOrder> rsList = mBusinessOrderService.queryByAgent(MyEnvironment.isDev(), agentInfo.getId(), agentUserType, userid, offset);
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        List myList = new ArrayList();

        String ordernoKey = "orderno";
        String txStatusKey = "txStatus";
        String amountKey = "amount";
        String usernameKey = "username";
        String staffnameKey = "staffname";

        for(BusinessOrder tmpOrderInfo : rsList)
        {
            Map<String, Object> model = Maps.newHashMap();
            model.put(ordernoKey, tmpOrderInfo.getNo());
            model.put(txStatusKey, tmpOrderInfo.getStatus());
            model.put(amountKey, tmpOrderInfo.getAmount());
            model.put(usernameKey, tmpOrderInfo.getUsername());
            model.put(staffnameKey, tmpOrderInfo.getStaffname());

            myList.add(model);
        }

        apiJsonTemplate.setData(myList);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 报表记录
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getReportList")
    public String getReportList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        UserInfo agentInfo = mUserQueryManager.findUserInfo(username);

        UserInfo.UserType agentUserType = UserInfo.UserType.getType(agentInfo.getType());
        checkUserType(agentUserType);

        int offset = WebRequest.getInt("offset");
        if(offset <= 0)
        {
            offset = 0;
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(offset >= 100)
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        //
        List<MemberReport> rsList = mUserReportService.queryAgentDataListByWebApi(MyEnvironment.isDev(), agentInfo.getId(), agentUserType);
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        List myList = new ArrayList();

        String recharge = "recharge";
        String withdraw = "withdraw";
        String refund = "refund";
        String feemoney = "feemoney";

        String businessRecharge = "businessRecharge";
        String businessDeduct = "businessDeduct";
        String businessFeemoney = "businessFeemoney";

        String currencyType = "currencyType";
        String pdate = "pdate";

        for(MemberReport tmpOrderInfo : rsList)
        {
            Map<String, Object> model = Maps.newHashMap();
            model.put(recharge, tmpOrderInfo.getRecharge());
            model.put(withdraw, tmpOrderInfo.getWithdraw());
            model.put(refund, tmpOrderInfo.getRefund());
            model.put(feemoney, tmpOrderInfo.getFeemoney());

            model.put(businessRecharge, tmpOrderInfo.getBusinessRecharge());
            model.put(businessDeduct, tmpOrderInfo.getBusinessDeduct());
            model.put(businessFeemoney, tmpOrderInfo.getBusinessFeemoney());

            model.put(currencyType, tmpOrderInfo.getCurrency());
            model.put(pdate, DateUtils.convertString(tmpOrderInfo.getPdate(), DateUtils.TYPE_YYYY_MM_DD));

            myList.add(model);
        }

        apiJsonTemplate.setData(myList);
        return apiJsonTemplate.toJSONString();
    }

    private void checkUserType(UserInfo.UserType userType)
    {
        if(!(userType == UserInfo.UserType.STAFF || userType == UserInfo.UserType.AGENT))
        {
            throw new RuntimeException("Not Found!");
        }
        if(!enableCustomPermission || !enableSupplyAdd)
        {
            throw new RuntimeException("Not Found!");
        }
    }

}

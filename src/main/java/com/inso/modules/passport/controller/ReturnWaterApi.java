package com.inso.modules.passport.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.business.model.ReturnWaterLevel;
import com.inso.modules.passport.business.model.ReturnWaterLogDetail;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogDetailService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.report.service.UserStatusDayService;
import com.inso.modules.report.service.UserStatusV2DayService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/passport/returnWaterApi")
public class ReturnWaterApi {

    @Autowired
    private GameService mGameService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogService;

    @Autowired
    private ReturnWaterLogDetailService mReturnWaterLogDetailService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private ReturnRecordManager mReturnRecordManager;

    @Autowired
    private UserStatusV2DayService mUserStatusV2DayService;

    @Autowired
    private UserStatusDayService mUserStatusDayService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private ReturnWaterManager mReturnWaterManager;

    /**
     * @api {post} /passport/returnWaterApi/findReturnWaterLog
     * @apiDescription  获取反水记录
     * @apiName findReturnWaterLog
     * @apiGroup passport-return-water-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *         "data"
     *       }
     */
    @MyIPRateLimit(maxCount = 20)
    @MyLoginRequired
    @RequestMapping("findReturnWaterLog")
    public String findReturnWaterLog()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        ReturnWaterLog log = mReturnWaterLogService.findByUserid(false, userInfo.getId(), username, currencyType);
        UserAttr userAttr=mUserAttrService.queryTotalRechargeByParentid(false, userInfo.getId());
        if(userAttr!=null){
            log.setLevel1TotalRecharge(userAttr.getTotalRecharge());
        }

        apiJsonTemplate.setData(log);

        return apiJsonTemplate.toJSONString();
    }

    @MyIPRateLimit(maxCount = 20)
    @MyLoginRequired
    @RequestMapping("getReturnWaterLogList")
    public String getReturnWaterLogList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        List<ReturnWaterLog> rsList = mReturnWaterLogService.queryByUser(false, userInfo.getId());
//        UserAttr userAttr=mUserAttrService.queryTotalRechargeByParentid(false, userInfo.getId());
//        if(userAttr!=null){
//            log.setLevel1TotalRecharge(userAttr.getTotalRecharge());
//        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/returnWaterApi/getReturnWaterLogRecord
     * @apiDescription  获取反水记录(每分钟最多请求20次)
     * @apiName getReturnWaterLogRecord
     * @apiGroup passport-return-water-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  offset  0-90
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *       }
     */
    @MyIPRateLimit(maxCount = 20)
    @MyLoginRequired
    @RequestMapping("getReturnWaterLogRecord")
    public String getReturnWaterLogRecord()
    {
        int offset = WebRequest.getInt("offset", 0, 90);
        int level = WebRequest.getInt("level", 1, 2);
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List<ReturnWaterLogDetail> list = mReturnWaterLogDetailService.queryByUserid(false, userInfo.getId(), level, offset, 100);
        // 前端下级手机号是否加密
        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH);
        if(switchValue){
            for(int i=0;i< list.size();i++){
                if(!StringUtils.isEmpty(list.get(i).getChildname())) {
                    list.get(i).setChildname(phoneEncryption(list.get(i).getChildname()));
                }
            }
        }
        apiJsonTemplate.setData(list);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/returnWaterApi/getReturnWaterLevelList
     * @apiDescription  获取反水配置信息
     * @apiName getReturnWaterLevelList
     * @apiGroup passport-return-water-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *       }
     */
    @RequestMapping("getReturnWaterLevelList")
    public String getReturnWaterLevelList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        ReturnWaterLevel[] arr = ReturnWaterLevel.values();

        List list = new ArrayList(arr.length);

        for(ReturnWaterLevel tmp : arr)
        {
            Map<String, Object> model = Maps.newHashMap();
            model.put("key", tmp.getKey());
            model.put("firstRate", tmp.getFirstRate());
            model.put("secondRate", tmp.getSecondRate());
            model.put("inviteCountOfDay", tmp.getInviteCountOfDay());
            model.put("buyVipCountOfDay", tmp.getBuyVipCountOfDay());

            list.add(model);
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/returnWaterApi/getUserInviteDataList
     * @apiDescription  获取会员邀请信息信息
     * @apiName getUserReportDataList
     * @apiGroup passport-return-water-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *         data:[
     *              {
     *                  validLv1Count: 1级有效邀请人数
     *                  totalLv1Count  1级人数
     *                  returnLv1Amount 1级返佣金额
     *
     *                  totalLv2Count
     *                  validLv2Count:
     *                  returnLv2Amount
     *
     *
     *                  pdate: 2023-02-02
     *              }
     *         ]
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getUserInviteDataList")
    public String getUserInviteDataList()
    {
        String accessToken = WebRequest.getAccessToken();
        String useranme = mAuthService.getAccountByAccessToken(accessToken);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, useranme);

        DateTime nowTime = DateTime.now();

        UserStatusV2Day todayUserStatusV2Day = mReturnRecordManager.getDataInfo(useranme, nowTime.getDayOfYear());
        if(todayUserStatusV2Day == null)
        {
            todayUserStatusV2Day = new UserStatusV2Day();
        }

        // today
        todayUserStatusV2Day.loadTradeData(nowTime, useranme);
        todayUserStatusV2Day.initNotEmpty();
        todayUserStatusV2Day.setUserid(0);
        todayUserStatusV2Day.setUsername(useranme);

        DateTime fromTime = nowTime.minusDays(6);
        List<UserStatusV2Day> rsList = mUserStatusV2DayService.queryListByUser(false, fromTime, userInfo.getId());
        if(!CollectionUtils.isEmpty(rsList))
        {
            for(UserStatusV2Day model : rsList)
            {
                model.setAgentid(0);
                model.setAgentname(StringUtils.getEmpty());

                model.setStaffid(0);
                model.setStaffname(StringUtils.getEmpty());
            }
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("getInviteInfo")
    public String getInviteInfo()
    {
        int typeHour = WebRequest.getInt("typeHour");
        String accessToken = WebRequest.getAccessToken();
        String useranme = mAuthService.getAccountByAccessToken(accessToken);
        String remoteip = WebRequest.getRemoteIP();

        boolean isWhiteIp = WhiteIPManager.getInstance().verify(remoteip);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(isWhiteIp)
        {
            String tmpUsername = WebRequest.getString("username");
            if(!StringUtils.isEmpty(tmpUsername))
            {
                useranme = tmpUsername;
            }
        }

        // 1d | 3d | 24 * 7=168d | 24 * 30=720
        if(!(typeHour == 24 || typeHour == 72  || typeHour == 168 ||  typeHour == 360 || typeHour == 720))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, useranme);

        DateTime nowTime = DateTime.now();
        UserStatusV2Day todayUserStatusV2Day = mReturnRecordManager.getDataInfo(userInfo.getName(), nowTime.getDayOfYear());
        if(todayUserStatusV2Day == null)
        {
            todayUserStatusV2Day = new UserStatusV2Day();
        }

        // today
        todayUserStatusV2Day.loadTradeData(nowTime, useranme);
        todayUserStatusV2Day.initNotEmpty();
        todayUserStatusV2Day.setUserid(0);
        todayUserStatusV2Day.setUsername(useranme);

        // history
        if(typeHour > 24)
        {
            boolean purge = false;
            if(MyEnvironment.isDev())
            {
                purge = true;
            }
            else if(isWhiteIp)
            {
                purge = WebRequest.getPurge();
            }
            UserStatusV2Day userStatusV2Day = mUserStatusV2DayService.queryByUserid(purge, typeHour, userInfo.getId());
            if(userStatusV2Day != null)
            {
                userStatusV2Day.initNotEmpty();
                todayUserStatusV2Day.merge(userStatusV2Day);

                if(MemberSubType.PROMOTION.getKey().equalsIgnoreCase(userInfo.getSubType()))
                {
                    //
                    todayUserStatusV2Day.mergeCore(userStatusV2Day);
                }
            }
        }

        todayUserStatusV2Day.handleToSafeData(mReturnWaterManager.getReturnRate(true), mReturnWaterManager.getReturnRate(false));
        apiJsonTemplate.setData(todayUserStatusV2Day);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/getSubUserStatusInfo")
    public String getSubUserStatusInfo() {
        int periodOfDay = WebRequest.getInt("periodOfDay");

        ApiJsonTemplate api = new ApiJsonTemplate();

        //
        if(!(periodOfDay == 1 || periodOfDay == 7 || periodOfDay == 30))
        {
            return api.toJSONString();
        }

        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.STAFF)
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

        DateTime dateTime = DateTime.now();
        UserStatusDay rsResult = mReturnRecordManager.getAgentDataInfo(username, dateTime.getDayOfYear());
        if(rsResult == null)
        {
            rsResult = new UserStatusDay();
        }

        if(periodOfDay == 1)
        {
            BigDecimal totalMemberBalance = mUserMoneyService.queryAllMoneyByStaffUserid(false, userInfo.getId(), userType);
            rsResult.setTotalMemberBalance(totalMemberBalance);
        }
        else
        {
            dateTime = dateTime.minusDays(periodOfDay);
            UserStatusDay dbUserStatus = mUserStatusDayService.querySubStatsInfoByAgent(MyEnvironment.isDev(), userInfo.getId(), dateTime, periodOfDay);
            if(dbUserStatus != null)
            {
                rsResult.merge(dbUserStatus);
            }
        }

        api.setData(rsResult);
        return api.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/getAgentReportList")
    public String getAgentReportList() {
        int offset = WebRequest.getInt("offset");

        ApiJsonTemplate api = new ApiJsonTemplate();

        //
        if(offset < 0 || offset > 90)
        {
            api.setData(Collections.emptyList());
            return api.toJSONString();
        }

        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.STAFF)
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

        List rsList = mUserStatusDayService.queryListByAgent(false, userInfo.getId(), offset);
        api.setData(rsList);
        return api.toJSONString();
    }

    public String phoneEncryption(String phone){
        return MyLotteryBetRecordCache.encryUsername(phone);
    }

}

package com.inso.modules.game.rg.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.RecurHashMap;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.rg.helper.RGTotalAmountHelper;
import com.inso.modules.game.rg.helper.RgOpenResultHelper;
import com.inso.modules.game.rg.logical.RGBetTaskManager;
import com.inso.modules.game.rg.logical.RGLatestPeriodCache;
import com.inso.modules.game.rg.logical.RGPeriodStatus;
import com.inso.modules.game.rg.logical.RGRunningStatus;
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game/lotteryapi")
public class LotteryApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private RGBetTaskManager mLotteryBetConcurrent;

    @Autowired
    private LotteryOrderService mLotteryOrderService;

    /**
     * @api {post} /game/lotteryapi/getLotteryStatus
     * @apiDescription  获取彩种状态
     * @apiName getLotteryStatus
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  lotteryType
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *         "data" : {
     *              "issue": "20210326170610",
     *              "startTime": 1616749560000,
     *              "endTime": 1616749670000,
     *              "status": true
     *         }
     *       }
     */
//    @MyIPRateLimit
    @RequestMapping("/getLotteryStatus")
    public String getLotteryStatus()
    {
        String lotteryTypeStr = WebRequest.getString("lotteryType");
        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);
        String issue = WebRequest.getString("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Status status = Status.ENABLE;
        if(StringUtils.isEmpty(issue))
        {
            RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
            if(runningStatus == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
                return apiJsonTemplate.toJSONString();
            }
            issue = runningStatus.getCurrentIssue();
            status = runningStatus.getStatus();
        }

        RGPeriodStatus periodStatus = RGPeriodStatus.tryLoadCache(false, lotteryType, issue);
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long currentTime = System.currentTimeMillis();

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();
        long countdownSeconds = (endTime - currentTime) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        RecurHashMap dataMpas = new RecurHashMap();
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("countdownAudioSeconds", 3);
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", status == Status.ENABLE);

        dataMpas.put("totalAmount", RGTotalAmountHelper.getInstance().getTotalAmount());

        if(countdownSeconds <= 0)
        {
            String openResult = RgOpenResultHelper.getOpenResult(lotteryType, issue);
            if(!StringUtils.isEmpty(openResult))
            {
                dataMpas.put("openResult", openResult);
            }
        }

        DateTime dateTime = new DateTime(currentTime);

        int secondOfMinus = dateTime.getSecondOfMinute();
        int minutesOfHour = dateTime.getMinuteOfHour();

        RecurHashMap countdownDataMpas = dataMpas.getChild("otherCountdowns");
        for(LotteryRGType type : LotteryRGType.mArr)
        {
            int stepOfMinus = type.getStepOfMinutes();
            int otherCoundownSeconds = type.getTotalSeconds() - ( minutesOfHour % stepOfMinus ) * 60 - secondOfMinus - 2;
            if(otherCoundownSeconds <= 0)
            {
                otherCoundownSeconds = 0;
            }
            countdownDataMpas.put(type.getKey(), otherCoundownSeconds);
        }
        RecurHashMap disableTimeMap = dataMpas.getChild("disableTimeMap");
        for(LotteryRGType type : LotteryRGType.mArr)
        {
            disableTimeMap.put(type.getKey(), type.getDisableSecond());
        }
        RecurHashMap totalSecondMap = dataMpas.getChild("totalSecondMap");
        for(LotteryRGType type : LotteryRGType.mArr)
        {
            totalSecondMap.put(type.getKey(), type.getTotalSeconds());
        }

        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lotteryapi/submitOrder
     * @apiDescription  提交Lottery订单
     * @apiName submitOrder
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {long}   basicAmount     基础金额  basicAmount=10 | 100
     * @apiParam {long}   betCount        投注数量   1 <= betCount <= 1000,   10 <= basicAmount * betCount <= 100000
     * @apiParam {String}  betItem      投注项 【red|green|purple|0-9】
     * @apiParam {String}  lotteryType  彩种类型
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg      错误信息
     *
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
//    @MyIPRateLimit(maxCount = 20, expires = 60)
    @MyLoginRequired
    @RequestMapping("/submitOrder")
    public String submitOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        long basicAmount = WebRequest.getLong("basicAmount");
        long betCount = WebRequest.getLong("betCount");
        long betAmount = WebRequest.getLong("betAmount");

        String betItem = WebRequest.getString("betItem");
        String lotteryTypeString = WebRequest.getString("lotteryType");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(betAmount > 0)
        {
            basicAmount = betAmount;
            betCount = 1;
        }
        else
        {
            if(betCount <= 0 || basicAmount <= 0)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!BetAmountHelper.verifyBasicAmount(basicAmount))
            {
                apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
                return apiJsonTemplate.toJSONString();
            }
        }

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

//        if(betCount < 1 || ( betCount > 1000 && basicAmount == 100))
//        {
//            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
//            return apiJsonTemplate.toJSONString();
//        }

        if(!LotteryRgBetItemType.verifyBetItem(betItem))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }

        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeString);
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null || !runningStatus.verify())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        //
        RGPeriodStatus status = RGPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        long totalBetAmount = basicAmount * betCount;
        ErrorResult errorResult = status.verify(username, totalBetAmount);
        if(errorResult != SystemErrorResult.SUCCESS)
        {
            apiJsonTemplate.setJsonResult(errorResult);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.isEnable())
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(!userMoney.verify(new BigDecimal(totalBetAmount)))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        //
        boolean rs = mLotteryBetConcurrent.addItemToQueue(lotteryType, runningStatus.getCurrentIssue(), userInfo, basicAmount, betCount, betItem);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lotteryapi/getLatestBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getLatestBetRecord
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  lotteryType
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping("/getLatestBetRecord")
    public String getLatestBetRecord()
    {
        String lotteryTypeStr = WebRequest.getString("lotteryType");
        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

//        RGPeriodStatus status = RGPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
//        if(status == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//            return apiJsonTemplate.toJSONString();
//        }

//        long rsTime = System.currentTimeMillis() - status.getStartTime().getTime();
//        boolean fetchRobotData = rsTime >= 3000;
//        boolean refreshRobotData = false; // 刷新频率

        List list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryType, runningStatus.getCurrentIssue());
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lotteryapi/getUserCurrentBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getUserCurrentBetRecord
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  lotteryType
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getUserCurrentBetRecord")
    public String getUserBetRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String lotteryTypeStr = WebRequest.getString("lotteryType");
        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        RGPeriodStatus status = RGPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, lotteryType, username);
        if(!CollectionUtils.isEmpty(list))
        {
            for(JSONObject jsonObject : list)
            {
                String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
                String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);

                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());

                if(runningStatus.getCurrentIssue().equalsIgnoreCase(issue))
                {
                    // 当前期号，未开奖
                    continue;
                }

                // 获取开奖结果
                String openResult = RgOpenResultHelper.getOpenResult(lotteryType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, RgOpenResultHelper.getOpenResult(lotteryType, issue));
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, LotteryHelper.isWin(openResultNumber, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lotteryapi/getUserHistoryBetRecord
     * @apiDescription  获取用户历史投注记录
     * @apiName getUserCurrentBetRecord
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {type}  lotteryType
     * @apiParam {type}  type => waiting|history
     * @apiParam {String}  offset  0-90
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getUserHistoryBetRecord")
    public String getUserHistoryBetRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String lotteryTypeStr = WebRequest.getString("lotteryType"); //
        String type = WebRequest.getString("type"); //
        int offset = WebRequest.getInt("offset", 0, 90);

        LotteryRGType rgType = LotteryRGType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(rgType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if("waiting".equalsIgnoreCase(type))
        {
            List rsList = Lists.newArrayList();
            loadAllCurrentUserBetRecordForWaiting(rsList, userInfo.getName(), rgType);
            apiJsonTemplate.setData(rsList);
        }
        else
        {
            // 最新3天的数据的前100条数据
            DateTime dateTime = new DateTime().minusDays(3);
            String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
            List<LotteryOrderInfo> rsList = mLotteryOrderService.queryListByUserid(timeString, userInfo.getId(), rgType, offset);

            if(!CollectionUtils.isEmpty(rsList))
            {
                List list = Lists.newArrayList();

                for(LotteryOrderInfo model : rsList)
                {
//                    LotteryRGType rgType = LotteryRGType.getType(model.getType());
                    Map<String, Object> maps = Maps.newHashMap();
                    maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, rgType.getKey());
                    maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_NAME, rgType.getTitle());
                    maps.put(MyLotteryBetRecordCache.KEY_ISSUE, model.getIssue());
                    maps.put(MyLotteryBetRecordCache.KEY_FEEMONEY, model.getFeemoney());
                    maps.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, model.getBetAmount());
                    maps.put(MyLotteryBetRecordCache.KEY_BET_ITEM, model.getBetItem());
                    if(model.getOpenResult() == -1)
                    {
                        maps.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, RgOpenResultHelper.getOpenResult(rgType, model.getIssue()));
                    }
                    else
                    {
                        maps.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, model.getOpenResult());
                    }
                    maps.put(MyLotteryBetRecordCache.KEY_IS_WIN, model.getWinAmount().compareTo(BigDecimal.ZERO) > 0);
                    maps.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, model.getWinAmount());
                    maps.put(MyLotteryBetRecordCache.KEY_CREATETIME, DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));

                    list.add(maps);
                }

                apiJsonTemplate.setData(list);
            }
            else
            {
                apiJsonTemplate.setData(Collections.emptyList());
            }
        }

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lotteryapi/getLatestPeriodList
     * @apiDescription  获取最新期号列表
     * @apiName getLatestPeriodList
     * @apiGroup Game-lottery
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  lotteryType
     * @apiParam {int}  offset  0-90
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
//    @MyIPRateLimit
    @RequestMapping("/getLatestPeriodList")
    public String getLatestPeriodList()
    {
        int offset = WebRequest.getInt("offset", 0, 90);
        String lotteryTypeStr = WebRequest.getString("lotteryType");
        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        List list = RGLatestPeriodCache.getCache(lotteryType, offset);
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 加载当前投注记录-状态为等待-未开奖的
     * @param resultList
     * @param username
     * @param lotteryType
     */
    private void loadAllCurrentUserBetRecordForWaiting(List<JSONObject> resultList, String username, LotteryRGType lotteryType)
    {
        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            return;
        }

        RGPeriodStatus status = RGPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            return;
        }

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, lotteryType, username);
        if(CollectionUtils.isEmpty(list))
        {
            return;
        }

        for(JSONObject jsonObject : list)
        {
            String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
            if(runningStatus.getCurrentIssue().equalsIgnoreCase(issue))
            {
                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());
                jsonObject.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, StringUtils.getEmpty());
                resultList.add(jsonObject);
            }
        }
    }
}

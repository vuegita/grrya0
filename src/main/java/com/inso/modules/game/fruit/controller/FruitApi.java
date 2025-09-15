package com.inso.modules.game.fruit.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.fruit.helper.FruitOpenResultHelper;
import com.inso.modules.game.fruit.logical.FruitBetTaskManager;
import com.inso.modules.game.fruit.logical.FruitLatestPeriodCache;
import com.inso.modules.game.fruit.logical.FruitPeriodStatus;
import com.inso.modules.game.fruit.logical.FruitRunningStatus;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitOrderInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitOrderService;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.rg.helper.LotteryHelper;
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
@RequestMapping("/game/fruitApi")
public class FruitApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private FruitBetTaskManager  mBetConcurrent;

    @Autowired
    private FruitOrderService  mOrderService;

    /**
     * @api {post} /game/fruitApi/getStatus
     * @apiDescription  获取游戏状态
     * @apiName getStatus
     * @apiGroup Game-ab
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
    @RequestMapping("/getStatus")
    public String getStatus()
    {

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        FruitType fruitType = FruitType.PRIMARY;

        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(fruitType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodStatus periodStatus = FruitPeriodStatus.tryLoadCache(false, fruitType, runningStatus.getCurrentIssue());
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime() - fruitType.getDisableMillis();
        // 加5s表示，倒计时最后5s开始出现结果
        long countdownSeconds = (endTime - System.currentTimeMillis()) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        Map<String, Object> dataMpas = Maps.newHashMap();
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", runningStatus.verify());

        dataMpas.put("openResult", periodStatus.getOpenResult());
        dataMpas.put("startIndex", periodStatus.getStartIndex());
        /*** 目标牌-带颜色的 ***/
       // dataMpas.put("targetCard", periodStatus.getmCardOriginNumber());

        // 判断是否是开奖阶段, 如果是则把开奖结果输出
        if(periodStatus.getOpenResult() != null)
        {
            dataMpas.put("openResult", periodStatus.getOpenResult());
            dataMpas.put("startIndex", periodStatus.getStartIndex());

        }

        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getCurrentStatus")
    @MyLoginRequired
    public String getCurrentStatus()
    {
        String issue = WebRequest.getString("issue");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        FruitType fruitType = FruitType.PRIMARY;
        FruitPeriodStatus periodStatus = FruitPeriodStatus.tryLoadCache(false, fruitType, issue);
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime() - fruitType.getDisableMillis();
        // 加5s表示，倒计时最后5s开始出现结果
        long countdownSeconds = (endTime - System.currentTimeMillis()) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        Map<String, Object> dataMpas = Maps.newHashMap();
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("issue", periodStatus.getIssue());
//        dataMpas.put("status", runningStatus.verify());
        /*** 目标牌-带颜色的 ***/
        // dataMpas.put("targetCard", periodStatus.getmCardOriginNumber());

        // 判断是否是开奖阶段, 如果是则把开奖结果输出
        if(periodStatus.getOpenResult() != null &&  (endTime - System.currentTimeMillis() <= 0) )
        {
            dataMpas.put("openResult", periodStatus.getOpenResult());
            dataMpas.put("startIndex", periodStatus.getStartIndex());

        }

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fruitApi/submitOrder
     * @apiDescription  提交订单
     * @apiName submitOrder
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {long}   basicAmount     基础金额  basicAmount=10 | 100
     * @apiParam {long}   betCount        投注数量   1 <= betCount <= 1000,   10 <= basicAmount * betCount <= 100000
     * @apiParam {String}  betItem      投注项 【Andar|Bahar|Tie】
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
    @MyIPRateLimit(maxCount = 20, expires = 50)
    @MyLoginRequired
    @RequestMapping("/submitOrder")
    public String submitOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        long basicAmount = WebRequest.getLong("basicAmount");
        long betCount = WebRequest.getLong("betCount");

        String betItemString = WebRequest.getString("betItem");


        FruitType fruitType = FruitType.PRIMARY;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

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

        if(!BetAmountHelper.verifyBasicAmount(basicAmount))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }
        if(betCount < 1 || ( betCount > 1000 && basicAmount == 100))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }

        FruitBetItemType  betItem = FruitBetItemType.getType(betItemString);
        if(betItem == null)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }

        if(fruitType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(fruitType);
        if(runningStatus == null || !runningStatus.verify())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        //
        FruitPeriodStatus status = FruitPeriodStatus.tryLoadCache(false, fruitType, runningStatus.getCurrentIssue());
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
        boolean rs = mBetConcurrent.addItemToQueue(fruitType, runningStatus.getCurrentIssue(), userInfo, basicAmount, betCount, betItem);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fruitApi/getLatestBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getLatestBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
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
       // String lotteryTypeStr = WebRequest.getString("lotteryType");
//        LotteryFruitType lotteryType = LotteryFruitType.getType(lotteryTypeStr);
        FruitType fruitType = FruitType.PRIMARY;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(fruitType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodStatus status = FruitPeriodStatus.tryLoadCache(false, fruitType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

//        long rsTime = System.currentTimeMillis() - status.getStartTime().getTime();
//        boolean fetchRobotData = rsTime >= 3000;
//        boolean refreshRobotData = false; // 刷新频率

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, fruitType, status.getIssue());
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
                String openResult = FruitOpenResultHelper.getOpenResult(fruitType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, FruitOpenResultHelper.getOpenResult(fruitType, issue));
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, LotteryHelper.isWin(openResultNumber, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fruitApi/getUserCurrentBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getUserCurrentBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
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

//        String lotteryTypeStr = WebRequest.getString("lotteryType");
//        LotteryFruitType lotteryType = LotteryFruitType.getType(lotteryTypeStr);

        FruitType fruitType = FruitType.PRIMARY;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(fruitType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(fruitType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodStatus status = FruitPeriodStatus.tryLoadCache(false, fruitType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, fruitType, username);
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
                String openResult = FruitOpenResultHelper.getOpenResult(fruitType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, FruitOpenResultHelper.getOpenResult(fruitType, issue));
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, LotteryHelper.isWin(openResultNumber, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fruitApi/getUserHistoryBetRecord
     * @apiDescription  获取用户历史投注记录
     * @apiName getUserCurrentBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
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

        String type = WebRequest.getString("type"); //
        int offset = WebRequest.getInt("offset", 0, 90);

        FruitType fruitType = FruitType.PRIMARY;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);


        if("waiting".equalsIgnoreCase(type))
        {
            List rsList = Lists.newArrayList();
            loadAllCurrentUserBetRecordForWaiting(rsList, userInfo.getName(), fruitType);
            apiJsonTemplate.setData(rsList);
        }
        else
        {
            // 最新3天的数据的前100条数据
            DateTime dateTime = new DateTime().minusDays(3);
            String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
            List<FruitOrderInfo> rsList = mOrderService.queryListByUserid(timeString, userInfo.getId(), offset);

            if(!CollectionUtils.isEmpty(rsList))
            {
                List list = Lists.newArrayList();

                for(FruitOrderInfo model : rsList)
                {
                    FruitType tmpType = FruitType.getType(model.getType());
                    Map<String, Object> maps = Maps.newHashMap();
                    maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, tmpType.getKey());
                    maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_NAME, tmpType.getTitle());
                    maps.put(MyLotteryBetRecordCache.KEY_ISSUE, model.getIssue());
                    maps.put(MyLotteryBetRecordCache.KEY_FEEMONEY, model.getFeemoney());
                    maps.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, model.getBetAmount());
                    maps.put(MyLotteryBetRecordCache.KEY_BET_ITEM, model.getBetItem());
                    if(StringUtils.isEmpty(model.getOpenResult()))
                    {
                        maps.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, FruitOpenResultHelper.getOpenResult(tmpType, model.getIssue()));
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
     * @api {post} /game/fruitApi/getLatestPeriodList
     * @apiDescription  获取最新期号列表
     * @apiName getLatestPeriodList
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {int}  offset  0-490
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
    @MyIPRateLimit
    @RequestMapping("/getLatestPeriodList")
    public String getLatestPeriodList()
    {
        int offset = WebRequest.getInt("offset", 0, 490);

        FruitType fruitType = FruitType.PRIMARY;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(fruitType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        List list = FruitLatestPeriodCache.getCache(fruitType, offset);
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 加载当前投注记录-状态为等待-未开奖的
     * @param resultList
     * @param username
     * @param lotteryType
     */
    private void loadAllCurrentUserBetRecordForWaiting(List<JSONObject> resultList, String username, FruitType lotteryType)
    {
        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            return;
        }

        FruitPeriodStatus status = FruitPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
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

package com.inso.modules.game.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.lottery_game_impl.GameResultManager;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.lottery_game_impl.NewLotteryBetTaskManager;
import com.inso.modules.game.lottery_game_impl.NewLotteryPeriodStatus;
import com.inso.modules.game.lottery_game_impl.NewLotteryRunningStatus;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.turntable.helper.TurntableHelper;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game/turntableApi")
public class TurntableApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private NewLotteryBetTaskManager mLotteryBetConcurrent;


    /**
     * @api {post} /game/turntableApi/getLotteryStatus
     * @apiDescription  获取彩种状态
     * @apiName getLotteryStatus
     * @apiGroup game-turntable-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  lotteryType 值为 turntable_1_min,  本api下的所有此参数值都是这个
     * @apiParam {String}  issue 期号,  可为空，
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
     *              "disableSeconds":"封盘秒数"
     *              "status": true, 游戏或关闭状态，比如游戏维护，正常为一直true
     *              "openIndex":"开奖索引-开奖之后才有",
     *              "openResult":"开奖结果-开奖之后之后",
     *              "latestAssertList": "[]"  最新期号开奖数据-注意数据只拿开奖的数据
     *         }
     *       }
     */
    @RequestMapping("/getLotteryStatus")
    public String getLotteryStatus()
    {
//        String lotteryTypeStr = WebRequest.getString("lotteryType");
        TurnTableType lotteryType = TurnTableType.ROULETTE;

        String issue = WebRequest.getString("issue");

        boolean fetchAssertList = WebRequest.getBoolean("isFetchAssertList");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!StringUtils.isEmpty(issue) && !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        boolean status = false;

        if(StringUtils.isEmpty(issue))
        {
            NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
            if(runningStatus == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
                return apiJsonTemplate.toJSONString();
            }

            issue = runningStatus.getCurrentIssue();
            status = runningStatus.verify();
        }


        NewLotteryPeriodStatus periodStatus = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, issue);
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();
        long countdownSeconds = (endTime - System.currentTimeMillis()) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        Map<String, Object> dataMpas = Maps.newHashMap();
//        dataMpas.put("startTime", startTime);
//        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("disableSeconds", lotteryType.getDisableSecond());
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", status);
        if(countdownSeconds <= 0)
        {
            if(StringUtils.isEmpty(periodStatus.getOpenResult()))
            {
                periodStatus = NewLotteryPeriodStatus.tryLoadCache(true, lotteryType, issue);
            }
            if(periodStatus.getOpenIndex() >= 0 && !StringUtils.isEmpty(periodStatus.getOpenResult()))
            {
                dataMpas.put("openIndex", periodStatus.getOpenIndex());
                dataMpas.put("openResult", periodStatus.getOpenResult());
            }
        }

        if(fetchAssertList)
        {
            dataMpas.put("latestAssertList", NewLotteryLatestPeriod.getCache(lotteryType, 0, 10));
        }
        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/turntableApi/submitOrder
     * @apiDescription  提交Lottery订单
     * @apiName submitOrder
     * @apiGroup game-turntable-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {long}   basicAmount     基础金额  basicAmount=1 | 10 | 100 | 1000
     * @apiParam {long}   betCount        投注数量   >= 1
     * @apiParam {String}  betItem      投注项 【Ruby=红宝石|Emerald=绿宝石|Chest=宝箱】
     * @apiParam {String}  lotteryType  值为 turntable_1_min,  本api下的所有此参数值都是这个
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
    @MyLoginRequired
    @RequestMapping("/submitOrder")
    public String submitOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        long betAmount = WebRequest.getLong("betAmount");

        String betItem = WebRequest.getString("betItem");

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

        if(betAmount <= 0 || !BetAmountHelper.checkMinBetAmount(betAmount))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }

        TurnTableType lotteryType = TurnTableType.ROULETTE;
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String[] betItemArr = StringUtils.split(betItem, ',');
        if(betItemArr == null || betItemArr.length < 0)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }
        if(!lotteryType.verifyBetItem(betItemArr, true))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }


        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null || !runningStatus.verify())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        //
        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        ErrorResult errorResult = status.verify(username, betAmount);
        if(errorResult != SystemErrorResult.SUCCESS)
        {
            apiJsonTemplate.setJsonResult(errorResult);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.enableBet())
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        int betCount = betItemArr.length;
        BigDecimal singleBetAmount  = new BigDecimal(betAmount);
        BigDecimal totalBetAmount = singleBetAmount;
        if(betCount > 1)
        {
            totalBetAmount = totalBetAmount.multiply(new BigDecimal(betCount));
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(!userMoney.verify(totalBetAmount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        //
        boolean rs = mLotteryBetConcurrent.addItemToQueue(null, null, lotteryType, runningStatus.getCurrentIssue(), userInfo, totalBetAmount, betCount, singleBetAmount, betItem, betItemArr);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_FIRST);
        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/turntableApi/getLatestBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getLatestBetRecord
     * @apiGroup game-turntable-api
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
     *         "data":[
     *              {
     *                  "betAmount":"",
     *                  "feemoney":"",
     *                  "winAmount":"",
     *                  "betItem":"",
     *                  "createtime":"",
     *                  "username":"",
     *              }
     *         ]
     *       }
     */
    @RequestMapping("/getLatestBetRecord")
    public String getLatestBetRecord()
    {
        TurnTableType lotteryType = TurnTableType.ROULETTE;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long rsTime = System.currentTimeMillis() - status.getStartTime().getTime();
        boolean fetchRobotData = rsTime >= 3000;
        boolean refreshRobotData = false; // 刷新频率

        List list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryType, status.getIssue());
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/turntableApi/getUserCurrentBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getUserCurrentBetRecord
     * @apiGroup game-turntable-api
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
     *         "data":[
     *              {
     *                  "betAmount":"",
     *                  "betItem":"",
     *                  "createtime":"",
     *              }
     *         ]
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getUserCurrentBetRecord")
    public String getUserBetRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        TurnTableType lotteryType = TurnTableType.ROULETTE;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

//        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
//        if(status == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//            return apiJsonTemplate.toJSONString();
//        }

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
                String openResult = GameResultManager.getInstance().getStringResult(lotteryType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
//                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, GameResultManager.getInstance().getStringResult(lotteryType, issue));
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, TurntableHelper.isWin(openResult, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/turntableApi/getUserHistoryBetRecord
     * @apiDescription  获取用户历史投注记录
     * @apiName getUserCurrentBetRecord
     * @apiGroup game-turntable-api
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

        String type = WebRequest.getString("type"); //
        int offset = WebRequest.getInt("offset", 0, 90);

        TurnTableType rgType = TurnTableType.ROULETTE;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(rgType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List rsList = loadAllCurrentUserBetRecordForWaiting(userInfo.getName(), rgType);
        apiJsonTemplate.setData(rsList);

//        if("waiting".equalsIgnoreCase(type))
//        {
//            List rsList = Lists.newArrayList();
//            loadAllCurrentUserBetRecordForWaiting(rsList, userInfo.getName(), rgType);
//            apiJsonTemplate.setData(rsList);
//        }
//        else
//        {
//            // 最新3天的数据的前100条数据
//            DateTime dateTime = new DateTime().minusDays(3);
//            String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//            List<TurntableOrderInfo> rsList = mLotteryOrderService.queryListByUserid(timeString, userInfo.getId(), rgType, offset);
//
//            if(!CollectionUtils.isEmpty(rsList))
//            {
//                List list = Lists.newArrayList();
//
//                for(TurntableOrderInfo model : rsList)
//                {
////                    TurnTableType rgType = TurnTableType.getType(model.getType());
//                    Map<String, Object> maps = Maps.newHashMap();
//                    maps.put(RGBetRecordCache.KEY_LOTTERY_TYPE, rgType.getKey());
////                    maps.put(RGBetRecordCache.KEY_LOTTERY_NAME, rgType.getTitle());
//                    maps.put(RGBetRecordCache.KEY_ISSUE, model.getIssue());
//                    maps.put(RGBetRecordCache.KEY_FEEMONEY, model.getFeemoney());
//                    maps.put(RGBetRecordCache.KEY_BET_AMOUNT, model.getBetAmount());
//                    maps.put(RGBetRecordCache.KEY_BET_ITEM, model.getBetItem());
//                    if(StringUtils.isEmpty(model.getOpenResult()))
//                    {
//                        maps.put(RGBetRecordCache.KEY_OPEN_RESULT, TurntableOpenResultHelper.getOpenResult(rgType, model.getIssue()));
//                    }
//                    else
//                    {
//                        maps.put(RGBetRecordCache.KEY_OPEN_RESULT, model.getOpenResult());
//                    }
//                    maps.put(RGBetRecordCache.KEY_IS_WIN, model.getWinAmount().compareTo(BigDecimal.ZERO) > 0);
//                    maps.put(RGBetRecordCache.KEY_WIN_AMOUNT, model.getWinAmount());
//                    maps.put(RGBetRecordCache.KEY_CREATETIME, DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//
//                    list.add(maps);
//                }
//
//                apiJsonTemplate.setData(list);
//            }
//            else
//            {
//                apiJsonTemplate.setData(Collections.emptyList());
//            }
//        }

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/turntableApi/getLatestPeriodList
     * @apiDescription  获取最新期号列表
     * @apiName getLatestPeriodList
     * @apiGroup game-turntable-api
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
     *         "data": {
     *             "issue":"",
     *             "openResult":"",
     *         }
     *       }
     */
    @RequestMapping("/getLatestPeriodList")
    public String getLatestPeriodList()
    {
        int offset = WebRequest.getInt("offset", 0, 90);
        TurnTableType lotteryType = TurnTableType.ROULETTE;
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        List list = NewLotteryLatestPeriod.getCache(lotteryType, offset, 20);
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 加载当前投注记录-状态为等待-未开奖的
     * @param username
     * @param lotteryType
     */
    private List loadAllCurrentUserBetRecordForWaiting(String username, TurnTableType lotteryType)
    {
        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            return Collections.emptyList();
        }

        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            return Collections.emptyList();
        }

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, lotteryType, username);
        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        GameResultManager resultManager = GameResultManager.getInstance();
        for(JSONObject jsonObject : list)
        {
            String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
            if(runningStatus.getCurrentIssue().equalsIgnoreCase(issue))
            {
                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());
                jsonObject.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, StringUtils.getEmpty());
            }
            else
            {
                String result = resultManager.getStringResult(lotteryType, issue);
                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getNotEmpty(result));
            }
        }

        return list;
    }
}

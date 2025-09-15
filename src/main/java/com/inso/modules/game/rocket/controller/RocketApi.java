package com.inso.modules.game.rocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.lottery_game_impl.GameResultManager;
import com.inso.modules.game.lottery_game_impl.NewLotteryRunningStatus;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.rocket.engine.RocketPlayEngine;
import com.inso.modules.game.rocket.helper.RocketCashoutHelper;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.helper.RocketOpenResultHelp;
import com.inso.modules.game.rocket.logical.RocketBetTaskManager;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import com.inso.modules.game.rocket.model.RocketGameStatusInfo;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.websocket.impl.RocketMessageImpl;
import com.inso.modules.websocket.model.MyEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game/rocketApi")
public class RocketApi {

    private static Log LOG = LogFactory.getLog(RocketApi.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private RocketBetTaskManager mLotteryBetConcurrent;

    @Autowired
    private NewLotteryOrderService mLotteryOrderService;


    /**
     * @api {post} /game/rocketApi/getLotteryStatus
     * @apiDescription  获取彩种状态
     * @apiName getLotteryStatus
     * @apiGroup game-rocket-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  issue 期号,  可为空，
     * @apiParam {boolean}  isFetchAssertList 期号,  默认为空
     *
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
     *
     *              "totalBetCount":"下注总人数",
     *              "cashoutCount":"下车总人数",
     *              "currentResult":"过程临时结果-当前上升数字- 数据加密AES128, salt为 MD5(固定值+issue).substring(0,16) ",
     *              "openResult":"开奖结果-开奖之后之后- 加密",
     *              "latestAssertList": "[]"  最新期号开奖数据-注意数据只拿开奖的数据
     *         }
     *       }
     */
    @RequestMapping("/getLotteryStatus")
    public String getLotteryStatus(@RequestBody(required = false) JSONObject jsonObject)
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_STATUS_GET_LOTTERY.getKey());

        if(jsonObject == null || jsonObject.isEmpty())
        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
            jsonObject = FastJsonHelper.empty;
        }

        RocketType lotteryType = RocketType.CRASH;

        String issue = jsonObject.getString("issue");
        boolean fetchAssertList = jsonObject.getBooleanValue("isFetchAssertList");

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

        RocketPeriodStatus periodStatus = RocketPeriodStatus.tryLoadCache(false, lotteryType, issue);
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


        boolean showLatestIssue = false;

        RocketGameStatusInfo gameStatusInfo = RocketGameStatusInfo.loadFromCache(issue);
        if(gameStatusInfo != null)
        {
            dataMpas.put("totalBetCount", gameStatusInfo.getTotalBetCount());
            dataMpas.put("cashoutCount", gameStatusInfo.getCashoutCount());

            if(countdownSeconds <= 0)
            {
                if(!StringUtils.isEmpty(gameStatusInfo.getOpenResult()))
                {
                    String openResult = gameStatusInfo.getEncryptOpenResult(issue);
                    dataMpas.put("currentResult", openResult);
                    dataMpas.put("openResult", openResult);
                    showLatestIssue = true;
                }
                else if(!StringUtils.isEmpty(gameStatusInfo.getCurrentResult()))
                {
                    dataMpas.put("currentResult", gameStatusInfo.getEncryptCurrentResult(issue));
                }
            }
        }

//        dataMpas.put("totalBetCount", periodStatus.getmTotalBetCount() + periodStatus.getmRobotBetCount());
//        dataMpas.put("cashoutCount", periodStatus.getmCashoutCount().get() + periodStatus.getmRobotCashoutCount());
//
//        if(countdownSeconds <= 0)
//        {
//            if(!StringUtils.isEmpty(periodStatus.getOpenResult()))
//            {
//                String openResult = RocketGameStatusInfo.getStaticEncryptResult(issue, periodStatus.getOpenResult());
//                dataMpas.put("currentResult", openResult);
//                dataMpas.put("openResult", openResult);
//            }
//            else if(!StringUtils.isEmpty(periodStatus.getCurrentResult()))
//            {
//                dataMpas.put("currentResult", RocketGameStatusInfo.getStaticEncryptResult(issue, periodStatus.getCurrentResult()));
//            }
//        }

        if(showLatestIssue || (fetchAssertList && countdownSeconds > 0))
        {
            dataMpas.put("latestAssertList", NewLotteryLatestPeriod.getCache(lotteryType, 0, 10));
        }

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/rocketApi/submitOrder
     * @apiDescription  提交Lottery订单
     * @apiName submitOrder
     * @apiGroup game-rocket-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {long}   betAmount     下注金额|为整数
     * @apiParam {String}  betItem      投注项 默认0，表示手动
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
//    @MyLoginRequired
    @RequestMapping("/submitOrder")
    public String submitOrder(@RequestBody(required = false) JSONObject jsonObject)
    {
        RocketType lotteryType = RocketType.CRASH;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_SUBMIT_ORDER.getKey());

        if(jsonObject == null || jsonObject.isEmpty())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        long betAmount = jsonObject.getLongValue("betAmount");
        float betItem = jsonObject.getFloatValue("betItem");

        String accessToken = jsonObject.getString("accessToken");// WebRequest.getAccessToken();
        if(StringUtils.isEmpty(accessToken))
        {
            accessToken = WebRequest.getAccessToken();
        }
        if(StringUtils.isEmpty(accessToken))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        if(StringUtils.isEmpty(username))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
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

        if(betAmount <= 0 || !BetAmountHelper.checkMinBetAmount(betAmount))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }

        if(betItem <= 0)
        {
            betItem = 0;
        }
        else if(betItem >= 100)
        {
            betItem = 0;
        }

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null || !runningStatus.verify())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        //
        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_GAME_NOT_STARTED);
            return apiJsonTemplate.toJSONString();
        }

        BigDecimal totalBetAmount = new BigDecimal(betAmount);
        ErrorResult errorResult = status.verify(username, totalBetAmount);
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

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(!userMoney.verify(totalBetAmount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        //
        boolean rs = mLotteryBetConcurrent.addItemToQueue(lotteryType, runningStatus.getCurrentIssue(), userInfo, totalBetAmount, betItem + StringUtils.getEmpty());
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        else
        {
            Map<String, Object> dataMaps = Maps.newHashMap();
            dataMaps.put(MyLotteryBetRecordCache.KEY_ISSUE, status.getIssue());
            dataMaps.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, betAmount);
            apiJsonTemplate.setData(dataMaps);
        }

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/rocketApi/cashout
     * @apiDescription  cashout 订单
     * @apiName cashout
     * @apiGroup game-rocket-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken - 头信息
     * @apiParam {long}    time - 头信息-毫秒
     * @apiParam {sign}    sign - MD5(time + cashoutResult加密过后的数据 + accessToken.subString(0, 10))
     *
     * @apiParam {float}   cashoutResult     client从当前上升获取-此数据Base64加密
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
//    @MyLoginRequired
    @RequestMapping("/cashout")
    public String cashout(@RequestBody(required = false) JSONObject jsonObject)
    {
        RocketType rocketType = RocketType.CRASH;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_CASHOUT_ORDER.getKey());

        String accessToken = jsonObject.getString("accessToken");// WebRequest.getAccessToken();
        if(StringUtils.isEmpty(accessToken))
        {
            accessToken = WebRequest.getAccessToken();
        }
        if(StringUtils.isEmpty(accessToken))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        if(StringUtils.isEmpty(username))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
        }

        // header to
        long time = StringUtils.asLong(jsonObject.getString("time"));
        String cashoutResultBase64 = jsonObject.getString("cashoutResult");
        // header sign
        String sign = jsonObject.getString("sign");

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(rocketType);
        if(runningStatus == null || !runningStatus.verify())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(cashoutResultBase64) || StringUtils.isEmpty(sign))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        //
        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, rocketType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        String processOpenResultStr = status.getCurrentResult();

        if(!RequestTokenHelper.verifCashout(username, status.getIssue()))
        {
            // 并发限制
            apiJsonTemplate.setUnShowError(GameErrorResult.ERR_GAME_OVER);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        float precessOpenResult = StringUtils.asFloat(processOpenResultStr);
        if(precessOpenResult < 1)
        {
            apiJsonTemplate.setUnShowError(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

//        LOG.info("time = " +  time);
//        LOG.info("cashoutResultBase64 = " +  cashoutResultBase64);
//        LOG.info("precess-open-result = " +  precessOpenResult);
//        LOG.info("sign = " +  sign);

        String tmpSign = MD5.encode(time + cashoutResultBase64 + accessToken.substring(0, 16));
        if(!tmpSign.equalsIgnoreCase(sign))
        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err params for s data!");
//            return apiJsonTemplate.toJSONString();
        }

        String cashoutResultDecode = Base64Utils.decode(cashoutResultBase64);

//        if(MyEnvironment.isDev())
//        {
//            cashoutResultDecode = cashoutResultBase64;
//        }

        BigDecimal cashoutResult = new BigDecimal(cashoutResultDecode).setScale(2, RoundingMode.DOWN);
        float userCashoutResult = cashoutResult.floatValue();


//        LOG.info("user-cashout-result = " +  userCashoutResult);
//        LOG.info("precess-open-result = " +  precessOpenResult);

        if(userCashoutResult < 1 || userCashoutResult > precessOpenResult)
        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err params for result data!");
            apiJsonTemplate.setUnShowError("err params for result data!");
            LOG.error("cashout result error: userCashoutResult " + userCashoutResult + ", username = " + username);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }
        else
        {
            cashoutResultDecode = cashoutResult.toString();
        }

        // 是否下注过
//        if(!status.existBet(username))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
//            return apiJsonTemplate.toJSONString();
//        }

        if(RocketCashoutHelper.hasCashout(status.getIssue(), username))
        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FINISHED);
            return apiJsonTemplate.toJSONString();
        }

        // 游戏未进入 cashout阶段
        if(StringUtils.isEmpty(status.getCurrentResult()))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_ROCKET_UN_CASHOUT_STAGE);
            return apiJsonTemplate.toJSONString();
        }

        // 游戏已结束
        if(!StringUtils.isEmpty(status.getOpenResult()))
        {
            apiJsonTemplate.setUnShowError(GameErrorResult.ERR_GAME_OVER);
//            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_GAME_OVER);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
//        if(!userInfo.isEnable())
//        {
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
//            return apiJsonTemplate.toJSONString();
//        }

        String orderno = RocketHelper.nextOrderId(status.getIssue(), userInfo.getId(), false);
        NewLotteryOrderInfo orderInfo = mLotteryOrderService.findByNo(rocketType, orderno);
        if(orderInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        Map<String, Object> dataMaps = Maps.newHashMap();
        dataMaps.put(MyLotteryBetRecordCache.KEY_ISSUE, status.getIssue());
        dataMaps.put("cashoutResult", userCashoutResult);
        dataMaps.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, orderInfo.getTotalBetAmount());
        apiJsonTemplate.setData(dataMaps);

        RocketCashoutHelper.saveCashoutStatus(status.getIssue(), username);
        mLotteryOrderService.updateCashoutItem(rocketType, orderInfo.getNo(), cashoutResult.toString());

        status.decryByCashout(username, orderInfo.getTotalBetAmount(), cashoutResult, orderInfo.getBetItem());
        MyLotteryBetRecordCache.getInstance().updateBetItem(status.getIssue(), false, username, rocketType, orderInfo.getNo(), cashoutResultDecode);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /game/rocketApi/getLatestBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getLatestBetRecord
     * @apiGroup game-rocket-api
     * @apiVersion 1.0.0
     *
     * @apiParam {issue}    指定期号获取信息
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
    public String getLatestBetRecord(@RequestBody(required = false) JSONObject paramsJson)
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        RocketType lotteryType = RocketType.CRASH;
        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_STATUS_GET_LATEST_BET_RECORD.getKey());

        if(paramsJson == null || paramsJson.isEmpty())
        {
            paramsJson = FastJsonHelper.empty;
        }
        String issue = paramsJson.getString("issue");
        String showResult = paramsJson.getString("showResult");
        if(StringUtils.isEmpty(issue))
        {
            NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
            if(runningStatus == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
                return apiJsonTemplate.toJSONString();
            }

            issue = runningStatus.getCurrentIssue();
        }

        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, lotteryType, issue);
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

//        long rsTime = System.currentTimeMillis() - status.getStartTime().getTime();
//        boolean fetchRobotData = rsTime >= 3000;
//        boolean refreshRobotData = false; // 刷新频率

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryType, status.getIssue());

        RocketGameStatusInfo gameStatusInfo = RocketGameStatusInfo.loadFromCache(status.getIssue());
        if( !CollectionUtils.isEmpty(list))
        {
            float currentOpenResult = -1;
            if(gameStatusInfo != null)
            {
                currentOpenResult = StringUtils.asFloat(gameStatusInfo.getCurrentResult());
            }
            for(JSONObject jsonObject : list)
            {
//                String issue = jsonObject.getString(RGBetRecordCache.KEY_ISSUE);
                String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);
                BigDecimal betAmount = jsonObject.getBigDecimal(MyLotteryBetRecordCache.KEY_BET_AMOUNT);

                if(gameStatusInfo != null)
                {
                    boolean isWin = RocketHelper.isWin(betItem, gameStatusInfo.getCurrentResult());
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, isWin);

                    if(isWin && !StringUtils.isEmpty(betItem))
                    {
                        BigDecimal winAmount = RocketHelper.calcWinMoney(StringUtils.asFloat(betItem), currentOpenResult, betAmount);
                        jsonObject.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, winAmount);
                    }
                }

            }
        }

        Map<String, Object> dataMaps = Maps.newLinkedHashMap();

        dataMaps.put("showResult", showResult);
        dataMaps.put("list", list);

        apiJsonTemplate.setData(dataMaps);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/rocketApi/getUserCurrentBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getUserCurrentBetRecord
     * @apiGroup game-rocket-api
     * @apiVersion 1.0.0
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
//    @MyLoginRequired
//    @RequestMapping("/getUserCurrentBetRecord")
    public String getUserBetRecord(@RequestBody(required = false) JSONObject paramsJson)
    {
        RocketType lotteryType = RocketType.CRASH;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_STATUS_GET_USER_CURRENT_BET_RECORD.getKey());

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String accessToken = paramsJson.getString("accessToken");// WebRequest.getAccessToken();
//        if(StringUtils.isEmpty(accessToken))
//        {
//            accessToken = WebRequest.getAccessToken();
//        }
        if(StringUtils.isEmpty(accessToken))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
        }
        String username = mAuthService.getAccountByAccessToken(accessToken);

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

//        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
//        if(status == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//            return apiJsonTemplate.toJSONString();
//        }

        GameResultManager resultManager = GameResultManager.getInstance();
        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(true, lotteryType, username);
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
                String openResult = resultManager.getStringResult(lotteryType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
//                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, openResult);
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, !StringUtils.isEmpty(betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

//    /**
//     * @apiIgnore Not finished Method
//     * @api {post} /game/rocketApi/getUserHistoryBetRecord
//     * @apiDescription  获取用户历史投注记录
//     * @apiName getUserCurrentBetRecord
//     * @apiGroup game-rocket-api
//     * @apiVersion 1.0.0
//     *
//     * @apiParam {type}  lotteryType
//     * @apiParam {type}  type => waiting|history
//     * @apiParam {String}  offset  0-90
//     *
//     * @apiSuccess  {String}  code    错误码
//     * @apiSuccess  {String}  msg   错误信息
//     *
//     * @apiSuccessExample {json} Success-Response:
//     *       {
//     *         "code": 200,
//     *         "msg": "success",
//     *       }
//     */
//    @MyLoginRequired
//    @RequestMapping("/getUserHistoryBetRecord")
//    public String getUserHistoryBetRecord()
//    {
//        String accessToken = WebRequest.getAccessToken();
//        String username = mAuthService.getAccountByAccessToken(accessToken);
//
//        String type = WebRequest.getString("type"); //
//        int offset = WebRequest.getInt("offset", 0, 90);
//
//        RocketType rgType = RocketType.getType(WebRequest.getString("lotteryType"));
//
//        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//
//        UserInfo userInfo = mUserService.findByUsername(false, username);
//
//        if(rgType == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }
//
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
////                    RocketType rgType = RocketType.getType(model.getType());
//                    Map<String, Object> maps = Maps.newHashMap();
//                    maps.put(RGBetRecordCache.KEY_LOTTERY_TYPE, rgType.getKey());
////                    maps.put(RGBetRecordCache.KEY_LOTTERY_NAME, rgType.getTitle());
//                    maps.put(RGBetRecordCache.KEY_ISSUE, model.getIssue());
//                    maps.put(RGBetRecordCache.KEY_FEEMONEY, model.getFeemoney());
//                    maps.put(RGBetRecordCache.KEY_BET_AMOUNT, model.getBetAmount());
//                    maps.put(RGBetRecordCache.KEY_BET_ITEM, model.getBetItem());
//                    if(StringUtils.isEmpty(model.getOpenResult()))
//                    {
//                        maps.put(RGBetRecordCache.KEY_OPEN_RESULT, RocketOpenResultHelper.getOpenResult(rgType, model.getIssue()));
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
//
//        return apiJsonTemplate.toJSONString();
//    }

    /**
     * @api {post} /game/rocketApi/getLatestPeriodList
     * @apiDescription  获取最新期号列表
     * @apiName getLatestPeriodList
     * @apiGroup game-rocket-api
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
    public String getLatestPeriodList(@RequestBody(required = false) JSONObject paramsJson)
    {
        RocketType lotteryType = RocketType.CRASH;
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(RocketMessageImpl.GROUP_TYPE_GAME_ROCKET, MyEventType.GAME_STATUS_GET_LATEST_PERIOD_LIST.getKey());

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        int offset = paramsJson.getIntValue("offset");// WebRequest.getInt("offset", 0, 90);
        if(offset <= 0)
        {
            offset = 0;
        }
        else if(offset >= 80)
        {
            offset = 80;
        }

        List list = NewLotteryLatestPeriod.getCache(lotteryType, offset, 20);
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/sfsdsdfasldjflasdjfasldfuasldfjalsdujfalsdf987342345dfjgsdfgdfgsdfgsdfgklsdjfglsdjfglsdfjglsdfjglsdfjgsdlfjglsdkfgjlsdf")
    public String stop()
    {
        String key = WebRequest.getString("accessKey");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(!RocketOpenResultHelp.getInstance().existAccessKey(key))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        RocketPlayEngine engine = RocketPlayEngine.getInstance();
        if(!engine.safeStop())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
        }
        return apiJsonTemplate.toJSONString();
    }

//    /**
//     * 加载当前投注记录-状态为等待-未开奖的
//     * @param resultList
//     * @param username
//     * @param lotteryType
//     */
//    private void loadAllCurrentUserBetRecordForWaiting(List<JSONObject> resultList, String username, RocketType lotteryType)
//    {
//        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
//        if(runningStatus == null)
//        {
//            return;
//        }
//
//        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
//        if(status == null)
//        {
//            return;
//        }
//
//        List<JSONObject> list = TruntbBetRecordCache.getInstance().getUserRecordListFromCache(lotteryType, username);
//        if(CollectionUtils.isEmpty(list))
//        {
//            return;
//        }
//
//        for(JSONObject jsonObject : list)
//        {
//            String issue = jsonObject.getString(RGBetRecordCache.KEY_ISSUE);
//            if(runningStatus.getCurrentIssue().equalsIgnoreCase(issue))
//            {
//                jsonObject.put(RGBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());
//                jsonObject.put(RGBetRecordCache.KEY_WIN_AMOUNT, StringUtils.getEmpty());
//                resultList.add(jsonObject);
//            }
//        }
//    }

    public static void main(String[] args) {
        long ts = System.currentTimeMillis();
        String cashoutResut = "1.2";
        String accessToken = MD5.encode("123");


        String base64Encode = Base64Utils.encode(cashoutResut);

        String tmpSign = MD5.encode(ts + base64Encode + accessToken.substring(0, 16));

        System.out.println("time = " + ts);
        System.out.println("base64Encode = " + base64Encode);
        System.out.println("tmpSign = " + tmpSign);
    }
}


package com.inso.modules.game.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.RecurHashMap;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.MessageManager;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.lottery_game_impl.*;
import com.inso.modules.game.lottery_game_impl.base.HttpBetAsyncNotify;
import com.inso.modules.game.lottery_game_impl.base.IMessageAsyncNotify;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.logical.WebInfoManager;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.websocket.model.MyGroupType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/game/lottery2Api")
public class LotteryV2Api {

    private static Log LOG = LogFactory.getLog(LotteryV2Api.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private NewLotteryBetTaskManager mLotteryBetConcurrent;

    @Autowired
    private NewLotteryOrderService mNewLotteryOrderService;

    @Autowired
    private GameService mGameService;


    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private WebInfoManager mWebInfoManager;


    private static List<GameChildType> mEnableHttpGameTypeArr = Lists.newArrayList();

    public LotteryV2Api()
    {
        mEnableHttpGameTypeArr.add(BTCKlineType.BTC_KLINE_1MIN);
        mEnableHttpGameTypeArr.add(BTCKlineType.BTC_KLINE_5MIN);

        mEnableHttpGameTypeArr.add(TurnTableType.ROULETTE);
        for(GameChildType tmp : RedGreen2Type.mArr)
        {
            mEnableHttpGameTypeArr.add(tmp);
        }

    }

    @RequestMapping("/stream/status")
    public void streamStatus(HttpServletRequest request, HttpServletResponse response)
    {
        try {
            response.setContentType("text/event-stream");
            // 设置编码格式为UTF-8
            response.setCharacterEncoding("UTF-8");
            // 禁止浏览器缓存响应
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Accel-Buffering", "no");

            PrintWriter out = response.getWriter();

            String startStr = "data: ";
            String endStr = "\n\n";

            String accessToken = WebRequest.getString("accessToken");

            GameChildType lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
            if(lotteryType == null)
            {
                return;
            }

            if(WebRequest.getBoolean("fetchLotteryStatusAll"))
            {
                BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(lotteryType);
                GameChildType[] allType = processor.getAllGameTypes();
                for(GameChildType tmp : allType)
                {
                    out.write(startStr + getLotteryStatus(tmp.getKey()) + endStr);
                    out.flush();
                }
            }
            else
            {
                out.write(startStr + getLotteryStatus(lotteryType.getKey()) + endStr);
                out.flush();
            }

            if(WebRequest.getBoolean("getLatestPeriodList"))
            {
                out.write(startStr + getLatestPeriodList() + endStr);
                out.flush();
            }

            if(WebRequest.getBoolean("getUserBetRecord"))
            {
                out.write(startStr + getUserBetRecord() + endStr);
                out.flush();

                // 获取最新一下单记录
                String username = mAuthService.getAccountByAccessToken(accessToken);
                int count = WebRequest.getInt("getUserBetRecord_count");
                if(count > 1 && !StringUtils.isEmpty(username))
                {
                    JSONObject firstDataJSon = null;
                    List<JSONObject> userRecordList = loadAllCurrentUserBetRecordForWaiting(0, username, lotteryType);
                    if(!CollectionUtils.isEmpty(userRecordList))
                    {
                        firstDataJSon = userRecordList.get(0);
                    }

                    boolean first = true;
                    count = Math.min(count, 3);
                    for(int i = 0; i < 10; i ++)
                    {
                        if(first)
                        {
                            first = false;
                        }
                        else
                        {
                            ThreadUtils.sleep(1000);
                        }

                        userRecordList = loadAllCurrentUserBetRecordForWaiting(0, username, lotteryType);
                        if(CollectionUtils.isEmpty(userRecordList) )
                        {
                            continue;
                        }

                        if(firstDataJSon != null && firstDataJSon == userRecordList.get(0))
                        {
                            continue;
                        }

                        out.write(startStr + getUserBetRecord() + endStr);
                        out.flush();
                        break;
                    }
                }
            }

            // 模拟数据流
//            for (int i = 0; i < 10; i++) {
//                try {
//                    // 通过sleep方法来模拟数据产生的间隔时间
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                // 按照事件流格式输出数据
//                out.write("data: " + i + "\n\n");
//                out.flush();
//            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @api {post} /game/lottery2Api/getLotteryStatus
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
    public String getLotteryStatus(@RequestParam(required = false) String lotteryTypeValue)
    {
        return getLotteryStatus_internal(lotteryTypeValue, null);
    }
    public String getLotteryStatus_internal(@RequestParam(required = false) String lotteryTypeValue, JSONObject jsonObject)
    {
        GameChildType lotteryType = GameChildType.getType(lotteryTypeValue);
        if(lotteryType == null)
        {
            lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        }
        else if(jsonObject != null)
        {
            lotteryType = GameChildType.getType(jsonObject.getString("lotteryType"));
        }

        // issue
        String issue = null;
        if(jsonObject != null)
        {

        }
        else {
            issue = WebRequest.getString("issue");
        }

        //
        boolean fetchAssertList = false;
        if(jsonObject != null)
        {
            fetchAssertList = jsonObject.getBoolean("isFetchAssertList");
        }
        else
        {
            fetchAssertList = WebRequest.getBoolean("isFetchAssertList");
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        apiJsonTemplate.setEvent(null, "getLotteryStatus");

        if(lotteryType == null || lotteryType == RocketType.CRASH)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!StringUtils.isEmpty(issue) && !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        String lastIssue = StringUtils.getEmpty();
        boolean status = false;
        if(StringUtils.isEmpty(issue))
        {
            NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
            if(runningStatus == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
                return apiJsonTemplate.toJSONString();
            }

            lastIssue = runningStatus.getLastIssue();
            issue = runningStatus.getCurrentIssue();
            status = runningStatus.verify();
        }


        NewLotteryPeriodStatus periodStatus = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, issue);
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        boolean isRedGreen = lotteryType instanceof RedGreen2Type;
        long currentTime = System.currentTimeMillis();

//        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime() + 1_000;
        long countdownSeconds = (endTime - System.currentTimeMillis()) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        RecurHashMap dataMpas = new RecurHashMap();
//        dataMpas.put("startTime", startTime);
//        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("lotteryType", lotteryType.getKey());
        dataMpas.put("totalSeconds", lotteryType.getTotalSeconds());
        dataMpas.put("countdownSeconds", countdownSeconds);
        if(isRedGreen)
        {
            dataMpas.put("countdownAudioSeconds", 3);
        }
        dataMpas.put("disableSeconds", lotteryType.getDisableSecond());
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", status);
        dataMpas.put("lastIssue", lastIssue);
        if(countdownSeconds <= 0)
        {
            if(StringUtils.isEmpty(periodStatus.getOpenResult()))
            {
                periodStatus = NewLotteryPeriodStatus.tryLoadCache(true, lotteryType, issue);
            }
            if( !StringUtils.isEmpty(periodStatus.getOpenResult()))
            {
                if(periodStatus.getOpenIndex() >= 0)
                {
                    dataMpas.put("openIndex", periodStatus.getOpenIndex());
                }
                dataMpas.put("openResult", periodStatus.getOpenResult());
            }
        }

        if(fetchAssertList)
        {
            dataMpas.put("latestAssertList", NewLotteryLatestPeriod.getCache(lotteryType, 0, 10));
        }

        if(isRedGreen)
        {
            DateTime dateTime = new DateTime(currentTime);

            int secondOfMinus = dateTime.getSecondOfMinute();
            int minutesOfHour = dateTime.getMinuteOfHour();

            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(lotteryType);
            GameChildType[] allType = processor.getAllGameTypes();

            RecurHashMap countdownDataMpas = dataMpas.getChild("otherCountdowns");
            for(GameChildType tmpItem : allType)
            {
                RedGreen2Type type = (RedGreen2Type) tmpItem;
                int stepOfMinus = type.getStepOfMinutes();
                int otherCoundownSeconds = type.getTotalSeconds() - ( minutesOfHour % stepOfMinus ) * 60 - secondOfMinus - 1;
                if(otherCoundownSeconds <= 0)
                {
                    otherCoundownSeconds = 0;
                }
                countdownDataMpas.put(type.getKey(), otherCoundownSeconds);
            }
            RecurHashMap disableTimeMap = dataMpas.getChild("disableTimeMap");
            for(GameChildType tmpItem : allType)
            {
                RedGreen2Type type = (RedGreen2Type) tmpItem;
                disableTimeMap.put(type.getKey(), type.getDisableSecond());
            }
            RecurHashMap totalSecondMap = dataMpas.getChild("totalSecondMap");
            for(GameChildType tmpItem : allType)
            {
                RedGreen2Type type = (RedGreen2Type) tmpItem;
                totalSecondMap.put(type.getKey(), type.getTotalSeconds());
            }
        }

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lottery2Api/submitOrder
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
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        GameChildType lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        boolean enable = false;
        for(GameChildType tmpType : mEnableHttpGameTypeArr)
        {
            if(tmpType == lotteryType)
            {
                enable = true;
                break;
            }
        }

        if(!enable)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }
        return submitOrder_internal(apiJsonTemplate, null, null, null);
    }
    public String submitOrder_internal(ApiJsonTemplate apiJsonTemplate, JSONObject jsonObject, IMessageAsyncNotify asyncNotify, String sessionid)
    {
        if(apiJsonTemplate == null)
        {
            apiJsonTemplate = new ApiJsonTemplate();
        }
        String accessToken = null;
        long betAmount = 0;
        String betItem = null;
        String tmpExternalId = null;
        GameChildType lotteryType = null;
        if(jsonObject != null)
        {
            accessToken = jsonObject.getString("accessToken");
            betAmount = jsonObject.getLongValue("betAmount");
            betItem = jsonObject.getString("betItem");
            lotteryType = GameChildType.getType(jsonObject.getString("lotteryType"));
        }
        else
        {
            accessToken = WebRequest.getAccessToken();
            betAmount = WebRequest.getLong("betAmount");
            betItem = WebRequest.getString("betItem");
            tmpExternalId = WebRequest.getString("tmpExternalId");
            lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);

        if(lotteryType == null || lotteryType == RocketType.CRASH || StringUtils.isEmpty(betItem))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!StringUtils.isEmpty(tmpExternalId) && (tmpExternalId.length() >= 50 || !RegexUtils.isLetterDigit(tmpExternalId)))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        tmpExternalId = StringUtils.getNotEmpty(tmpExternalId);
        if(asyncNotify != null)
        {
//            asyncNotify.setExternalId(tmpExternalId);
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

        betItem = betItem.toLowerCase();
        String[] betItemArr = StringUtils.split(betItem, ',');
        if(betItemArr == null)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }

        int betLen = betItemArr.length;
        if(betLen < 0 || betLen >= 15)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }
        if(!lotteryType.verifyBetItem(betItemArr, true))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
            return apiJsonTemplate.toJSONString();
        }

//        if(lotteryType == BTCKlineType.BTC_KLINE_5MIN)
//        {
//            // 关闭数字
//            boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_AD_VIP0_DAILY_DOWNLOAD_APP_SWITCH);
//            if(!switchValue )
//            {
//                for(String tmpBetItem : betItemArr)
//                {
//                    if(RegexUtils.isDigit(tmpBetItem))
//                    {
//                        apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
//                        return apiJsonTemplate.toJSONString();
//                    }
//                }
//            }
//        }

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

        GameInfo gameInfo = mGameService.findByKey(false, lotteryType.getKey());
        if(!Status.ENABLE.getKey().equalsIgnoreCase(gameInfo.getStatus()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
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
        boolean rs = mLotteryBetConcurrent.addItemToQueue(sessionid, asyncNotify, lotteryType, runningStatus.getCurrentIssue(), userInfo, totalBetAmount, betCount, singleBetAmount, betItem, betItemArr);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_FIRST);
        }
        else
        {
            apiJsonTemplate.setData("queue");
        }

       try{
           // 获取配置
           WebInfoManager.TargetType targetType = WebInfoManager.TargetType.PHONE_AREA_CODE;
           String value = mWebInfoManager.getInfo(targetType);
           String[] valueArray = null;
           if(!StringUtils.isEmpty(value))
           {
                valueArray = StringUtils.split(value, '|');
           }

           MessageManager.getInstance().sendUserBetMessageTG(userInfo,betAmount+"",runningStatus.getType(), runningStatus.getCurrentIssue(),betItemArr,userMoney,valueArray);
       } catch (Exception e) {
        e.printStackTrace();
    }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lottery2Api/getLatestBetRecord
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
        return getLatestBetRecord_internal(null, null);
    }

    public String getLatestBetRecord_internal(JSONObject jsonObject, String groupType)
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(groupType, "getLatestBetRecord");

        GameChildType lotteryType = null;
        if(jsonObject != null)
        {
            lotteryType = GameChildType.getType(jsonObject.getString("lotteryType"));
        }
        else
        {
            lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        }
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

        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        List list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryType, status.getIssue());
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lottery2Api/getUserCurrentBetRecord
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
        return getUserBetRecord_internal(null, null);
    }
    public String getUserBetRecord_internal(JSONObject jsonObject, String groupType)
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(groupType, "getUserCurrentBetRecord");

        String accessToken = null;
        GameChildType lotteryType = null;
        int offset = 0;
        if(jsonObject != null)
        {
            offset = jsonObject.getIntValue("offset");
            lotteryType = GameChildType.getType(jsonObject.getString("lotteryType"));
            accessToken = jsonObject.getString("accessToken");
        }
        else
        {
            offset = WebRequest.getInt("offset", 0, 90);
            lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
            accessToken = WebRequest.getAccessToken();
        }
        if(offset < 0)
        {
            offset = 0;
        }
        if(offset > 90)
        {
            offset = -1;
        }

        if(lotteryType == null || offset < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        if(StringUtils.isEmpty(username))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            return apiJsonTemplate.toJSONString();
        }

//        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
//        if(runningStatus == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//            return apiJsonTemplate.toJSONString();
//        }

        List<JSONObject> list = loadAllCurrentUserBetRecordForWaiting(offset, username, lotteryType);
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lottery2Api/getUserHistoryBetRecord
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
        GameChildType lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));

        int offset = WebRequest.getInt("offset", 0, 90);

//        String dateTimeStr = WebRequest.getString("dateTime");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        DateTime dateTime = DateTime.now().minusDays(5);
        List<NewLotteryOrderInfo> rsList = mNewLotteryOrderService.queryListByUserid(MyEnvironment.isDev(), dateTime, userInfo.getId(), lotteryType, offset);
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(lotteryType);

        char split = ',';
        for(NewLotteryOrderInfo orderInfo : rsList)
        {
            orderInfo.setAgentid(0);
            orderInfo.setAgentname(StringUtils.getEmpty());

            orderInfo.setStaffid(0);
            orderInfo.setStaffname(StringUtils.getEmpty());

            orderInfo.setUserid(0);
            orderInfo.setUsername(StringUtils.getEmpty());


            if(lotteryType == RocketType.CRASH)
            {

            }
            else
            {
                List betItemList = Lists.newArrayList();
                String[] betItemArr = StringUtils.split(orderInfo.getBetItem(), split);
                for(String tmpItem : betItemArr)
                {
                    JSONObject itemResult = new JSONObject();
                    itemResult.put(MyLotteryBetRecordCache.KEY_BET_ITEM, tmpItem);

                    if(!StringUtils.isEmpty(orderInfo.getOpenResult()))
                    {
                        BigDecimal winamount = processor.calcWinAmount(orderInfo.getOpenResult(), orderInfo.getSingleBetAmount(), tmpItem);
                        itemResult.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, BigDecimalUtils.getNotNull(winamount));
                    }

                    betItemList.add(itemResult);
                }
                orderInfo.setBetItemResultList(betItemList);
            }
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/lottery2Api/getLatestPeriodList
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
        GameChildType lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(null, "getLatestPeriodList");
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
    private List loadAllCurrentUserBetRecordForWaiting(int offset, String username, GameChildType lotteryType)
    {
        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, lotteryType, username);
        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int size = list.size();
        if(offset >= size)
        {
            return Collections.emptyList();
        }

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
//        if(runningStatus == null)
//        {
//            return Collections.emptyList();
//        }

        int count = 0;
//        List rsResultList = Lists.newArrayList();
        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(lotteryType);

//        if(runningStatus != null)
//        {
//            String lastIssue = runningStatus.getLastIssue();
//            String openResult = GameResultManager.getInstance().getStringResult(lotteryType, lastIssue);
//            LOG.info("last issue = " + lastIssue + ", openResult = " + openResult + ", current-issue = " + runningStatus.getCurrentIssue() + ", username = " + username);
//        }

        for(int i = offset; i < size; i ++)
        {
            if(count >= 20)
            {
                break;
            }
            JSONObject jsonObject = list.get(i);
//            rsResultList.add(jsonObject);

            String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
            String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);

            if(!StringUtils.isEmpty(jsonObject.getString(MyLotteryBetRecordCache.KEY_IS_WIN)))
            {
                continue;
            }

            jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());
            if(!lotteryType.autoBoot())
            {
                // 自定义的类型
                continue;
            }

            if(runningStatus != null && runningStatus.getCurrentIssue().equalsIgnoreCase(issue))
            {
                // 当前期号，未开奖
                continue;
            }

            BigDecimal betAmount = jsonObject.getBigDecimal(MyLotteryBetRecordCache.KEY_BET_AMOUNT);

            // 获取开奖结果
            JSONObject jsonResult = GameResultManager.getInstance().getJsonResult(lotteryType, issue);
//            String openResult = GameResultManager.getInstance().getStringResult(lotteryType, issue);
            if(jsonResult != null)
            {
                String openResult = jsonResult.getString(MyLotteryBetRecordCache.KEY_OPEN_RESULT);
                if(StringUtils.isEmpty(openResult))
                {
                    continue;
                }
                // 开奖结果
//                jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, GameResultManager.getInstance().getStringResult(lotteryType, issue));
                BigDecimal winAmount = processor.calcWinAmount(openResult, betAmount, betItem);

                boolean isWin = winAmount != null && winAmount.compareTo(BigDecimal.ZERO) > 0;
                // 是否中奖
                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, isWin);
                jsonObject.put(MyLotteryBetRecordCache.KEY_WIN_AMOUNT, winAmount);
                jsonObject.putAll(jsonResult);
            }

            count ++;
        }

        return list;
    }

}

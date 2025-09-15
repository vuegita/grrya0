package com.inso.modules.game.red_package.controller;

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
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.helper.RedPOpenResultHelper;
import com.inso.modules.game.red_package.logical.RedPBetTaskManager;
import com.inso.modules.game.red_package.logical.RedPPeriodStatus;
import com.inso.modules.game.red_package.model.RedPBetOrderInfo;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPBetOrderService;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.rg.helper.LotteryHelper;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/game/redPBetApi")
public class BetApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private RedPBetTaskManager mBetConcurrent;

    @Autowired
    private RedPBetOrderService mOrderService;

    @Autowired
    private RedPPeriodService mRedPackagePeriodService;

    /**
     * @api {post} /game/redPBetApi/getStatus
     * @apiDescription  获取游戏状态
     * @apiName getStatus
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  type
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
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
        String typeString = WebRequest.getString("type");
        long issue = WebRequest.getLong("issue");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        RedPType redPType = RedPType.getType(typeString);

        RedPPeriodStatus periodStatus = RedPPeriodStatus.tryLoadCache(false, redPType, issue);
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        RedPPeriodInfo model = mRedPackagePeriodService.findByIssue(false, issue);
        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
        // 只有等待完成才能操作

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();
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
        dataMpas.put("status", status.getKey());


        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/redPBetApi/submitOrder
     * @apiDescription  提交订单
     * @apiName submitOrder
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  accessToken
     * @apiParam {long}   basicAmount     基础金额  basicAmount=10 | 100
     * @apiParam {long}   betCount        投注数量   1 <= betCount <= 1000,   10 <= basicAmount * betCount <= 100000
     * @apiParam {string}  betItem      投注项 【Big|Small|Single|Even】
     * @apiParam {long}  issue      红包id
     * @apiParam {string}  type      投注项 number
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg      错误信息
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

        String typeString = WebRequest.getString("type");
        RedPType redPType = RedPType.getType(typeString);

        long issue = WebRequest.getLong("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!LotteryHelper.checkBasicAmount(basicAmount))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }
        if(betCount < 1 || ( betCount > 1000 && basicAmount == 100))
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_BET_MONEY);
            return apiJsonTemplate.toJSONString();
        }

//        RedPBetItemType betItem = RedPBetItemType.getType(betItemString);
//        if(betItem == null)
//        {
//            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_BET_ITEM);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        if(redPType == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        //
        RedPPeriodStatus status = RedPPeriodStatus.tryLoadCache(false, redPType, issue);
        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }


        float totalBetAmount = basicAmount * betCount;
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
//        boolean rs = mBetConcurrent.addItemToQueue(redPType, issue, userInfo, basicAmount, betCount, betItem);
//        if(!rs)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/redPBetApi/getLatestBetRecord
     * @apiDescription  获取最新投注记录-当前运行的期号
     * @apiName getLatestBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  type      投注项 number
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
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
        String typeString = WebRequest.getString("type");
        RedPType redPType = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, redPType, null);
        if(!CollectionUtils.isEmpty(list))
        {
            for(JSONObject jsonObject : list)
            {
                String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
                String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);

                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());

                // 获取开奖结果
                String openResult = RedPOpenResultHelper.getOpenResult(redPType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, openResult);
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, LotteryHelper.isWin(openResultNumber, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/redPBetApi/getUserLatestBetRecord
     * @apiDescription  获取用户最新投注记录
     * @apiName getUserLatestBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getUserLatestBetRecord")
    public String getUserLatestBetRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String typeString = WebRequest.getString("type");
        RedPType redPType = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(redPType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<JSONObject> list = MyLotteryBetRecordCache.getInstance().getUserRecordListFromCache(false, redPType, username);
        if(!CollectionUtils.isEmpty(list))
        {
            for(JSONObject jsonObject : list)
            {
                String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
                String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);

                jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, StringUtils.getEmpty());

                // 获取开奖结果
                String openResult = RedPOpenResultHelper.getOpenResult(redPType, issue);
                if(!StringUtils.isEmpty(openResult))
                {
                    long openResultNumber = StringUtils.asInt(openResult);

                    // 开奖结果
                    jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, openResult);
                    // 是否中奖
                    jsonObject.put(MyLotteryBetRecordCache.KEY_IS_WIN, LotteryHelper.isWin(openResultNumber, betItem));
                }
            }
        }

        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/redPBetApi/getUserHistoryBetRecord
     * @apiDescription  获取用户历史投注记录
     * @apiName getUserCurrentBetRecord
     * @apiGroup Game-ab
     * @apiVersion 1.0.0
     *
     * @apiParam {type}  type => waiting|history
     * @apiParam {string}  offset  0-90
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
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

        int offset = WebRequest.getInt("offset", 0, 90);

        String typeString = WebRequest.getString("type");
        RedPType redPType = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(redPType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);


        List<RedPBetOrderInfo> rsList = mOrderService.queryListByUserid(false, redPType, userInfo.getId(), offset);

        if(!CollectionUtils.isEmpty(rsList))
        {
            List list = Lists.newArrayList();

            for(RedPBetOrderInfo model : rsList)
            {
//                RedPType tmpType = RedPType.getType(model.getType());
                Map<String, Object> maps = Maps.newHashMap();
                maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, redPType.getKey());
                maps.put(MyLotteryBetRecordCache.KEY_LOTTERY_NAME, redPType.getTitle());
                maps.put(MyLotteryBetRecordCache.KEY_ISSUE, model.getRpid());
                maps.put(MyLotteryBetRecordCache.KEY_FEEMONEY, model.getFeemoney());
                maps.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, model.getBetAmount());
                maps.put(MyLotteryBetRecordCache.KEY_BET_ITEM, model.getBetItem());
                if(StringUtils.isEmpty(model.getOpenResult()))
                {
                    maps.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, RedPOpenResultHelper.getOpenResult(redPType, model.getRpid() + StringUtils.getEmpty()));
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

        return apiJsonTemplate.toJSONString();
    }


}

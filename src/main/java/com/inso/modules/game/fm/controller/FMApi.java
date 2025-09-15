package com.inso.modules.game.fm.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.passport.UserErrorResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.game.fm.logical.FMBuyTaskManager;
import com.inso.modules.game.fm.logical.FMPeriodStatus;
import com.inso.modules.game.fm.logical.FMProductListManager;
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.FMOrderService;
import com.inso.modules.game.fm.service.FMProductService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.UserBetReportLogical;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;

@RestController
@RequestMapping("/game/fmApi")
public class FMApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private FMProductService mProductInfoService;

    @Autowired
    private FMOrderService mOrderService;

    @Autowired
    private FMBuyTaskManager mFMBuyTaskManager;



    /**
     * @api {post} /game/fmApi/getProductList
     * @apiDescription  获取产品列表
     * @apiName getProductList
     * @apiGroup Game-fm
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  type  waiting | history
     * @apiParam {int}  offset
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
    @RequestMapping("/getProductList")
    public String getProductList()
    {
        String type = WebRequest.getString("type");
        int offset = WebRequest.getInt("offset", 0, 90);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        boolean isFinish = false;
        if("history".equalsIgnoreCase(type))
        {
            isFinish = true;
        }
        List dataList = FMProductListManager.getDataList(isFinish, offset);
        apiJsonTemplate.setData(dataList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fmApi/getStatus
     * @apiDescription  获取产品状态
     * @apiName getStatus
     * @apiGroup Game-fm
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  type
     * @apiParam {long}  issue
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
    @MyLoginRequired
    public String getStatus()
    {
        long issue = WebRequest.getLong("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        FMPeriodStatus runningStatus = FMPeriodStatus.tryLoadCache(false, issue);
//        if(runningStatus == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
//            return apiJsonTemplate.toJSONString();
//        }

        FMProductInfo model = mProductInfoService.findById(false, issue);
//        FMProductStatus status = FMProductStatus.getType(model.getStatus());
        JSONObject dataMpas = FMProductListManager.convertModelToJSONObj(false, model);
        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fmApi/createOrder
     * @apiDescription  购买产品
     * @apiName createOrder
     * @apiGroup Game-fm
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  accessToken
     * @apiParam {long}  issue 产品id
     * @apiParam {long}  buyAmount 购买金额
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
    @RequestMapping("/createOrder")
    public String createOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        long issue = WebRequest.getLong("issue");
        FMType type = FMType.SIMPLE;

        long buyAmountValue = WebRequest.getLong("buyAmount");

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


        if(buyAmountValue <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(type == null || userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        // 只能会员才能领取
        if(!userInfo.getType().equalsIgnoreCase(UserInfo.UserType.MEMBER.getKey()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        FMPeriodStatus runningStatus = FMPeriodStatus.tryLoadCache(false, issue);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        FMProductInfo model = mProductInfoService.findById(false, issue);



        ErrorResult errorResult = runningStatus.verify(userInfo, buyAmountValue,issue);
        if(errorResult != SystemErrorResult.SUCCESS)
        {
            if( (errorResult== GameErrorResult.ERR_REDP_SALES_LESS_MINIMUM_SALES)
                    && (!FMProductStatus.SALED.getKey().equalsIgnoreCase(model.getStatus()))
                            &&(!FMProductStatus.REALIZED.getKey().equalsIgnoreCase(model.getStatus()))
                    ){
                mProductInfoService.updateToFinish(issue, null,  FMProductStatus.SALED);
            }
            apiJsonTemplate.setJsonResult(errorResult);
            return apiJsonTemplate.toJSONString();
        }

        if(!FMProductStatus.SALING.getKey().equalsIgnoreCase(model.getStatus()))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current product not saling or end!");
            return apiJsonTemplate.toJSONString();
        }

        // 认购限额
        if(!(buyAmountValue >= model.getLimitMinSale() && buyAmountValue <= model.getLimitMaxSale()))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Sale amount is between " + model.getLimitMinSale() + " and " + model.getLimitMaxSale());
            return apiJsonTemplate.toJSONString();
        }

        // 投注限制
        BigDecimal todayBetAmount = UserBetReportLogical.getTotalBetAmount(username);
        if(todayBetAmount.longValue() < model.getLimitMinBets())
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current product need user min bet amount: " + model.getLimitMinBalance());
            return apiJsonTemplate.toJSONString();
        }

        // 账户余额限制
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(userMoney.getBalance().compareTo(model.getLimitMinBalance()) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current product need user min balance:" + model.getLimitMinBalance());
            return apiJsonTemplate.toJSONString();
        }

        //购买金额要小于等于余额
        if( buyAmountValue>= userMoney.getBalance().floatValue() )
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        //当前用户已购买金额
        BigDecimal limitMaxSale=new BigDecimal(model.getLimitMaxSale());

        FMOrderInfo totalFMOrderInfo=mOrderService.findByNoAndUserid(false,issue,userInfo.getId());
        if(totalFMOrderInfo!=null && totalFMOrderInfo.getBuyAmount()!=null){
            if(limitMaxSale.subtract(totalFMOrderInfo.getBuyAmount()).compareTo(new BigDecimal(buyAmountValue))==-1){
                apiJsonTemplate.setJsonResult(GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT);
                return apiJsonTemplate.toJSONString();
            }
        }


        //
        boolean rs = mFMBuyTaskManager.addItemToQueue(null, model, userInfo, new BigDecimal(buyAmountValue));
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /game/fmApi/getUserBuyRecordList
     * @apiDescription  获取用户最新认购记录
     * @apiName getUserBuyRecordList
     * @apiGroup Game-fm
     * @apiVersion 1.0.0
     *
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
    @RequestMapping("/getUserBuyRecordList")
    public String getUserBuyRecordList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

//        String type = WebRequest.getString("type"); //
        int offset = WebRequest.getInt("offset", 0, 90);
        String status = WebRequest.getString("status");

        OrderTxStatus txStatus= OrderTxStatus.getType(status);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<FMOrderInfo> rsList = mOrderService.queryListByUserid(false, userInfo.getId(), txStatus,offset);

        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
        }
        else
        {
//            String amountKey = "amount";
//            String issueKey = "issue";
//            String rpType = "rpType";
//            String indexKey = "index";
//            String createtimeKey = "createtime";
//
//            List list = Lists.newArrayList();
//            for(FMOrderInfo model : rsList)
//            {
//                Map<String, Object> map = Maps.newHashMap();
//                map.put(amountKey, model.getBuyAmount());
//                map.put(issueKey, model.getFmid());
//                map.put(rpType, model.getRpType());
//                map.put(indexKey, model.getIndex());
//                map.put(indexKey, model.getIndex());
//                map.put(createtimeKey, DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//
//                list.add(map);
//            }

            apiJsonTemplate.setData(rsList);
        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/fmApi/redemptionOrder
     * @apiDescription  赎回订单
     * @apiName redemptionOrder
     * @apiGroup Game-fm
     * @apiVersion 1.0.0
     *
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
    @RequestMapping("/redemptionOrder")
    public String redemptionOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String orderno = WebRequest.getString("orderno");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        FMOrderInfo fMOrderInfo=mOrderService.findByNo(orderno);

        if(fMOrderInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(userInfo.getId() != fMOrderInfo.getUserid()){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(OrderTxStatus.getType(fMOrderInfo.getStatus()) != OrderTxStatus.WAITING){
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Order status is wrong" );
            return apiJsonTemplate.toJSONString();
        }

        DateTime nowDateTime = new DateTime();
        DateTime enndtime=new DateTime(fMOrderInfo.getEndtime());

        if(enndtime.minusMinutes(1).getMillis()-nowDateTime.getMillis()<0){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        boolean rs=mFMBuyTaskManager.doRefundOrder(fMOrderInfo,userInfo,true);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }

        return apiJsonTemplate.toJSONString();
    }


}

package com.inso.modules.game.red_package.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.inso.modules.game.GameErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.helper.RedPIDSignUtils;
import com.inso.modules.game.red_package.logical.RedPGrabStatus;
import com.inso.modules.game.red_package.logical.RedPGrabTaskManager;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPReceivOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.red_package.service.RedPReceivOrderService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;

@RestController
@RequestMapping("/game/grabRedPApi")
public class GrabRedPApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private RedPGrabTaskManager mBetConcurrent;

    @Autowired
    private RedPPeriodService mRedPackagePeriodService;

    @Autowired
    private RedPReceivOrderService mOrderService;

    /**
     * @api {post} /game/grabRedPApi/getStatus
     * @apiDescription  获取红包状态
     * @apiName getStatus
     * @apiGroup Game-redp
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
     *              "type: 红包类型
     *              "issue": "20210326170610",
     *              "startTime": 1616749560000,
     *              "endTime": 1616749670000,
     *              "countdownSeconds":红包领取倒计时
     *              "status": true
     *              "winAmountByusername": 会员领取到金额，如果有领取
     *         }
     *       }
     */
    @RequestMapping("/getStatus")
    @MyLoginRequired
    public String getStatus()
    {
//        String typeString = WebRequest.getString("type");
        String issueEncrypt = WebRequest.getString("issue");


        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
//        UserInfo userInfo = mUserService.findByUsername(false, username);

//        RedPType type = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issueEncrypt))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        long issue = RedPIDSignUtils.decrypt(issueEncrypt);
        if(issue < 1)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_RED_PACKAGE_TREASURE_BOX_EMPTY);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RedPGrabStatus runningStatus = RedPGrabStatus.tryLoadCache(false, issue);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_RED_PACKAGE_TREASURE_BOX_EMPTY);
            return apiJsonTemplate.toJSONString();
        }


        RedPPeriodInfo model = mRedPackagePeriodService.findByIssue(false, issue);
        RedPType type = RedPType.getType(model.getRpType());
        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());

        long startTime = runningStatus.getStartTime().getTime();
        long endTime = runningStatus.getEndTime().getTime();
        // 减去封盘时间
        long countdownSeconds = (endTime - System.currentTimeMillis()) / 1000 - type.getDisableSecond();
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        Map<String, Object> dataMpas = Maps.newHashMap();
        dataMpas.put("type", type.getKey());
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", runningStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("issue", runningStatus.getIssue());
        dataMpas.put("status", status.getKey());
        // 抢到红包的人
//        dataMpas.put("winList", runningStatus.getWinList());
        dataMpas.put("externalLimitMinAmount", runningStatus.getExternalLimitMinAmount());

        dataMpas.put("winAmountByusername", runningStatus.getWinAmount(username));

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /game/grabRedPApi/createOrder
     * @apiDescription  抢红包
     * @apiName createOrder
     * @apiGroup Game-redp
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  accessToken
     * @apiParam {string}  issue 红包id
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

        //String typeString = WebRequest.getString("type");

        // 加密的红包id
        String issueEncrypt = WebRequest.getString("issue");
        //RedPType type = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(issueEncrypt))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        long issue = RedPIDSignUtils.decrypt(issueEncrypt);

        if(issue < 1)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_RED_PACKAGE_TREASURE_BOX_EMPTY);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RedPGrabStatus runningStatus = RedPGrabStatus.tryLoadCache(false, issue);

        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_RED_PACKAGE_TREASURE_BOX_EMPTY);
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        ErrorResult errorResult = runningStatus.verify(username);
        if(errorResult != SystemErrorResult.SUCCESS)
        {
            if(errorResult == SystemErrorResult.ERR_CUSTOM){
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "You are not eligible!");
                return apiJsonTemplate.toJSONString();
            }
//            apiJsonTemplate.setJsonResult(GameErrorResult.ERR_RED_PACKAGE_TREASURE_BOX_EMPTY);
            apiJsonTemplate.setJsonResult(errorResult);
            return apiJsonTemplate.toJSONString();
        }

        RedPPeriodInfo model = mRedPackagePeriodService.findByIssue(false, issue);
          RedPType type =RedPType.getType(model.getRpType());
        // 红包谁能领取
        if(runningStatus.getCreatorType() == RedPCreatorType.AGENT)
        {
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            if(userAttr.getAgentid() != runningStatus.getCreatorUserid())
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "You are not in this agent!");
                return apiJsonTemplate.toJSONString();
            }
        }
        else if(runningStatus.getCreatorType() == RedPCreatorType.STAFF)
        {
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            if(userAttr.getDirectStaffid() != runningStatus.getCreatorUserid())
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "You are not in this agent !!!");
                return apiJsonTemplate.toJSONString();
            }
        }

        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
        // 只有等待完成才能操作
        if(status != GamePeriodStatus.WAITING)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
            return apiJsonTemplate.toJSONString();
        }

        //
        ErrorResult rsResult = mBetConcurrent.doCreateOrder(apiJsonTemplate, type, issue, issueEncrypt,  userInfo);
        if(SystemErrorResult.SUCCESS != rsResult)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /game/grabRedPApi/getUserReceivRecord
     * @apiDescription  获取用户最新抢到的红包记录
     * @apiName getUserReceivRecord
     * @apiGroup Game-redp
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
     *         "data":{
     *             amount:
     *             rpType:
     *             createtime
     *         }
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getUserReceivRecord")
    public String getUserReceivRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

//        String type = WebRequest.getString("type"); //
        int offset = WebRequest.getInt("offset", 0, 90);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<RedPReceivOrderInfo> rsList = mOrderService.queryListByUserid(false, userInfo.getId(), offset);

        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
        }
        else
        {
            String amountKey = "amount";
//            String issueKey = "issue";
            String rpType = "rpType";
            String indexKey = "index";
            String createtimeKey = "createtime";

            List list = Lists.newArrayList();
            for(RedPReceivOrderInfo model : rsList)
            {
                Map<String, Object> map = Maps.newHashMap();
                map.put(amountKey, model.getAmount());
//                map.put(issueKey, model.getRpid());
                map.put(rpType, model.getRpType());
                map.put(indexKey, model.getIndex());
                map.put(createtimeKey, DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));

                list.add(map);
            }

            apiJsonTemplate.setData(list);
        }
        return apiJsonTemplate.toJSONString();
    }


}

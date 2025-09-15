package com.inso.modules.passport.gift.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
import com.inso.modules.passport.gift.logical.GiftManager;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.service.GiftConfigService;
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

@RestController
@RequestMapping("/passport/giftApi")
public class GiftApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private GiftConfigService mGiftConfigService;

    @Autowired
    private GiftManager mGiftManager;


    /**
     * @api {post} /passport/giftApi/getGiftDataList
     * @apiDescription  获取礼物列表
     * @apiName getGiftDataList
     * @apiGroup passport-gift-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *         "data": {
     *             title:"",
     *             desc:"",
     *             type:
     *             status: 状态，是否可以领取状态 enable|disable
     *         }
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getGiftDataList")
    public String getGiftDataList()
    {
        // 表示如果存在才输出到前端, 对cocos-creator游戏可不用传
        String getIfExistValue = WebRequest.getString("getIfExist");
        boolean getIfExist = true;
        if(!StringUtils.isEmpty(getIfExistValue))
        {
            getIfExist = StringUtils.asBoolean(getIfExistValue);
        }

        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setData(mGiftManager.getDataList(userInfo, getIfExist));
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/receiveGift")
    public String receiveGift()
    {
        long id = WebRequest.getLong("id");
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
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

        GiftConfigInfo model = mGiftConfigService.findById(MyEnvironment.isDev(), id);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        GiftPeriodType periodType = GiftPeriodType.getType(model.getPeriodType());
        if(periodType != GiftPeriodType.Day)
        {
            // 大周期的是抽奖
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        DateTime dateTime = new DateTime();

        BigDecimal amount = GiftStatusHelper.getInstance().getAmount(periodType, dateTime, username, model.getTargetType());
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(amount.compareTo(model.getLimitAmount()) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        mGiftManager.receive(apiJsonTemplate, model, userInfo);
        return apiJsonTemplate.toJSONString();
    }
}

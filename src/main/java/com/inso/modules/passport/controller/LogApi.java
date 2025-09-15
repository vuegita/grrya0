package com.inso.modules.passport.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.analysis.logical.UserActiveStatsMgr;
import com.inso.modules.analysis.model.UserActiveStatsType;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passport/logApi")
public class LogApi {

    @Autowired
    private AuthService mOauth2Service;

    /**
     * @apiIgnore Not finished Method
     * @api {post} /passport/logApi/addUserActiveStats
     * @apiDescription  获取用户信息
     * @apiName addUserActiveStats
     * @apiGroup passport-logApi
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {String}  type
     * @apiParam {long}  duration  时长-单位秒
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
    @RequestMapping("addUserActiveStats")
    public String addUserActiveStats()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);
        if(StringUtils.isEmpty(username))
        {
            return ApiJsonTemplate.buildErrorResult(SystemErrorResult.SUCCESS);
        }

        String typeString = WebRequest.getString("type");
        UserActiveStatsType type = UserActiveStatsType.getType(typeString);
        if(type == null)
        {
            return ApiJsonTemplate.buildErrorResult(SystemErrorResult.SUCCESS);
        }

        long duration = WebRequest.getLong("duration");
        if(duration <= 0)
        {
            return ApiJsonTemplate.buildErrorResult(SystemErrorResult.SUCCESS);
        }

        UserActiveStatsMgr mgr = UserActiveStatsMgr.getInstance();
        mgr.addStats(username, type, duration);


        return ApiJsonTemplate.buildErrorResult(SystemErrorResult.SUCCESS);
    }

}

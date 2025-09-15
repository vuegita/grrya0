package com.inso.modules.game.task_checkin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.game.task_checkin.logical.TaskCheckinManger;
import com.inso.modules.game.task_checkin.model.TaskCheckinOrderInfo;
import com.inso.modules.game.task_checkin.service.TaskCheckinOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;

@RestController
@RequestMapping("/game/taskCheckinApi")
public class TaskCheckinApi {


    @Autowired
    private TaskCheckinManger mTaskCheckinManger;

    @Autowired
    private TaskCheckinOrderService mTaskCheckinOrderService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;


    /**
     * @api {post} /game/taskCheckinApi/checkin
     * @apiDescription  签到
     * @apiName submitOrder
     * @apiGroup Game-taskcheckin
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken

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
    @MyIPRateLimit(maxCount = 10, expires = 20)
    @MyLoginRequired
    @RequestMapping("/checkin")
    public String submitOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate=new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.isEnable())
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        if(!UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(userInfo.getType()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        //
        ErrorResult result = mTaskCheckinManger.doCheckin(userInfo);
        apiJsonTemplate.setJsonResult(result);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /game/taskCheckinApi/getLatestCheckinList
     * @apiDescription  签到
     * @apiName submitOrder
     * @apiGroup Game-taskcheckin
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken

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
    @MyIPRateLimit(maxCount = 10, expires = 20)
    @MyLoginRequired
    @RequestMapping("/getLatestCheckinList")
    public String getLatestCheckinList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate=new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.isEnable())
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        if(!UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(userInfo.getType()))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        //
        List<TaskCheckinOrderInfo> list= mTaskCheckinOrderService.queryListByUserid(userInfo.getId());
        apiJsonTemplate.setData(list);
        return apiJsonTemplate.toJSONString();
    }
}

package com.inso.modules.web.activity.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.service.ActivityService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/inviteFriendActivityApi")
public class InviteFriendActivityApi {


    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ActivityService mActivityService;

//    @MyIPRateLimit(maxCount = 30)
    @RequestMapping("/getInviteActivityInfo")
    public String getInviteActivityInfo()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//        ActivityInfo activityInfo = mActivityService.findLatestActive(false, ActivityBusinessType.INVITE_ACTIVITY);
//        apiJsonTemplate.setData(activityInfo);
        return apiJsonTemplate.toJSONString();
    }

//    @MyLoginRequired
    @RequestMapping("/getInviteFriendActivityStatus")
    public String getInviteFriendActivityStatus()
    {
//        String accessToken = WebRequest.getAccessToken();
//        String username = mAuthService.getAccountByAccessToken(accessToken);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        ActivityInfo activityInfo = mActivityService.findLatestActive(false, ActivityBusinessType.INVITE_ACTIVITY);
//        if(activityInfo != null)
//        {
//            DateTime fromTime = new DateTime(activityInfo.getBegintime());
//            DateTime toTime = new DateTime(activityInfo.getEndtime());
//            String cachkey = InviteFriendStatus.getCacheKey(username, fromTime, toTime, null);
//            InviteFriendStatus status = InviteFriendStatus.loadFromCacheOrDefault(cachkey);
//
//            apiJsonTemplate.setData(status);
//        }
//        else
//        {
//            apiJsonTemplate.setData(InviteFriendStatus.mDefaultStatus);
//        }
        return apiJsonTemplate.toJSONString();
    }

}

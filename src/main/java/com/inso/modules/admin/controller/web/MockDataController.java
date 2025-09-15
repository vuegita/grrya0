package com.inso.modules.admin.controller.web;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MockDataController {

    @Autowired
    private UserService mUserService;

    @RequiresPermissions("root_web_mock_data_invite_friend_present_list")
    @RequestMapping("root_web_mock_data_invite_friend_present")
    public String toPageList(Model model)
    {
        return "admin/web/mock_data/mock_data_invite_friend_present";
    }

    @RequiresPermissions("root_web_mock_data_invite_friend_present_list")
    @RequestMapping("updateMockDataInviteFriendPresentInfo")
    @ResponseBody
    public String updateMockDataInviteFriendPresentInfo(Model model)
    {
        String username = WebRequest.getString("username");

        long inviteCount = WebRequest.getLong("inviteCount");
        long rechargeCount = WebRequest.getLong("rechargeCount");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(username) || !RegexUtils.isLetterOrDigitOrBottomLine(username))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(inviteCount < 0 || rechargeCount < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.TEST)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        InviteFriendStatus status = InviteFriendStatus.loadWeekCache(username);
        status.setInviteCount(inviteCount);
        status.setRechargeCount(rechargeCount);
        status.saveTodayCache(username, PresentBusinessType.INVITE_WEEK);

        return apiJsonTemplate.toJSONString();
    }

}

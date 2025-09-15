package com.inso.modules.admin.agent.controller.passport;

import com.inso.framework.utils.*;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.logical.RelationManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserRelationService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/agent/passport")
public class UserController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserRelationService mRelationService;

    @Autowired
    private RelationManager mRelationMgr;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("toEditUserPage")
    public String toAddUserPage(Model model)
    {
        //已检查权限
        String username = WebRequest.getString("username");
        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userInfo != null)
        {

            if(userInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey())){
                if(!mAgentAuthManager.verifyAgentData(userInfo.getId())){
                    return  "admin/agent/err";
                }
            }else{
                if(!mAgentAuthManager.verifyStaffData(userInfo.getId())){
                    return  "admin/agent/err";
                }
            }


            model.addAttribute("userinfo", userInfo);
        }

        String password = RandomStringUtils.generator0_Z(16);
        model.addAttribute("password", password);

        return "admin/agent/passport/staff_edit";
    }

    @RequestMapping("/staff/page")
    public String toStaffPage(Model model)
    {
        String agentname = AgentAccountHelper.getUsername();
        UserInfo agentInfo = mUserService.findByUsername(false, agentname);

        String value = "0";
        if(agentInfo != null && agentInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
        {
            value = "1";
        }
        model.addAttribute("isAgent", value);
        return "admin/agent/passport/staff_list";
    }

    @RequestMapping("getStaffList")
    @ResponseBody
    public String getStaffList()
    {
        //已检查权限
        String ancestorUsername = WebRequest.getString("ancestorUsername");
//        String username = AgentAccountHelper.getUsername();
        UserInfo.UserType[] userTypes = {UserInfo.UserType.AGENT, UserInfo.UserType.STAFF};
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        UserInfo staffUserInfo = mUserService.findByUsername(false, ancestorUsername);
        // 查询员工现在的所有会员
        if(staffUserInfo != null)
        {

            if(!mAgentAuthManager.verifyStaffData(staffUserInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }

            RowPager<UserInfo> rowPager = mUserAttrService.querySubMemberPageScrollWithStaffid(pageVo, staffUserInfo.getId());
            template.setData(rowPager);
        }
        else
        {
            // 查询当前代理所有下级代理和员工
            RowPager<UserInfo> rowPager = mRelationService.queryScrollPage(pageVo, AgentAccountHelper.getAdminAgentid(), userTypes);
            template.setData(rowPager);
        }

        return template.toJSONString();
    }

    @RequestMapping("addStaff")
    @ResponseBody
    public String actionAddStaff()
    {

        long userid = WebRequest.getLong("id");
//        String nickname = WebRequest.getString("nickname");
        String username = WebRequest.getString("username");
        String phone = WebRequest.getString("phone");
        String email = WebRequest.getString("email");
        String password = WebRequest.getString("password");
        String remoteip = WebRequest.getRemoteIP();
        String statusString = WebRequest.getString("status");

        UserInfo.UserType userType = UserInfo.UserType.STAFF;
        Status userStatus = Status.getType(statusString);

        Status loginAgentStatus = Status.getType(WebRequest.getString("loginAgentStatus"));

        ApiJsonTemplate api = new ApiJsonTemplate();

        if(!AgentAccountHelper.isAgentLogin())
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return api.toJSONString();
        }

        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
            return api.toJSONString();
        }

        if(username.startsWith(UserInfo.DEFAULT_GAME_TEST_ACCOUNT))
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return api.toJSONString();
        }

        if (!( !StringUtils.isEmpty(phone) && phone.length() >= 8 && phone.length() <= 15 && RegexUtils.isLetterOrDigitOrBottomLine(phone))) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Phone error, Phone lenght >= 8 and Phone lenght <= 11 !");
            return api.toJSONString();
        }

        if (StringUtils.isEmpty(email) || !RegexUtils.isEmail(email)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Email error !");
            return api.toJSONString();
        }

        // 添加用户的时候
        if (userid == 0 && !ValidatorUtils.checkPassword(password)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "password error!");
            return api.toJSONString();
        }

//        if (!ValidatorUtils.checkNickname(nickname))
//        {
//            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "nickname error!");
//            return api.toJSONString();
//        }

        if(userType != UserInfo.UserType.STAFF)
        {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Please input user type!");
            return api.toJSONString();
        }

        if(UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(username))
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
        if(agentInfo == null || !agentInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userid == 0 && userInfo != null)
        {
            api.setJsonResult(SystemErrorResult.ERR_EXIST);
            return api.toJSONString();
        }

        if(userInfo == null)
        {
            // check email exist
            String tmpUsername = mUserService.findNameByEmail(email);
            if(!StringUtils.isEmpty(tmpUsername))
            {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Exist email!");
                return api.toJSONString();
            }

            // check phone exist
            tmpUsername = mUserService.findNameByPhone(phone);
            if(!StringUtils.isEmpty(tmpUsername))
            {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Exist phone!");
                return api.toJSONString();
            }

            UserInfo.UserType[] userTypes = {UserInfo.UserType.STAFF};
            PageVo pageVo = new PageVo(0, 100);
            RowPager<UserInfo> rowPager = mRelationService.queryScrollPage(pageVo, AgentAccountHelper.getAdminAgentid(), userTypes);
            if(rowPager.getTotal() > 50)
            {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "最多添加50个员工!");
                return api.toJSONString();
            }
            try {
                mUserService.addUserByThirdCoin(username, password, phone, email, userType, null, remoteip,null);

                // 如果注意类型是员工，要绑定
                bindAgentAndStaff(username, agentInfo);
            } catch (Exception e) {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Register error!");
                return api.toJSONString();
            }
        }
        else
        {
            if(!userInfo.getEmail().equalsIgnoreCase(email))
            {
                mUserService.updateEmail(username, email);
            }

//            if(!userInfo.getPhone().equalsIgnoreCase(phone))
//            {
//                mUserService.updatePhone(username, phone);
//            }

            mUserService.updateStatus(username, userStatus.getKey(), loginAgentStatus);

            if(Status.ENABLE == userStatus)
            {
                String inputErrorPwdTimesCacheKey = UserInfoCacheKeyUtils.getInputLoginPwdTimes(username);
                CacheManager.getInstance().delete(inputErrorPwdTimesCacheKey);
            }

        }

        return api.toJSONString();
    }

    private void bindAgentAndStaff(String regUsername, UserInfo agentInfo)
    {
        // check
        UserInfo.UserType agentType = UserInfo.UserType.getType(agentInfo.getType());

        if(agentType != UserInfo.UserType.AGENT)
        {
            return;
        }

        UserInfo childUserInfo = mUserService.findByUsername(false, regUsername);
        ErrorResult errorResult = mRelationMgr.moveRelation(agentInfo, childUserInfo);
        if(SystemErrorResult.SUCCESS == errorResult)
        {
            mUserAttrService.updateStaffAndAgent(childUserInfo.getId(), null, -1, agentInfo.getName(), agentInfo.getId());
        }
    }


    @RequestMapping("findUserInfo")
    @ResponseBody
    public String actionToFindUserInfo()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String username = WebRequest.getString("username");

        FundAccountType accountType = FundAccountType.getType(WebRequest.getString("fundAccountType"));
        if(accountType == null)
        {
            accountType = FundAccountType.Spot;
        }

        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));
        if(currencyType == null)
        {
            currencyType = ICurrencyType.getSupportCurrency();
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        userInfo.setBalance(userMoney.getBalance());

        apiJsonTemplate.setData(userInfo);
        return apiJsonTemplate.toJSONString();
    }

}

package com.inso.modules.admin.controller.passport;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.google.GoogleImageUtils;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.admin.agent.AgentLoginHelper;
import com.inso.modules.admin.config.shiro.ShiroRealm;
import com.inso.modules.admin.core.CoreSafeManager;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.logical.RelationManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserController {

    private static Log LOG = LogFactory.getLog(UserController.class);

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserRelationService mUserRelationService;

    @Autowired
    private MoneyOrderService moneyOrderService;

    @Autowired
    private RelationManager mRelationMgr;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AdminService adminService;

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("toEditUserPage")
    public String toAddUserPage(Model model)
    {
        String username = WebRequest.getString("username");
        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo != null)
        {
            model.addAttribute("userinfo", userInfo);
        }
        return "admin/passport/user_edit";
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("addUser")
    @ResponseBody
    public String actionAddUser()
    {
        long userid = WebRequest.getLong("id");
//        String nickname = WebRequest.getString("nickname");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String phone = WebRequest.getString("phone");
        String email = WebRequest.getString("email");
        String password = WebRequest.getString("password");
        String remoteip = WebRequest.getRemoteIP();
        String userTypeString = WebRequest.getString("type");
        String statusString = WebRequest.getString("status");
        String memberSubTypeString = WebRequest.getString("subType");

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeString);
        Status userStatus = Status.getType(statusString);

        Status loginAgentStatus = Status.getType(WebRequest.getString("loginAgentStatus"));

        ApiJsonTemplate api = new ApiJsonTemplate();
        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
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

        if(UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(username))
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

//        if (!ValidatorUtils.checkNickname(nickname))
//        {
//            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "nickname error!");
//            return api.toJSONString();
//        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userid == 0 && userInfo != null)
        {
            api.setJsonResult(SystemErrorResult.ERR_EXIST);
            return api.toJSONString();
        }

        if(userInfo == null)
        {
            // 注册
            if(userType == null)
            {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Please input user type!");
                return api.toJSONString();
            }


            if (!( !StringUtils.isEmpty(phone) && phone.length() >= 8 && phone.length() <= 15 && RegexUtils.isDigit(phone))) {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Phone error, Phone lenght >= 8 and Phone lenght <= 11 !");
                return api.toJSONString();
            }

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


            UserInfo agentInfo = mUserService.findByUsername(false, agentname);
            if(StringUtils.isEmpty(agentname))
            {
                agentInfo = mUserService.findByUsername(false, agentname);
            }

            // 创建用户为员工，需要判断是不是代理
            if(userType == UserInfo.UserType.STAFF && (agentInfo == null || !agentInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey())))
            {
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Agent is empty!");
                return api.toJSONString();
            }

            try {
                password = RandomStringUtils.generator0_Z(16);

                mUserService.addUserByThirdCoin(username, password, phone, email, userType, null, remoteip,null);

                if(userType == UserInfo.UserType.STAFF)
                {
                    // 如果注意类型是员工，要绑定
                    bindAgentAndStaff(username, agentInfo);
                }
            } catch (Exception e) {
                LOG.error("register error:", e);
                api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Register error!");
                return api.toJSONString();
            }
        }
        else
        {
            // 更新
            if(!userInfo.getEmail().equalsIgnoreCase(email))
            {
                mUserService.updateEmail(username, email);
            }

            // 手机号不能更新
//            if(!userInfo.getPhone().equalsIgnoreCase(phone))
//            {
//                mUserService.updatePhone(username, phone);
//            }

            mUserService.updateStatus(username, userStatus.getKey(), loginAgentStatus);

            if(userInfo.getType().equalsIgnoreCase(userTypeString) && userType == UserInfo.UserType.MEMBER)
            {
                // 只有会员才能设置这个
                MemberSubType subType = MemberSubType.getType(memberSubTypeString);
                MemberSubType dbSubType = MemberSubType.getType(userInfo.getSubType());
                if(dbSubType == MemberSubType.SIMPLE || dbSubType == MemberSubType.PROMOTION)
                {
                    if((subType == MemberSubType.SIMPLE || subType == MemberSubType.PROMOTION) && dbSubType != subType)
                    {
                        mUserService.updateSubType(username, subType);
                    }
                }
            }


            if(Status.ENABLE == userStatus)
            {
                String inputErrorPwdTimesCacheKey = UserInfoCacheKeyUtils.getInputLoginPwdTimes(username);
                CacheManager.getInstance().delete(inputErrorPwdTimesCacheKey);

                String disableUserLoginCacheKey = UserInfoCacheKeyUtils.disableUserLogin(username);
                CacheManager.getInstance().delete(disableUserLoginCacheKey);
            }

            else if(userStatus == Status.DISABLE)
            {
                // 如果是代理或员工，并且有登陆代理后台，则直接踢出
                ShiroRealm.stickAgent(username);

                String loginToken = mAuthService.createLoginTokenByAccount(username, "679868yjgjgj", false);
                mAuthService.refreshAccessToken(loginToken, false);
            }

        }

        return api.toJSONString();
    }


    @RequiresPermissions("root_passport_member_list")
    @RequestMapping("root_passport_member")
    public String toUserListPage(Model model)
    {

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());
        SystemRunningMode.addModel(model);
        return "admin/passport/user_list";
    }

    @RequiresPermissions("root_passport_member_list")
    @RequestMapping("getUserList")
    @ResponseBody
    public String getUserList()
    {
        String time = WebRequest.getString("time");
        String userTypeStr = WebRequest.getString("type");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String inviteCode = WebRequest.getString("inviteCode");


        String from = WebRequest.getString("from"); // agent | member
        String ancestorUsername = WebRequest.getString("ancestorUsername");

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeStr);

        if(StringUtils.isEmpty(ancestorUsername))
        {
            pageVo.parseTime(time);

            long userid = mUserQueryManager.findUserid(username);
            long agentid = mUserQueryManager.findUserid(agentname);
            long staffid = mUserQueryManager.findUserid(staffname);
            RowPager<UserInfo> rowPager = mUserService.queryScrollPage(pageVo, userid, userType ,username,agentid,staffid, inviteCode);
            template.setData(rowPager);
        }
        else
        {
            UserInfo.UserType[] userTypes = null;
//            if(!"member".equalsIgnoreCase(from))
//            {
//                if(userType != null)
//                {
//                    userTypes = new UserInfo.UserType[1];
//                    userTypes[0] = userType;
//                }
//                else
//                {
//                    userTypes = new UserInfo.UserType[2];
//                    userTypes[0] = UserInfo.UserType.AGENT;
//                    userTypes[1] = UserInfo.UserType.STAFF;
//                }
//            }

            if(userType != null)
            {
                userTypes = new UserInfo.UserType[1];
                userTypes[0] = userType;
            }

            UserInfo ancestorUserInfo = mUserService.findByUsername(false, ancestorUsername);


            RowPager<UserInfo> rowPager = mUserRelationService.queryScrollPage(pageVo, ancestorUserInfo.getId(), userTypes);
            template.setData(rowPager);
        }
        return template.toJSONString();
    }

    @RequestMapping("getSystemConfig")
    @ResponseBody
    public String getSystemConfig()
    {
        String inviteCode = WebRequest.getString("inviteCode");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(inviteCode == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        SystemRunningMode modl= SystemRunningMode.getSystemConfig();
        Map<String, Object> map = new HashMap<>();
        if(modl==SystemRunningMode.CRYPTO){
            map.put("shareUrl", "/mining/defi?inviteCode="+inviteCode);
        }else if(modl==SystemRunningMode.BC){
            map.put("shareUrl", "/mining/#/register?inviteCode="+inviteCode);
        }else if(modl==SystemRunningMode.FUNDS){
            map.put("shareUrl", "/#/register?inviteCode="+inviteCode);
        }

        apiJsonTemplate.setData(map);

        return apiJsonTemplate.toJSONString();

    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("updateUserType")
    @ResponseBody
    public String actionToUpdateUserType()
    {
        String username = WebRequest.getString("username");
        String userTypeString = WebRequest.getString("userType");

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(userType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        mUserService.updateUserType(username, userType);
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("getParentUserInfo")
    @ResponseBody
    public String actionGetParentUserInfo()
    {
        String childUsername = WebRequest.getString("childUsername");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo childUserInfo = mUserService.findByUsername(false, childUsername);
        if(childUsername == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        // 查询直属上级, 0表示自己
        UserAttr attr = mUserAttrService.find(false, childUserInfo.getId());
        apiJsonTemplate.setData(attr);
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("updateUserRelation")
    @ResponseBody
    public String actionToUpdateUserRelation()
    {
        String parentUsername = WebRequest.getString("parentUsername");
        String childUsername = WebRequest.getString("childUsername");
        String remoteip = WebRequest.getRemoteIP();

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        if(SystemRunningMode.isCryptoMode())
//        {
//            if(!(AdminAccountHelper.isSupperAdmin() || WhiteIPManager.getInstance().verify(remoteip)))
//            {
//                // 没有权限，只有超级管理员才能操作
//                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//                return apiJsonTemplate.toJSONString();
//            }
//        }

        UserInfo parentUserInfo = mUserService.findByUsername(false, parentUsername);
        if(parentUserInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!parentUserInfo.isEnable())
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo childUserInfo = mUserService.findByUsername(false, childUsername);
        if(childUserInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!childUserInfo.isEnable())
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType parentUserType = UserInfo.UserType.getType(parentUserInfo.getType());
        // 上级只能是员工
        if(parentUserType != UserInfo.UserType.STAFF)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType childUserType = UserInfo.UserType.getType(childUserInfo.getType());

        // 会员有上级，并且有数据，无法变更上级
//        if(childUserType == UserInfo.UserType.MEMBER)
//        {
//            UserAttr userAttr = mUserAttrService.find(false, childUserInfo.getId());
//            if(userAttr.getDirectStaffid() > 0 && moneyOrderService.countByUserid(childUserInfo.getId()) > 0)
//            {
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前下级已产生数据，无法变更上级");
//                return apiJsonTemplate.toJSONString();
//            }
//        }

        UserAttr parentAttr = mUserAttrService.find(false, parentUserInfo.getId());

        ErrorResult errorResult = SystemErrorResult.SUCCESS;
        // 代理下级只能是员工
        if(parentUserType == UserInfo.UserType.AGENT)
        {
            //
//            if(childUserType != UserInfo.UserType.STAFF)
//            {
//                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
//                return apiJsonTemplate.toJSONString();
//            }
//            errorResult = mRelationMgr.moveRelation(parentUserInfo, childUserInfo);
//            if(SystemErrorResult.SUCCESS == errorResult)
//            {
//                mUserAttrService.updateStaffAndAgent(childUserInfo.getId(), null, -1, parentUserInfo.getName(), parentUserInfo.getId());
//            }
        }
        // 员工下级只能是员工|会员
        else if(parentUserType == UserInfo.UserType.STAFF)
        {
            if(childUserType == UserInfo.UserType.MEMBER)
            {
                mRelationMgr.updateMemberStaffRelation(parentAttr, childUserInfo);
                //mUserAttrService.updateStaffAndAgent(childUserInfo.getId(), parentUserInfo.getName(), parentUserInfo.getId(), parentAttr.getAgentname(), parentAttr.getAgentid());
            }
//            else if(childUserType == UserInfo.UserType.STAFF)
//            {
//                errorResult = mRelationMgr.moveRelation(parentUserInfo, childUserInfo);
//                if(SystemErrorResult.SUCCESS == errorResult)
//                {
//                    mUserAttrService.updateStaffAndAgent(childUserInfo.getId(), null, -1, parentAttr.getAgentname(), parentAttr.getAgentid());
//                }
//            }
            else
            {
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
                return apiJsonTemplate.toJSONString();
            }
        }
        // 会员下级只能会员
//        else if(parentUserType == UserInfo.UserType.MEMBER)
//        {
//            mUserAttrService.bindAncestorInfo(childUserInfo.getId(), childUserInfo.getName(),
//                    parentAttr.getDirectStaffid(), parentAttr.getDirectStaffname(),
//                    parentAttr.getUserid(), parentAttr.getUsername(),
//                    parentAttr.getParentid(), parentAttr.getParentname(),
//                    parentAttr.getAgentname(), parentAttr.getAgentid());
//        }
        else
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setJsonResult(errorResult);

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("updateUserStatus")
    @ResponseBody
    public String actionToUpdateUserStatus()
    {
        String username = WebRequest.getString("username");
        String statusString = WebRequest.getString("statusString");


        Status status = Status.getType(statusString);


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null || status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        mUserService.updateStatus(username, statusString, null);

        if(Status.ENABLE == status)
        {
            String inputErrorPwdTimesCacheKey = UserInfoCacheKeyUtils.getInputLoginPwdTimes(username);
            CacheManager.getInstance().delete(inputErrorPwdTimesCacheKey);
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("updateUserPassword")
    @ResponseBody
    public String actionToUpdateUserPassword()
    {
        String username = WebRequest.getString("username");
        String password = WebRequest.getString("password");
        String paypwd = WebRequest.getString("paypwd");
        boolean googleCode = WebRequest.getBoolean("googleCode");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserSecret secretInfo = mUserSecretService.find(false, username);
        if(secretInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if (!StringUtils.isEmpty(password) && ValidatorUtils.checkPassword(password)) {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "password error!");
//            return apiJsonTemplate.toJSONString();
            mUserSecretService.updateLoginPwd(username, password);
        }

        if (!StringUtils.isEmpty(paypwd) && ValidatorUtils.checkPassword(paypwd)) {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "paypwd error!");
//            return apiJsonTemplate.toJSONString();
            mUserSecretService.updatePaypwd(username, paypwd);
        }

        if(googleCode)
        {
            UserInfo userInfo = mUserQueryManager.findUserInfo(username);
            GoogleStatus googleStatus = null;
            if(UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(userInfo.getType()))
            {
                googleStatus = GoogleStatus.UNBIND;
            }
            String googlekey = GoogleAuthenticator.generateSecretKey();
            mUserSecretService.updateGoogleInfo(username, googleStatus, googlekey);
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("agentUpdateUserPassword")
    @ResponseBody
    public String agentUpdateUserPassword()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();
        String username = WebRequest.getString("username");
        String password = WebRequest.getString("password");
        String paypwd = WebRequest.getString("paypwd");
        boolean googleCode = WebRequest.getBoolean("googleCode");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        long userid = mUserQueryManager.findUserid(username);
        UserAttr UserAttr = mUserAttrService.find(false,userid);
        if(agentid <= 0 || UserAttr.getAgentid()!=agentid)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        UserSecret secretInfo = mUserSecretService.find(false, username);
        if(secretInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if (!StringUtils.isEmpty(password) && ValidatorUtils.checkPassword(password)) {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "password error!");
//            return apiJsonTemplate.toJSONString();
            mUserSecretService.updateLoginPwd(username, password);
        }

        if (!StringUtils.isEmpty(paypwd) && ValidatorUtils.checkPassword(paypwd)) {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "paypwd error!");
//            return apiJsonTemplate.toJSONString();
            mUserSecretService.updatePaypwd(username, paypwd);
        }

        if(googleCode)
        {
            String googlekey = GoogleAuthenticator.generateSecretKey();
            mUserSecretService.updateGoogleInfo(username, null, googlekey);
        }

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_passport_member_list")
    @RequestMapping("findUserInfo")
    @ResponseBody
    public String actionToFindUserInfo()
    {
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

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        userInfo.setBalance(userMoney.getBalance());

        apiJsonTemplate.setData(userInfo);
        return apiJsonTemplate.toJSONString();
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

    @RequiresPermissions("root_passport_member_list")
    @RequestMapping("/passport/getGoogleKeyEWM")
    @ResponseBody
    public void getGoogleKeyEWM(HttpServletResponse response) {
        boolean success = false;
        String username = WebRequest.getString("username");
        String remoteip = WebRequest.getRemoteIP();
        try {

            if(SystemRunningMode.isCryptoMode())
            {
                boolean rs = CoreSafeManager.getInstance().verifyByWeb(false);
                if(!rs)
                {
                    return;
                }

                if(CoreSafeManager.getInstance().verifyWhiteIPLogin() && !WhiteIPManager.getInstance().verify(remoteip))
                {
                    return;
                }
            }
            else
            {
                String googleCode = WebRequest.getString("googleCode");
                if(StringUtils.isEmpty(username) || StringUtils.isEmpty(googleCode))
                {
                    return;
                }

                AdminSecret adminSecret = adminService.findAdminSecretByID(AdminAccountHelper.getAdmin().getAccount());
                if(!adminSecret.checkGoogleCode(googleCode))
                {
                    return;
                }
            }



            UserSecret secret = mUserSecretService.find(false, username);
            if(secret == null)
            {
                return;
            }
            MyConfiguration conf = MyConfiguration.getInstance();
            String projectName = conf.getString("project.name");

            String name = username + "@" + projectName  +"-" + MyEnvironment.getEnv();
            String googleKey = secret.getGoogleKey();
            GoogleImageUtils.getGoogleKeyEWM(response, name, googleKey);
            success = true;
        } finally {
            EventLogManager.getInstance().addAdminLog(WebEventLogType.PASSPORT_READ_GOOGLE_KEY, "username = " + username + ", result = " + success);
        }
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping(value = "createAgentAccessKey", method = RequestMethod.POST)
    @ResponseBody
    public String createAgentAccessKey() {

        String username = WebRequest.getString("username");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(username))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String accessKey = AgentLoginHelper.createAgentAccessKey(username);
        apiJsonTemplate.setData(accessKey);
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("updateUserToTest")
    @ResponseBody
    public String actionUpdateUserToTest()
    {
        String remoteip = WebRequest.getRemoteIP();
        String username = WebRequest.getString("username");
        ApiJsonTemplate api = new ApiJsonTemplate();

        if(SystemRunningMode.isCryptoMode() && !WhiteIPManager.getInstance().verify(remoteip))
        {
            api.setJsonResult(SystemErrorResult.SUCCESS);
            return api.toJSONString();
        }

        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
            return api.toJSONString();
        }

        if(UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(username))
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return api.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return api.toJSONString();
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.MEMBER))
        {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "角色异常, 只能会员才能变为测试!");
            return api.toJSONString();
        }

        // 只有会员才能设置这个
        mUserService.updateUserType(username, UserInfo.UserType.TEST);
        return api.toJSONString();
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("toEditUserAttrPage")
    public String toEditUserAttrPage(Model model)
    {
        String username = WebRequest.getString("username");
        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            return null;
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.MEMBER || userType == UserInfo.UserType.TEST))
        {
            return null;
        }

        UserAttr userAttr = mUserAttrService.find(true, userInfo.getId());
        model.addAttribute("entity", userAttr);
        return "admin/passport/user_attr_edit";
    }

    @RequiresPermissions("root_passport_member_edit")
    @RequestMapping("editUserAttr")
    @ResponseBody
    public String editUserAttrPage(Model model)
    {
        long userid = WebRequest.getLong("id");

        Status returnLevelStatus = Status.getType(WebRequest.getString("returnLevelStatus"));
        BigDecimal returnLv1Rate = WebRequest.getBigDecimal("returnLv1Rate");
        BigDecimal returnLv2Rate = WebRequest.getBigDecimal("returnLv2Rate");
        BigDecimal receivLv1Rate = WebRequest.getBigDecimal("receivLv1Rate");
        BigDecimal receivLv2Rate = WebRequest.getBigDecimal("receivLv2Rate");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(returnLevelStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(returnLv1Rate == null || returnLv2Rate == null || receivLv1Rate == null || receivLv2Rate == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(returnLv1Rate.compareTo(BigDecimal.ZERO) < 0 || returnLv1Rate.compareTo(BigDecimal.ONE) > 0)
        {
            apiJsonTemplate.setError(-1, "返佣比例设置范围 0 <= X <= 1, 0表示关闭不赠送");
            return apiJsonTemplate.toJSONString();
        }

        if(returnLv2Rate.compareTo(BigDecimal.ZERO) < 0 || returnLv2Rate.compareTo(BigDecimal.ONE) > 0)
        {
            apiJsonTemplate.setError(-1, "返佣比例设置范围 0 <= X <= 1, 0表示关闭不赠送");
            return apiJsonTemplate.toJSONString();
        }

        if(receivLv1Rate.compareTo(BigDecimal.ZERO) < 0 || receivLv1Rate.compareTo(BigDecimal.ONE) > 0)
        {
            apiJsonTemplate.setError(-1, "接受比例设置范围 0 <= X <= 1, 0表示关闭不赠送");
            return apiJsonTemplate.toJSONString();
        }

        if(receivLv2Rate.compareTo(BigDecimal.ZERO) < 0 || receivLv2Rate.compareTo(BigDecimal.ONE) > 0)
        {
            apiJsonTemplate.setError(-1, "接受比例设置范围 0 <= X <= 1, 0表示关闭不赠送");
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userid);
        if(userAttr == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        mUserAttrService.updateReturn(userid, returnLv1Rate, returnLv2Rate, returnLevelStatus, receivLv1Rate, receivLv2Rate);

        return apiJsonTemplate.toJSONString();


    }
}

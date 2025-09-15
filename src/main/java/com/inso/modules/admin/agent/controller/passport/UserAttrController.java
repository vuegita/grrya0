package com.inso.modules.admin.agent.controller.passport;


import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.logical.RelationManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.web.SystemRunningMode;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;

import java.math.BigDecimal;


@Controller
@RequestMapping("/alibaba888/agent/passport")
public class UserAttrController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private RelationManager mRelationMgr;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @Autowired
    private UserQueryManager mUserQueryManager;


    @RequestMapping("/member_addr/page")
    public String toPage(Model model)
    {
        model.addAttribute("isAgent", AgentAccountHelper.isAgentLogin());

        boolean showSettingToTest = SystemRunningMode.isCryptoMode();
        model.addAttribute("showSettingToTest", showSettingToTest + StringUtils.getEmpty());

        return "admin/agent/passport/attr_list";
    }


    @RequestMapping("/member_addr/page2")
    public String toPage2(Model model)
    {
        model.addAttribute("isAgent", AgentAccountHelper.isAgentLogin());

        boolean showSettingToTest = SystemRunningMode.isCryptoMode();
        model.addAttribute("showSettingToTest", showSettingToTest + StringUtils.getEmpty());

        return "admin/agent/passport/attr_query_user_list";
    }

    @RequestMapping("getUserSystemStaffAttrList")
    @ResponseBody
    public String getUserSystemStaffAttrList()
    {
        //已检查权限
        String sortName = WebRequest.getString("sort");
        String sortOrder = WebRequest.getString("order");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");


        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = 0;
        long agentid = AgentAccountHelper.getAdminAgentid();

        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        agentid = mUserQueryManager.findUserid(UserInfo.DEFAULT_GAME_SYSTEM_AGENT);
        long staffid = 0;
        long parentid = 0;
        long granttid = 0;


        if(username==null){
            username = "c_"+WebRequest.getString("address");
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo != null  )
        {
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            if(userAttr.getAgentid() == agentid){
                userid = userInfo.getId();
            }else{
                template.setJsonResult(SystemErrorResult.ERR_USER_EXIST_AGENT);
                return template.toJSONString();
            }

        }else{
            template.setJsonResult(SystemErrorResult.ERR_USER_NO_EXIST);
            return template.toJSONString();
        }



        RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, granttid, null, sortName,sortOrder,null,null);
        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("getUserAttrList")
    @ResponseBody
    public String getUserAttrList()
    {
        //已检查权限
        String sortName = WebRequest.getString("sort");
        String sortOrder = WebRequest.getString("order");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");
        String parentname = WebRequest.getString("parentname");
        String grantname = WebRequest.getString("grantname");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = 0;
        long agentid = AgentAccountHelper.getAdminAgentid();
        long staffid = 0;
        long parentid = 0;
        long granttid = 0;

        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

//        if(ValidatorUtils.checkUsername(username))
//        {
//            UserInfo userInfo = mUserService.findByUsername(false, username);
//            if(userInfo != null)
//            {
//                userid = userInfo.getId();
//            }
//        }
//        else
            if(ValidatorUtils.checkUsername(parentname))
        {
            UserInfo userInfo = mUserService.findByUsername(false, parentname);
            if(userInfo != null)
            {
                parentid = userInfo.getId();
            }
        }
        else if(ValidatorUtils.checkUsername(grantname))
        {
            UserInfo userInfo = mUserService.findByUsername(false, grantname);
            if(userInfo != null)
            {
                granttid = userInfo.getId();
            }
        }else{
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo != null)
            {
                userid = userInfo.getId();

            }
       }

        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffid = loginUserInfo.getId();
        }
        // 代理查询下级员工
        else if(ValidatorUtils.checkUsername(staffname))
        {
            UserInfo userInfo = mUserService.findByUsername(false, staffname);
            if(userInfo != null)
            {
                staffid = userInfo.getId();
            }
        }

        //RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPage(pageVo, userid, agentid, staffid, parentid, granttid);
        RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, granttid, null, sortName,sortOrder,username,null);
        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("getParentUserInfo")
    @ResponseBody
    public String actionGetParentUserInfo()
    {
        //已检查权限
        String childUsername = WebRequest.getString("childUsername");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo childUserInfo = mUserService.findByUsername(false, childUsername);
        if(childUsername == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(childUserInfo.getId())){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        // 查询直属上级, 0表示自己
        UserAttr attr = mUserAttrService.find(false, childUserInfo.getId());
        apiJsonTemplate.setData(attr);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("updateUserRelation")
    @ResponseBody
    public String actionToUpdateUserRelation()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String parentUsername = WebRequest.getString("parentUsername");
        String childUsername = WebRequest.getString("childUsername");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

//        if(!AdminAccountHelper.isSupperAdmin())
//        {
//            // 没有权限，只有超级管理员才能操作
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
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

        UserInfo agentInfo = AgentAccountHelper.getAgentInfo();
        UserAttr childUserAttr = mUserAttrService.find(false, childUserInfo.getId());

       if(agentInfo.getId()!=childUserAttr.getAgentid()){
           //没有权限，只有超级管理员才能操作
           apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
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


        UserAttr parentAttr = mUserAttrService.find(false, parentUserInfo.getId());

        ErrorResult errorResult = SystemErrorResult.SUCCESS;
        // 代理下级只能是员工
        if(parentUserType == UserInfo.UserType.AGENT)
        {

        }
        // 员工下级只能是员工|会员
        else if(parentUserType == UserInfo.UserType.STAFF)
        {
            if(childUserType == UserInfo.UserType.MEMBER)
            {
                mRelationMgr.updateMemberStaffRelation(parentAttr, childUserInfo);
            }

            else
            {
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
                return apiJsonTemplate.toJSONString();
            }
        }

        else
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setJsonResult(errorResult);

        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("getParentAgentUserInfo")
    @ResponseBody
    public String actionGetParentAgentUserInfo()
    {
        //已检查权限
        String childUsername = WebRequest.getString("childUsername");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo childUserInfo = mUserService.findByUsername(false, childUsername);
        if(childUsername == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        long agentid = AgentAccountHelper.getAdminAgentid();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }


        UserInfo userInfo = mUserService.findByUsername(false,  UserInfo.DEFAULT_GAME_SYSTEM_AGENT);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, childUserInfo.getId());

        if(userAttr.getAgentid()!= userInfo.getId()){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


//        if(!mAgentAuthManager.verifyUserData(childUserInfo.getId())){
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        // 查询直属上级, 0表示自己
        UserAttr attr = mUserAttrService.find(false, childUserInfo.getId());
        apiJsonTemplate.setData(attr);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("updateUserAgentRelation")
    @ResponseBody
    public String actionToUpdateUserAgentRelation()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String parentUsername = WebRequest.getString("parentUsername");
        String childUsername = WebRequest.getString("childUsername");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

//        if(!AdminAccountHelper.isSupperAdmin())
//        {
//            // 没有权限，只有超级管理员才能操作
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
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

        UserAttr parentUserAttr = mUserAttrService.find(false, parentUserInfo.getId());
        UserInfo loginAgentInfo = AgentAccountHelper.getAgentInfo();

        if(loginAgentInfo.getId() != parentUserAttr.getAgentid()){

            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
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

        //UserInfo agentInfo = AgentAccountHelper.getAgentInfo();

        UserInfo agentInfo = mUserService.findByUsername(false,  UserInfo.DEFAULT_GAME_SYSTEM_AGENT);
        if(agentInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr childUserAttr = mUserAttrService.find(false, childUserInfo.getId());

        if(agentInfo.getId() != childUserAttr.getAgentid()){
            //没有权限，只有超级管理员才能操作
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
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


        UserAttr parentAttr = mUserAttrService.find(false, parentUserInfo.getId());

        ErrorResult errorResult = SystemErrorResult.SUCCESS;
        // 代理下级只能是员工
        if(parentUserType == UserInfo.UserType.AGENT)
        {

        }
        // 员工下级只能是员工|会员
        else if(parentUserType == UserInfo.UserType.STAFF)
        {
            if(childUserType == UserInfo.UserType.MEMBER)
            {
                mRelationMgr.updateMemberStaffRelation(parentAttr, childUserInfo);
            }

            else
            {
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
                return apiJsonTemplate.toJSONString();
            }
        }

        else
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_RELATION_ROLE);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setJsonResult(errorResult);

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("updateUserToTest")
    @ResponseBody
    public String actionUpdateUserToTest()
    {
        String username = WebRequest.getString("username");
        ApiJsonTemplate api = new ApiJsonTemplate();

        if(!SystemRunningMode.isCryptoMode())
        {
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

        if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
            api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
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

    @RequestMapping("toUpdateUserAttrCodeAmountPage")
    public String toUpdateUserAttrCodeAmountPage(Model model)
    {
        String username = WebRequest.getString("username");
        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userInfo == null)
        {
            return null;
        }

        if(!mAgentAuthManager.verifyUserData(userInfo.getId()))
        {
            return null;
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);

        model.addAttribute("moneyInfo", userMoney);
        model.addAttribute("username", username);

        model.addAttribute("userInfo", userInfo);

        return "admin/agent/passport/user_attr_update_code_amount_freeze";
    }

    @RequestMapping("updateUserAttrCodeAmountAndFreezeAmount")
    @ResponseBody
    public String actionToUpdateUserAttrCodeAmountAndFreezeAmount()
    {
        String username = WebRequest.getString("username");
        BigDecimal codeAmount = WebRequest.getBigDecimal("codeAmount");
        BigDecimal limitCode = WebRequest.getBigDecimal("limitCode");
        BigDecimal freezeAmount = WebRequest.getBigDecimal("freezeAmount");

        MemberSubType subType = MemberSubType.getType(WebRequest.getString("subType"));;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!AgentAccountHelper.isAgentLogin())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(codeAmount == null || freezeAmount == null || subType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(codeAmount.compareTo(BigDecimal.ZERO) < 0 && freezeAmount.compareTo(BigDecimal.ZERO) < 0)
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

        if(!mAgentAuthManager.verifyUserData(userInfo.getId()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mUserMoneyService.updateCodeAmount(userInfo.getId(), accountType, limitCode, currencyType, codeAmount, freezeAmount);

        if(subType == MemberSubType.PROMOTION)
        {
            mUserService.updateSubType(username, subType);
        }
        return apiJsonTemplate.toJSONString();
    }

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

        if(!mAgentAuthManager.verifyUserData(userInfo.getId()))
        {
            return null;
        }

        UserAttr userAttr = mUserAttrService.find(true, userInfo.getId());
        model.addAttribute("entity", userAttr);
        return "admin/agent/passport/user_attr_edit";
    }

    @RequestMapping("editUserAttr")
    @ResponseBody
    public String editUserAttrPage()
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

        if(!mAgentAuthManager.verifyUserData(userid))
        {
            return null;
        }

        mUserAttrService.updateReturn(userid, returnLv1Rate, returnLv2Rate, returnLevelStatus, receivLv1Rate, receivLv2Rate);

        return apiJsonTemplate.toJSONString();


    }
}
